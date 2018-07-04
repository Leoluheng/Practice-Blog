package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.config.Config;
import com.github.leoluheng.blog.entity.NotificationAdder;
import com.github.leoluheng.blog.entity.UserAdder;
import com.github.leoluheng.blog.service.EncryptUtil;
import com.github.leoluheng.blog.service.MD5Util;
import com.github.leoluheng.blog.service.Resp;
import com.github.leoluheng.blog.service.UserService;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.upload.UploadFile;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class UserController extends Controller {
    // members
    UserService userManager = UserService.getInstance();

    // static block
    static {

    }

    // constructors

    // properties

    // public methods
    public void login() {
        render("/WEB-INF/view/blog/login.html");
    }

    public void doLogin() {
        String username, password;
        username = getPara("username");
        password = getPara("password");
        Map<String, List<String>> response = new HashMap<String, List<String>>();
        List<String> errors = new ArrayList<String>();
        int is_correct = userManager.verifyLogin(username, password);
        if (is_correct == 0) {
            setSessionAttr("username", username);
            setSessionAttr("is_active", "true");

            String tx = userManager.getTx(username);
            setSessionAttr("user_img",tx );

            sendToken();
        } else if (is_correct == 1) {
            errors.add("Incorrect password");
        } else {
            errors.add("Incorrect username");
        }
        response.put("errors", errors);
        renderJson(response);
    }

    public void doLogout() {
        String username = getSessionAttr("username");
        removeSessionAttr("username");
        removeSessionAttr("is_active");
        userManager.doLogout(username);
        delToken();
        renderJson();
    }

    public void register() {
        render("/WEB-INF/view/blog/register.html");
    }

    public void doRegister() {
        String username, password1, password2, email;
        username = getPara("username");
        password1 = getPara("password1");
        password2 = getPara("password2");
//        first_name = getPara("first_name");
//        last_name = getPara("last_name");
        email = getPara("email");
        //Getting the date this user joins the blog
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date_now = sf.format(date);
        //////////////////////////////////
//        String result = userManager.doReg(username, password, first_name, last_name, email, date_now);
        Map<String, List<String>> response = new HashMap<String, List<String>>();
        List<String> errors = new ArrayList<String>();

        if (password1.equals("") || password2.equals("") || username.equals("") || email.equals("")) {
            errors.add("All fields need to be filled");
            response.put("errors", errors);
            renderJson(response);
            return;
        }
        String result = userManager.doReg(username, password1, email, date_now);

        if (result.equalsIgnoreCase("username")) {
            errors.add("Repeated Username");
            response.put("errors", errors);
            renderJson(response);
        } else if (result.equalsIgnoreCase("email")) {
            errors.add("Repeated Email");
            response.put("errors", errors);
            renderJson(response);
        } else {
            String username1 = getSessionAttr("username");
            userManager.doLogout(username1);

            setSessionAttr("username", username);
            setSessionAttr("is_active", "true");
            String tx = userManager.getTx(username);
//            setSessionAttr("tx", tx);
            setAttr("user_img", tx);

            response.put("errors", errors);

            sendToken();

            renderJson(response);
        }
    }

    public void changepassword() {
//        String username = getSessionAttr("username");
//        setAttr("username", username);
//        String tx = getSessionAttr("user_img");
//        setAttr("user_img", tx);
        render("/WEB-INF/view/blog/user_changepassword.html");
    }

    public void doChangePassword() {
        String username, oldPassword, newPassword, newPassword1, status;
        status = getSessionAttr("is_active");
        Boolean isActive = Boolean.parseBoolean(status);
        Map<String, List<String>> response = new HashMap<String, List<String>>();

        if (!isActive) {
            return;
        }
        username = getSessionAttr("username");
        oldPassword = getPara("old_password");
        newPassword = getPara("new_password1");
        newPassword1 = getPara("new_password2");

        List<String> errors = new ArrayList<String>();
        if (newPassword.equals("") || newPassword1.equals("")) {
            errors.add("All fields need to be filled");
            response.put("errors", errors);
            renderJson(response);
            return;
        } else if (!newPassword.equals(newPassword1)) {
            errors.add("Password mismatch");
            response.put("errors", errors);
            renderJson(response);
            return;
        }
        int id = userManager.getId(username);

        if (-1 == id) {
            errors.add("Account Lost. Server is under attack!!! Serious Damage occurred!!");
        }
        response.put("errors", errors);
        if (!userManager.changePass(id, oldPassword, newPassword)) {
            errors.add("Wrong password");
            response.put("errors", errors);
            renderJson(response);
        }
        removeSessionAttr("username");
        removeSessionAttr("isActive");
        userManager.doLogout(username);
        delToken();
        renderJson(response);

//        redirect("/login");
    }

    public void forgetPassword() {
        render("/WEB-INF/view/blog/forgetpassword.html");
    }

    public void fixforgetPassword() throws Exception {
        String username, email;
        username = getPara("username");
        email = getPara("email");
        Boolean result = userManager.verifyUser(username, email);
        if (!result) {
            setAttr("ErrorMessage", "Incorrect username or email");
        } else {
//            userManager.resetPassword(email, this.getRequest());
        }

    }

    public void resetPass() {
        String message = "";
        HttpServletRequest request = this.getRequest();

        String sid = request.getParameter("sid");
        String id = request.getParameter("id");

        if (sid.isEmpty() || id.isEmpty()) {
            System.out.println("请求的链接不正确,请重新操作.");
            message = "请求的链接不正确,请重新操作.";
        } else {
            UserAdder userSheet = new UserAdder().dao().findById(Integer.parseInt(id));
            if (userSheet != null) {
                String key = userSheet.get("username") + "$" + userSheet.get("secretKey");

                String digitalSignature = MD5Util.MD5(key);
                if (!digitalSignature.equals(sid)) {
                    System.out.println("链接加密密码不正确");
                    message = "链接加密密码不正确";
                } else {
                    //验证成功，实现跳转进行密码更改
                    setAttr("userSheet", userSheet);
                }

            } else {
                System.out.println("用户信息不存在");
                message = "用户信息不存在";
            }
        }
        setAttr("ErrorMessage", message);
    }

    public void doResetPass() {
        UserAdder userSheet = getAttr("userSheet");
        String newPassword, newPassword1;

        newPassword = getPara("vmaig-auth-resetpassword-password1");
        newPassword1 = getPara("vmaig-auth-resetpassword-password2");

        if (!newPassword.equals(newPassword1)) {
            setAttr("ErrorMessage", "password mismatch");
        }
        userSheet.set("password", newPassword).update();    //业务逻辑是不是不应该放这里？不可以 ： 可以允许这一次以节省代码量
        removeAttr("userSheet");
        redirect("user.html");
    }

    public void changetx() {
//        String username = getSessionAttr("username");
//        setAttr("username", username);
//        String tx = getSessionAttr("user_img");
//        setAttr("user_img", tx);
        render("/WEB-INF/view/blog/user_changetx.html");
    }

    public void dochangetx() {
//        String path = "";
//        String uploadDir = File.separator + "upload" + File.separator + "contract" + File.separator;
//        path = PathKit.getWebRootPath() + uploadDir;
//        String path = getSession().getServletContext().getRealPath("/")
//                + "img/";
//        List<UploadFile> uf = getFiles(path);
//
//        File file = uf.get(0).getFile();
//        String fileName = file.getName();
//        String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
//        UploadFile newTx = getFile("upload", path); ////////////////EEEEERRRRRROOOOOORRRRR!!!!!!!!!!!!!!!
//        String filename = newTx.getFileName();
//        String txhref = "src/main/webapps/upload" + filename;
//        int temp = (int) (Math.random() * 9000 + 1000);
//        fileName = getRequest().getRemoteAddr().replaceAll(":", "")
//                + dateFormat.format(new Date())
//                + new Integer(temp).toString() + "." + extName;
//        file.renameTo(new File(path + fileName));
//        String txPath = (UploadFile)file.getUploadPath();

//        userManager.saveTx(getSessionAttr("username").toString(),txPath);
//        setSessionAttr("tx", txPath
//         String uri = getPara("tx");

        String tx = getPara("tx");
        Resp resp = Resp.getInstance();
        try {
            String fileName = PropKit.get("relativeFilePath") + getRandomString(Integer.parseInt(
                    PropKit.get("lengthOfFileName")))+".png";
            if (GenerateImage(tx, fileName)) {
                System.out.println("--------file--------");
                resp.setResult(true);
                resp.setMsg("Successfully uploaded\nIt will be cached for 10 minutes");
                String username = getSessionAttr("username");
                userManager.saveTx(username, fileName);
            } else {
                resp.setResult(false);
                resp.setMsg("Invalid image");
            }
//
        } catch (Exception e) {
            e.printStackTrace();
            resp.setMsg("Server Error, our server is under unknown attack!!!");
        }
        renderJson(resp.getResponse());
    }

    //Decode the base64-encoded img data and generate a img file on server
    public static boolean GenerateImage(String imgStr, String imgFileName) {
        if (imgStr == null || imgStr == "") // base64-encoded img data empty
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            imgStr = imgStr.replace(" ", "+");
            // Base64解码
            byte[] bytes = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// fix lost data
                    bytes[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(PropKit.get("baseUploadPath")+imgFileName);         // 生成png图片
            out.write(bytes);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            //Any possible char from a-z,A-Z,0-9
            int number = random.nextInt(3);
            long result;
            switch (number) {
                case 0:
                    //A-Z
                    result = Math.round(Math.random() * 25 + 65);
                    //change to string
                    sb.append(String.valueOf((char) result));
                    break;
                case 1:
                    //a-z
                    result = Math.round(Math.random() * 25 + 97);
                    sb.append(String.valueOf((char) result));
                    break;
                case 2:
                    //0-9
                    sb.append(String.valueOf
                            (new Random().nextInt(10)));
                    break;
            }
        }
        return sb.toString();
    }

    public void notification() {
//        String username = getSessionAttr("username");
//        setAttr("username", username);
//        String tx = getSessionAttr("user_img");
//        setAttr("user_img", tx);
        render("/WEB-INF/view/blog/user_notification.html");
    }

    public List<Map<String, Object>> getNotification(String username) {
        return userManager.getNotificationList(username);
    }

    public void doNotification() {
        String username = getSessionAttr("username");
        Map<String, List<Map<String, Object>>> response = new HashMap<String, List<Map<String, Object>>>();
        List<Map<String, Object>> notificationList = getNotification(username);
        response.put("notificationList", notificationList);
        renderJson(response);
    }

    public void setNotificationRead() {
        String notificationId = getPara("notification_id");
        userManager.doReadNotification(Integer.parseInt(notificationId));

        renderJson(new ArrayList<Integer>());
    }

    //Needing a new scheme for token generation... without using time info...
    private String tokenGen(String username) {
        Date now = new Date();
        DateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        now.setTime(now.getTime() + 1200000);
        String msg = EncryptUtil.base64Encode(simpleformat.format(now)) + "," + EncryptUtil.sha256Hex(username);
        return msg;
    }

    protected void sendToken() {
        String username = getSessionAttr("username");
        String token = tokenGen(username);
        setCookie("csrf-token", token, 1200, false);
    }

    protected void delToken() {
        removeCookie("csrf-token");
    }
    // protected methods

    // friendly methods

    // private methods

    // inner class

    // test main
}
