package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.entity.UserAdder;
import com.github.leoluheng.blog.service.MD5Util;
import com.github.leoluheng.blog.service.UserService;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
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
//            setSessionAttr("tx", tx);
            //More to add if needed
            setAttr("user_img", tx);
            setAttr("username", username);
//            redirect("/");
        }else if(is_correct == 1){
            errors.add("Incorrect password");
        }else{
            errors.add("Incorrect username");
        }
        response.put("errors",errors);
        renderJson(response);
    }

    public void doLogout() {
        String status = getSessionAttr("is_active");
        Boolean is_active = Boolean.parseBoolean(status);
        if (!is_active) {
            return;
        } else {
            removeSessionAttr("username");
            removeSessionAttr("is_active");
//            removeSessionAttr("tx");
//            removeAttr("user");
        }
    }

    public void register(){
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

        if(password1.equals("") || password2.equals("") || username.equals("") || email.equals("")){
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
            setSessionAttr("username", username);
            setSessionAttr("is_active", "true");
            String tx = userManager.getTx(username);
//            setSessionAttr("tx", tx);
            setAttr("user_img", tx);

            response.put("errors", errors);
            renderJson(response);
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

        Map<String, List<String>> response = new HashMap<String, List<String>>();
        List<String> errors = new ArrayList<String>();
        if(newPassword.equals("") || newPassword1.equals("")){
            errors.add("All fields need to be filled");
            response.put("errors", errors);
            renderJson(response);
            return;
        }else if (!newPassword.equals(newPassword1)) {
            errors.add("Password mismatch");
            response.put("errors", errors);
            renderJson(response);
            return;
        }
        int id = userManager.getId(username);

        if(-1 == id){
            errors.add("Account Lost. Server is under attack!!! Serious Damage occurred!!");
        }
        response.put("errors", errors);
        if (!userManager.changePass(id, oldPassword, newPassword)) {
            removeSessionAttr("username");
            setSessionAttr("is_active", "false");
        }
        renderJson(response);
        removeSessionAttr("username");
        setSessionAttr("is_active", "false");
//        redirect("/login");
    }

    public void forgetPassword(){
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
//        String username = getSessionAttr("username");
//        setAttr("tx", userManager.getTx(username));
        render("/WEB-INF/view/blog/user_changetx.html");
    }
    public void dochangetx(){
//        String path = "";
//        String uploadDir = File.separator + "upload" + File.separator + "contract" + File.separator;
//        path = PathKit.getWebRootPath() + uploadDir;
        String path = getSession().getServletContext().getRealPath("/")
                + "img/";
        List<UploadFile> uf = getFiles(path);

        File file = uf.get(0).getFile();
        String fileName = file.getName();
        String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
//        UploadFile newTx = getFile("upload", path); ////////////////EEEEERRRRRROOOOOORRRRR!!!!!!!!!!!!!!!
//        String filename = newTx.getFileName();
//        String txhref = "src/main/webapps/upload" + filename;
        int temp = (int) (Math.random() * 9000 + 1000);
        fileName = getRequest().getRemoteAddr().replaceAll(":", "")
//                + dateFormat.format(new Date())
                + new Integer(temp).toString() + "." + extName;
        file.renameTo(new File(path + fileName));
//        String txPath = (UploadFile)file.getUploadPath();

//        userManager.saveTx(getSessionAttr("username").toString(),txPath);
//        setSessionAttr("tx", txPath);
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
