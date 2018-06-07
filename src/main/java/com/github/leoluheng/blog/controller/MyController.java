/*
 * Copyright 2010 Guangdong Etone Technology Co.,Ltd.
 * All rights reserved.
 */
package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.interceptor.MyInterceptor;
import com.github.leoluheng.blog.service.*;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;


/**
 * 一句简洁的说明
 *
 * @author <a href="mailto:heng.lu@uwaterloo.ca">HengLu</a>
 * @since $$Id$$
 */
@Before(MyInterceptor.class)
public class MyController extends Controller {
    // members
    ContentService contentManager = new ContentService();
    LinkService linkManager = new LinkService();
    NavService navManager = new NavService();
    UserService userManager = new UserService();
    CommentService commentManager = new CommentService();
    // static block
    static {

    }
    // constructors

    // properties

    // public methods
    public void index() {
        setAttr("nav_list", navManager.get_nav_list());
        String is_active = getSessionAttr("is_active");
        setAttr("is_active", is_active);


        setAttr("carousel_page_list", contentManager.get_carousel_page_list());
        setAttr("article_list", contentManager.get_article_list("index"));
        setAttr("page_obj", contentManager.get_page_obj());
        setAttr("links", linkManager.get_links());
        setAttr("hot_article_list", contentManager.get_hot_article_list());
        setAttr("latest_comment_list", commentManager.get_latest_comments());

        String username = getSessionAttr("username");

        if(null != username) {
            setAttr("user_img", userManager.getTx(username));
            setAttr("username", username);
            setAttr("notification_count", userManager.get_user_notification_num(username));
        }

        render("blog/index.html");
    }

    // protected methods

    // friendly methods

    // private methods

    // inner class

    // test main
}

