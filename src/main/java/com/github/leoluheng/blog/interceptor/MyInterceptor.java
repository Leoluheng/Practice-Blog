package com.github.leoluheng.blog.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

public class MyInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation me) {
        String action = me.getActionKey();
        Controller ctrl = me.getController();
//        Object is_Signin = ctrl.getSessionAttr("user");
        System.out.println(String.format("interceptor path: %s", action));
        ctrl.setAttr("website_title", PropKit.get("website_title"));
        me.invoke();
    }
}
