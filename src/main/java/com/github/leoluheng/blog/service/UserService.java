package com.github.leoluheng.blog.service;

import com.github.leoluheng.blog.entity.UserAdder;
import com.jfinal.kit.PropKit;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class UserService {

    public boolean verifyLogin(String username, String password) {
        List<UserAdder> userSheet = UserAdder.dao.find("select username from vmaig_auth_vmaiguser where " +
                "username = ?  AND  password = ?", username, password);
        return 0 != userSheet.size();
    }

    //Acquiring first_name last_name
    public String doreg(String username, String password, String first_name, String last_name, String email, String date_joined) {
        UserAdder userSheet = UserAdder.dao.findFirst("select username email from vmaig_auth_vmaiguser where " +
                "username = ?", username);
        if (null != userSheet.get("username")) {
            return "username";
        }else if(null != userSheet.get("email")){
            return "email";
        }

        new UserAdder().set("username", username)
                .set("password", password).set("first_name", first_name).set("last_name", last_name)
                .set("email", email).set("date_joined", date_joined).set("is_active", 1).save();
        return "true";
    }

    //No first_name last_name
    public String doreg(String username, String password, String email, String date_joined) {
        UserAdder userSheet = UserAdder.dao.findFirst("select username email from vmaig_auth_vmaiguser where " +
                "username = ?", username);
        if (null != userSheet) {
            return "username";
        }

        new UserAdder().set("username", username)
                .set("password", password).set("email", email)
                .set("date_joined", date_joined).set("is_active", true).save();
        return "true";
    }

    public int getId(String username) {
        UserAdder userSheet = UserAdder.dao.findFirst("select id from vmaig_auth_vmaiguser where username = ?", username);
        return userSheet.get("id");
    }

    public boolean changePass(int id, String credential, String newPassword) {
        UserAdder userSheet = UserAdder.dao.findById(id);
        if(!credential.equals(userSheet.get("password"))){
            return false;
        }
        return userSheet.set("password", newPassword).update();
    }

    public boolean verifyUser(String username, String email){
        UserAdder userSheet = UserAdder.dao.findFirst("select username from vmaig_auth_vmaiguser where username = ? AND" +
        "email = ?", username, email);
        if(null != userSheet){
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
                                             HttpServletRequest request) throws Exception{
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
        msg.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(recipientAddress));
        //设置邮件主题
        msg.setSubject("邮件主题","UTF-8");
        //设置邮件正文
        //创建文本"节点"

        String link = creatLink(userSheet, request);

        MimeBodyPart resetpasswordLink = new MimeBodyPart();
        // 这里添加图片的方式是将整个图片包含到邮件内容中, 实际上也可以以 http 链接的形式添加网络图片
        resetpasswordLink.setContent("请点击以下连接重设账号密码<br/>" +
                "<a href='" + link +"'>请点击我重设账号密码</a>", "text/html;charset=UTF-8");

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

    private static String creatLink(UserAdder userSheet, HttpServletRequest request){
        //生成密钥
        String secretKey=UUID.randomUUID().toString();
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

    public void saveTx(String username, String txhref){
        UserAdder userSheet = new UserAdder().dao().findFirst("select username from vmaig_auth_vmaiguser where username = ?", username);
        userSheet.set("img", txhref).update();
    }

    public String getTx(String username){
        UserAdder userSheet = new UserAdder().dao().findFirst("select img from vmaig_auth_vmaiguser where username = ?", username);
        return userSheet.get("img");
    }

    public Object get_user() {
        return null;
    }
}
