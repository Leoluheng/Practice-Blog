package com.github.leoluheng.blog.interceptor;

import com.github.leoluheng.blog.service.ColumnService;
import com.github.leoluheng.blog.service.UserService;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

public class NavInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation me) {
        UserService userManager = UserService.getInstance();
        ColumnService columnManager = ColumnService.getInstance();

        String action = me.getActionKey();
        Controller ctrl = me.getController();
//        String username = ctrl.getSessionAttr("username");
//        String is_active = ctrl.getSessionAttr("is_active"
        System.out.println(String.format("interceptor path: %s", action));
        ctrl.setAttr("website_title", PropKit.get("website_title"));

//        if(null != username) {
//            ctrl.setAttr("user_img", userManager.getTx(username));
//
//        }

//        if(null == is_active) {
//            ctrl.setSessionAttr("is_active", "false");
//        }
        me.invoke();
    }
}
