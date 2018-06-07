package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.entity.UserAdder;
import com.github.leoluheng.blog.service.MD5Util;
import com.github.leoluheng.blog.service.UserService;
import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class UserController extends Controller {
    // members
    UserService userManager = new UserService();

    // static block
    static {

    }

    // constructors

    // properties

    // public methods
    public void login() {
        render("blog/login.html");
    }

    public void dologin() {
        String username, password;
        username = getPara("username");
        password = getPara("password");

        Boolean is_correct = userManager.verifyLogin(username, password);
        if (is_correct) {
            setSessionAttr("username", username);
            setSessionAttr("is_active", "true");
            String tx = userManager.getTx(username);
            setSessionAttr("tx", tx);
            //More to add if needed
            setAttr("user_img", tx);
            setAttr("username", username);
            redirect("/");
        }
    }

    public void dologout() {
        String status = getSessionAttr("is_active");
        Boolean is_active = Boolean.parseBoolean(status);
        if (!is_active) {
            return;
        } else {
            removeSessionAttr("username");
            removeSessionAttr("is_active");
            removeSessionAttr("tx");
            removeAttr("user");
            redirect("/");
        }
    }

    public void register(){
        redirect("/auth/register.html");
    }

    public void doregister() {
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
//        String result = userManager.doreg(username, password, first_name, last_name, email, date_now);


        String result = userManager.doreg(username, password1, email, date_now);
        if (result.equalsIgnoreCase("username")) {
            setAttr("RepeatedUsername", "username already used");
            return;
        } else if (result.equalsIgnoreCase("email")) {
            setAttr("RepeatedEmail", "email already registered");
        } else {
            setSessionAttr("username", username);
            setSessionAttr("is_active", true);
            String tx = userManager.getTx(username);
            setSessionAttr("tx", tx);
        }
    }

    public void changepassword(){
        render("/WEB-INF/view/blog/user_changepassword.html");
    }

    public void doChangePassword() {
        String username, oldPassword, newPassword, newPassword1, status;
        status = getSessionAttr("is_active");
        Boolean is_active = Boolean.parseBoolean(status);
        if (!is_active) {
            return;
        }
        username = getSessionAttr("username");
        oldPassword = getPara("old_password");
        newPassword = getPara("new_password1");
        newPassword1 = getPara("new_password2");
        if (!newPassword.equals(newPassword1)) {
            setAttr("ErrorMessage", "mismatch");
            return;
        }
        int id = userManager.getId(username);
        if (!userManager.changePass(id, oldPassword, newPassword)) {
            setAttr("ErrorMessage", "wrong password");
        }
        removeSessionAttr("username");
        setSessionAttr("is_active", "false");
        redirect("/login");

    }

    public void forgetPassword(){
        redirect("/auth/forgetpassword.html");
    }
    public void fixforgetPassword() throws Exception {
        String username, email;
        username = getPara("vmaig-auth-forgetpassword-username");
        email = getPara("vmaig-auth-forgetpassword-email");
        Boolean result = userManager.verifyUser(username, email);
        if (!result) {
            setAttr("ErrorMessage", "Incorrect username or email");
        } else {
            userManager.resetPassword(email, this.getRequest());
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
        }else {
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

    public void doResetPass(){
        UserAdder userSheet = getAttr("userSheet");
        String newPassword, newPassword1;

        newPassword = getPara("vmaig-auth-resetpassword-password1");
        newPassword1 = getPara("vmaig-auth-resetpassword-password2");

        if(!newPassword.equals(newPassword1)){
            setAttr("ErrorMessage","password mismatch");
        }
        userSheet.set("password", newPassword).update();    //业务逻辑是不是不应该放这里？不可以 ： 可以允许这一次以节省代码量
        removeAttr("userSheet");
        redirect("user.html");
    }

    public void changetx(){
        String username = getSessionAttr("username");
        setAttr("tx", userManager.getTx(username));
        render("blog/user_changetx.html");
        dochangetx();
    }
    public void dochangetx(){
        UploadFile newTx = getFile(); ////////////////EEEEERRRRRROOOOOORRRRR!!!!!!!!!!!!!!!
        String filename = newTx.getFileName();
//        String txhref = "src/main/webapps/upload" + filename;
        String txPath = newTx.getUploadPath();
        userManager.saveTx(getSessionAttr("username").toString(),txPath);
        setSessionAttr("tx", txPath);
    }

    public void notification(){
        String username = getSessionAttr("username");
        //////get_notification()
//        setAttr("")
        setAttr("notification_list", getNotification(username));
        render("/WEB-INF/view/blog/user_notification.html");
    }

    public Map<Integer, Map<String, Object>> getNotification(String username){
        return userManager.get_notification_list(username);
    }

    public void getNotificationContent(){

    }

    // protected methods

    // friendly methods

    // private methods

    // inner class

    // test main
}
