/*
 * Copyright 2010 Guangdong Etone Technology Co.,Ltd.
 * All rights reserved.
 */
package com.github.leoluheng.blog.controller;
import com.github.leoluheng.blog.service.ContentService;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
/**
 * 一句简洁的说明
 *
 * @author <a href="mailto:heng.lu@uwaterloo.ca">HengLu</a>
 * @since $$Id$$
 */
//@Before(NavInterceptor.class)

public class AllController extends Controller {

    ContentService ContentManager = ContentService.getInstance();

    // members
    // static block
    static {

    }
    // constructors

    // properties

    // public methods
    public void index() {
        render("/WEB-INF/view/blog/all.html");
        setAttr("category_list", ContentManager.get_category_list());
        setAttr("article_list", ContentManager.get_article_list("all"));

    }

    // protected methods

    // friendly methods

    // private methods

    // inner class

    // test main
}

