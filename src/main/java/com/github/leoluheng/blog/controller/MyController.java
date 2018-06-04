/*
 * Copyright 2010 Guangdong Etone Technology Co.,Ltd.
 * All rights reserved.
 */
package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.service.ContentService;
import com.github.leoluheng.blog.service.LinkService;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;


/**
 * 一句简洁的说明
 *
 * @author <a href="mailto:heng.lu@uwaterloo.ca">HengLu</a>
 * @since $$Id$$
 */
//@Before(MyInterceptor.class)
public class MyController extends Controller {
    // members
    ContentService contentManager = new ContentService();
    LinkService linkManager = new LinkService();
    // static block
    static {

    }
    // constructors

    // properties

    // public methods
    public void index() {
        render("/WEB-INF/view/layout/index.html");
        setAttr("carousel_page_list", contentManager.get_carousel_page_list());
        setAttr("article_list", contentManager.get_article_list());
        setAttr("page_obj", contentManager.get_page_obj());
        setAttr("links", linkManager.get_links());

    }

    // protected methods

    // friendly methods

    // private methods

    // inner class

    // test main
}

