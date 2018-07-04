package com.github.leoluheng.blog.service;

import com.github.leoluheng.blog.config.Config;
import com.github.leoluheng.blog.entity.CommentAdder;
import com.github.leoluheng.blog.entity.ContentAdder;
import com.github.leoluheng.blog.entity.NotificationAdder;
import com.github.leoluheng.blog.entity.UserAdder;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserService {

    private static UserService instance;

    private UserService() {
    }

    public synchronized static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public int verifyLogin(String username, String password) {
        List<UserAdder> userSheet = UserAdder.dao.find("select username from vmaig_auth_vmaiguser where " +
                "username = ?  AND  password = ?", username, HashKit.md5(password));
        if(userSheet.size() == 0){
            userSheet = UserAdder.dao.find("select username from vmaig_auth_vmaiguser where " +
                    "username = ?  ", username);
            if(userSheet.size() == 0){
                return 2;
            }
            return 1;
        }
        Date latestLogin = new Date();
        DateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int id = getId(username);
        UserAdder user = UserAdder.dao.findById(id);
        user.set("is_active", 1).set("last_login", simpleformat.format(latestLogin)).update();

        CacheKit.put(Config.CACHE_BLOG, "isActive-"+id,"1");

        return 0;
    }

    //Acquiring first_name last_name
    public String doReg(String username, String password, String first_name, String last_name, String email, String date_joined) {
        UserAdder userSheet = UserAdder.dao.findFirst("select username email from vmaig_auth_vmaiguser where " +
                "username = ?", username);
        if (null != userSheet.get("username")) {
            return "username";
        } else if (null != userSheet.get("email")) {
            return "email";
        }

        CacheKit.remove(Config.CACHE_BLOG,"userList");

        new UserAdder().set("username", username)
                .set("password", HashKit.md5(password)).set("first_name", first_name).set("last_name", last_name)
                .set("email", email).set("date_joined", date_joined).set("is_active", 1).set("last_login", date_joined)
                .save();
        return "true";
    }

    //No first_name last_name
    public String doReg(String username, String password, String email, String date_joined) {
        UserAdder userSheet = UserAdder.dao.findFirst("select username email from vmaig_auth_vmaiguser where " +
                "username = ? OR email = ?", username, email);
        if (null != userSheet) {
            userSheet = UserAdder.dao.findFirst("select email from vmaig_auth_vmaiguser where " +
                    "username = ?", username);
            if(null != userSheet){
                return "username";
            }
            return "email";
        }

        CacheKit.remove(Config.CACHE_BLOG,"userList");

        new UserAdder().set("username", username)
                .set("password", HashKit.md5(password)).set("email", email)
                .set("date_joined", date_joined).set("is_active", 1).set("last_login", date_joined).save();
        return "true";
    }

    public int getId(String username) {
        UserAdder userSheet = UserAdder.dao.findFirst("select id from vmaig_auth_vmaiguser where username = ?", username);
        if(userSheet == null){
            return -1;
        }

        return userSheet.get("id");
    }

    public boolean changePass(int id, String credential, String newPassword) {
        UserAdder userSheet = UserAdder.dao.findById(id);
        if (userSheet == null || !credential.equals(userSheet.get("password"))) {
            return false;
        }
        return userSheet.set("password", HashKit.md5(newPassword)).update();
    }

    public boolean verifyUser(String username, String email) {
        UserAdder userSheet = UserAdder.dao.findFirstByCache(Config.CACHE_BLOG,"userList","select username from vmaig_auth_vmaiguser where username = ? AND" +
                "email = ?", username, email);
        if (null != userSheet) {
            return true;
        }
        return false;
    }

    public void resetPassword(String recipientAddress, HttpServletRequest request) throws Exception {
        String senderAddress = "xxx@163.com";
        String senderPassword = "";
        String sender = "Blog_Manager";
        UserAdder userSheet = new UserAdder().dao().findFirst("select id from vmaig_auth_vmaiguser where email = ?", recipientAddress);

        //1、连接邮件服务器的参数配置
        Properties props = new Properties();
        //设置用户的认证方式
        props.setProperty("mail.smtp.auth", PropKit.get("mail.smtp.auth"));
        //设置传输协议
        props.setProperty("mail.transport.protocol", PropKit.get("mail.transport.protocol"));
        //设置发件人的SMTP服务器地址
        props.setProperty("mail.smtp.host", PropKit.get("mail.smtp.host"));


        //2、创建定义整个应用程序所需的环境信息的 Session 对象
        Session session = Session.getInstance(props);
        //设置调试信息在控制台打印出来
        session.setDebug(true);


        //3、创建邮件的实例对象
        Message msg = getMimeMessage(session, senderAddress, recipientAddress, userSheet, request);
        //4、根据session对象获取邮件传输对象Transport
        Transport transport = session.getTransport();
        //设置发件人的账户名和密码
        transport.connect(sender, senderPassword);
        //发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(msg, msg.getAllRecipients());

        //如果只想发送给指定的人，可以如下写法
        //transport.sendMessage(msg, new Address[]{new InternetAddress("xxx@qq.com")});

        //5、关闭邮件连接
        transport.close();
    }

    private static MimeMessage getMimeMessage(Session session, String senderAddress, String recipientAddress, UserAdder userSheet,
                                              HttpServletRequest request) throws Exception {
        //创建一封邮件的实例对象
        MimeMessage msg = new MimeMessage(session);
        //设置发件人地址
        msg.setFrom(new InternetAddress(senderAddress));
        /**
         * 设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
         * MimeMessage.RecipientType.TO:发送
         * MimeMessage.RecipientType.CC：抄送
         * MimeMessage.RecipientType.BCC：密送
         */
        msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipientAddress));
        //设置邮件主题
        msg.setSubject("邮件主题", "UTF-8");
        //设置邮件正文
        //创建文本"节点"

        String link = creatLink(userSheet, request);

        MimeBodyPart resetpasswordLink = new MimeBodyPart();
        // 这里添加图片的方式是将整个图片包含到邮件内容中, 实际上也可以以 http 链接的形式添加网络图片
        resetpasswordLink.setContent("请点击以下连接重设账号密码<br/>" +
                "<a href='" + link + "'>请点击我重设账号密码</a>", "text/html;charset=UTF-8");

        MimeBodyPart words = new MimeBodyPart();
        words.setContent("这是一封给" + recipientAddress + "的重要邮件", "text/html;charset=UTF-8");

        MimeMultipart content = new MimeMultipart();
        content.addBodyPart(words);
        content.addBodyPart(resetpasswordLink);
        content.setSubType("mixed");

        msg.setContent(content);
        //设置邮件的发送时间,默认立即发送
        msg.setSentDate(new Date());

        return msg;
    }

    private static String creatLink(UserAdder userSheet, HttpServletRequest request) {
        //生成密钥
        String secretKey = UUID.randomUUID().toString();
        //更新数据库内该用户的密钥
        userSheet.set("secretKey", secretKey).update();
//        //设置过期时间
//        Date outDate = new Date(System.currentTimeMillis() + 30 * 60 * 1000);// 30分钟后过期
//        System.out.println(System.currentTimeMillis());
//        long date = outDate.getTime() / 1000 * 1000;// 忽略毫秒数  mySql 取出时间是忽略毫秒数的

        String key = userSheet.get("username") + "$" + secretKey;
        String digitalSignature = MD5Util.MD5(key);

        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;

        return basePath + "/resetPass.action?sid=" + digitalSignature + "&id=" + userSheet.get("id");

    }

    public void saveTx(String username, String txhref) {
        int id = getId(username);
        UserAdder userSheet = new UserAdder().dao().findById(id);
        userSheet.set("img", txhref).update();
        CacheKit.remove(Config.CACHE_BLOG, "userTx");
    }

    public String getTx(String username) {
        if (username == null) {
            return "";
        } else {
            String img;
            System.out.println(CacheKit.get(Config.CACHE_BLOG,"userTx"));
            ContentAdder user = ContentAdder.dao.findFirstByCache(Config.CACHE_BLOG, "userTx", "select user.img from vmaig_auth_vmaiguser user " +
                    "where user.username = ?", username);
            img = user.get("img");
            return img;
        }
    }

    public int getUserNotificationNum(String username) {
        if (StrKit.isBlank(username)) {
            return 0;
        }
        int to_user_id = getId(username);
        UserAdder userSheet = UserAdder.dao.findFirst("select count(id) as notification_num from vmaig_system_notification where is_read = 0 AND" +
                " to_user_id = ?", to_user_id);
        return Integer.parseInt(userSheet.get("notification_num").toString());

//        UserAdder.class.getMethods()[0].invoke(userSheet);
    }

    public List<Map<String, Object>> getNotificationList(String username) {
        List<Map<String, Object>> notification_list = new ArrayList<Map<String, Object>>();
        List<NotificationAdder> notificationSheet = NotificationAdder.dao.find("select note.url as url, note.id " +
                "as id, note.text as text, note.is_read as is_read from `vmaig_system_notification` as note, " +
                "`vmaig_auth_vmaiguser` as user where note.to_user_id = user.id AND user.username = ? order by id DESC"
                , username);
        NotificationAdder notification;
        for (int i = 0; i < notificationSheet.size(); i++) {
            notification = notificationSheet.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("url", notification.get("url"));
            map.put("id", notification.get("id"));
            String text = notification.get("text");
            int margin = text.indexOf(":");
            int idLength = Integer.parseInt(text.substring(0,text.indexOf(":")));
            map.put("text", text.substring(margin + 1 + idLength));
            map.put("is_read", notification.get("is_read"));
            map.put("commentId", text.substring(margin+1, margin+1+idLength));
            notification_list.add(map);
        }
        return notification_list;
    }

    public Boolean isActive(String username){
        int id = getId(username);
        String cacheKey = "isActive" + id;
        UserAdder userSheet = UserAdder.dao.findFirstByCache(Config.CACHE_BLOG,cacheKey,"select is_active from vmaig_auth_vmaiguser where username = ?", username);
        if(userSheet != null){
            Boolean is_active = userSheet.get("is_active");
            return is_active;
        }else{
            return false;
        }
    }

    public void doLogout(String username) {
        int id = getId(username);
        UserAdder userSheet = UserAdder.dao.findById(id);
        if(userSheet == null){
            return;
        }
        userSheet.set("is_active", 0).update();
        CacheKit.remove(Config.CACHE_BLOG,"isActive-"+id);
        CacheKit.remove(Config.CACHE_BLOG, "userTx");
    }

    public void sendNotification(String type, int fromId, int commentId, int articleId) {

        //Container of types info - to determine the template for notification content
//        List<String> typeOfNotification = new ArrayList<String>();
//        typeOfNotification.add("reply");
        //more to add to assist with upcoming services
        //Container of template content
        Map<String, String> content = new HashMap<String, String>();
        content.put("reply", "{from} replied your comment for {article}, check it out!");
        //more to add to assist with upcoming services

        CommentAdder comment = CommentAdder.dao.findById(commentId);
        int toId = comment.get("user_id");          //user_id of whom is getting this notification

        UserAdder fromUserSheet = UserAdder.dao.findById(fromId);
        String fromUser = fromUserSheet.get("username");

        ContentAdder article = ContentAdder.dao.findById(articleId);
        String articleTitle = article.get("title");
        String articleUrl = article.get("en_title");

        String notificationText = Integer.toString(commentId).length() +
        ":" + commentId + content.get(type).replace("{from}",fromUser)
                .replace("{article}",articleTitle);
        Date now = new Date();
        DateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        new NotificationAdder().set("title", type).set("text", notificationText).set("url",articleUrl).set("type", type)
                .set("from_user_id",fromId).set("to_user_id",toId).set("create_time", simpleFormat.format(now)).save();

    }

    public void doReadNotification(int notificationId) {
        NotificationAdder notification = NotificationAdder.dao.findById(notificationId);
        notification.set("is_read", 1).update();
    }
}
