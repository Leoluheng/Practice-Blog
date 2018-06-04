package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.entity.UserAdder;
import com.github.leoluheng.blog.service.MD5Util;
import com.github.leoluheng.blog.service.UserService;
import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;


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
        render("user.html");
    }

    public void dologin() {
        String username, password;
        username = getPara("vmaig-auth-login-username");
        password = getPara("vmaig-auth-login-password");

        Boolean is_correct = userManager.verifyLogin(username, password);
        if (is_correct) {
            setSessionAttr("username", username);
            setSessionAttr("is_active", true);
            String tx = userManager.getTx(username);
            setSessionAttr("tx", tx);
            //More to add if needed

            redirect("/");
        }
    }

    public void dologout() {
        Boolean is_active = getSessionAttr("is_active");
        if (!is_active) {
            return;
        } else {
            removeSessionAttr("username");
            removeSessionAttr("is_active");
            removeSessionAttr("tx");
            redirect("");
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
        setAttr("user", userManager.get_user());
        redirect("/view/blog/user_changepassword.html");
    }

    public void dochangePassword() {
        String username, oldPassword, newPassword, newPassword1;
        Boolean is_active = getSessionAttr("is_active");
        if (!is_active) {
            return;
        }
        username = getSessionAttr("username");
        oldPassword = getPara("old-password");
        newPassword = getPara("new-password-1");
        newPassword1 = getPara("new-password-2");
        if (!newPassword.equals(newPassword1)) {
            setAttr("ErrorMessage", "mismatch");
            return;
        }
        int id = userManager.getId(username);
        if (!userManager.changePass(id, oldPassword, newPassword)) {
            setAttr("ErrorMessage", "wrong password");
        }
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
        setAttr("user", userManager.get_user());
        redirect("/view/blog/user_changetx.html");
    }
    public void dochangetx(){
        UploadFile newTx = getFile();
        String filename = newTx.getFileName();
        String txhref = "src/main/webapps/upload" + filename;
        userManager.saveTx(getSessionAttr("username").toString(),txhref);
        setSessionAttr("tx", txhref);
    }

    public void notification(){
        setAttr("user", userManager.get_user());
        redirect("/view/blog/user_notification.html");
    }

    public void getNotification(){

    }

    // protected methods

    // friendly methods

    // private methods

    // inner class

    // test main
}
