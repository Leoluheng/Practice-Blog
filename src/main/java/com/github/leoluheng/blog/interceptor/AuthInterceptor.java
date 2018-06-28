package com.github.leoluheng.blog.interceptor;

import com.github.leoluheng.blog.service.EncryptUtil;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import jdk.nashorn.internal.runtime.Undefined;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuthInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation me) {
        List<String> noDirectAccess = new ArrayList<String>();
        noDirectAccess.add("/user/notification");
        noDirectAccess.add("/user/changetx");
        noDirectAccess.add("/user/changepassword");
        noDirectAccess.add("/user/doLogout");

        Controller ctrl = me.getController();
        String route = me.getControllerKey();

        String username = ctrl.getSessionAttr("username");
//        String is_active = ctrl.getSessionAttr("is_active");
        String action = me.getActionKey();

        String token = ctrl.getRequest().getHeader("csrf-token");

        //token exists in the request Head when user has logged in --> username exists
        if (route.equals("/user") && token != null && !token.equals("undefined")) {
            if (tokenVerify(token, username)) {
                me.invoke();
            }
        }

        if (noDirectAccess.contains(action) && (username == null)) {
            ctrl.redirect("/user/login");
        } else {
            me.invoke();
        }

    }

    protected Boolean tokenVerify(String token, String username) {

        String[] msg = token.split(",");
        username = EncryptUtil.sha256Hex(username);

        Date now = new Date();
        DateFormat simpleformate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String rn = simpleformate.format(now);
        byte[] expiryDate = EncryptUtil.base64Decode(msg[0]);
        String expiry = new String(expiryDate);
        String encryptedUsername = msg[1];

        String expiry1 = expiry.substring(0, 10);
        expiry = expiry.substring(11, expiry.length());
        expiry1 = expiry1.replaceAll("-", "");
        expiry = expiry.replaceAll(":", "");

        String rnDate = rn.substring(0, 10);
        rn = rn.substring(11, rn.length());
        rnDate = rnDate.replaceAll("-", "");
        rn = rn.replaceAll(":", "");

        if (Integer.valueOf(expiry1) > Integer.valueOf(rnDate)) {
            return false;
        } else if (Integer.valueOf(expiry) - Integer.valueOf(rn) < 0) {
            return false;
        } else if (username.equals(encryptedUsername)) {
            return true;
        } else {
            return false;
        }
    }
}
