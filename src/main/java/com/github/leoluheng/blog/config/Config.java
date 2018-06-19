package com.github.leoluheng.blog.config;

import com.github.leoluheng.blog.controller.*;
import com.github.leoluheng.blog.entity.*;
import com.github.leoluheng.blog.interceptor.AuthInterceptor;
import com.github.leoluheng.blog.interceptor.NavInterceptor;
import com.jfinal.config.*;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;

public class Config extends JFinalConfig {
    public void configConstant(Constants me) {
        me.setDevMode(true);
        //Config for application.properties
        PropKit.use("application.properties");
        this.loadPropertyFile("application.properties");
        //Config for tx uploaded
        me.setBaseUploadPath("upload");

    }

    public void configInterceptor(Interceptors me) {
        me.add(new AuthInterceptor());
        me.add(new NavInterceptor());
    }


    public void configHandler(Handlers me) {

    }

    public void configEngine(Engine me) {
//        me.setBaseTemplatePath("");
//        me.addSharedFunction("/WEB-INF/view/layout/base.html");
//        me.setBaseTemplatePath(PathKit.getWebRootPath());
        me.addSharedMethod(new java.lang.String());
    }

    public void configRoute(Routes me) {
        me.setBaseViewPath("/WEB-INF/view");
        me.add("/", MyController.class);
        me.add("/blog", MyController.class);
        me.add("/user", UserController.class);
        me.add("/all", AllController.class);
        me.add("/article", ArticleController.class);
        me.add("/category", CategoryController.class);
        me.add("/column", ColumnController.class);
        me.add("/search", SearchController.class);
        me.add("/news", NewsController.class);
        me.add("/tag", TagController.class);
    }

    public void configPlugin(Plugins me) {
        DruidPlugin dp = new DruidPlugin("jdbc:mysql://localhost:3306/blog", "root","123456");
        me.add(dp);
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        me.add(arp);
        arp.addMapping("blog_category", CategoryAdder.class);
        arp.addMapping("blog_column", ColumnAdder.class);
        arp.addMapping("blog_column_article", ColumnPostAdder.class);
        arp.addMapping("vmaig_comments_comment", CommentAdder.class);
        arp.addMapping("blog_article", ContentAdder.class);
        arp.addMapping("vmaig_system_link", LinkAdder.class);
        arp.addMapping("blog_nav", NavAdder.class);
        arp.addMapping("blog_news", NewsAdder.class);
        arp.addMapping("vmaig_system_notification", NotificationAdder.class);
        arp.addMapping("vmaig_auth_vmaiguser", UserAdder.class);
    }
}
