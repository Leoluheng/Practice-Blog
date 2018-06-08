package com.github.leoluheng.blog.interceptor;

import com.github.leoluheng.blog.service.UserService;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

public class NavInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation me) {
        UserService userManager = UserService.getInstance();

        String action = me.getActionKey();
        Controller ctrl = me.getController();
        String username = ctrl.getSessionAttr("username");
        System.out.println(String.format("interceptor path: %s", action));
        ctrl.setAttr("website_title", PropKit.get("website_title"));
        ctrl.setAttr("notification_count", userManager.getUserNotificationNum(username));
        String is_active = ctrl.getSessionAttr("is_active");

        if(null != username) {
            ctrl.setAttr("user_img", userManager.getTx(username));
            ctrl.setAttr("username", username);

        }

        if(null == is_active) {
            ctrl.setSessionAttr("is_active", "false");
        }

        me.invoke();
    }
}
