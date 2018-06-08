package com.github.leoluheng.blog.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

import java.util.ArrayList;
import java.util.List;

public class AuthInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation me){
        List<String> noDirectAccess = new ArrayList<String>();
        noDirectAccess.add("/user/notification");
        noDirectAccess.add("/user/changetx");
        noDirectAccess.add("/user/changepassword");
        noDirectAccess.add("/user/doLogout");

        Controller ctrl = me.getController();
        String username = ctrl.getSessionAttr("username");
        String is_active = ctrl.getSessionAttr("is_active");
        String action = me.getActionKey();

        if(noDirectAccess.contains(action) && (username == null || is_active == null)){
            ctrl.redirect("/user/login");
//            me.invoke();
        }else{
            me.invoke();
        }
    }
}
