/*
 * Copyright 2010 Guangdong Etone Technology Co.,Ltd.
 * All rights reserved.
 */
package com.github.leoluheng.blog.controller;
import com.github.leoluheng.blog.service.ContentService;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    }

    public void doIndex(){
        Map<String, List<Map<String, Object>>> response = new HashMap<String, List<Map<String, Object>>>();

        List<Map<String, Object>> category_list = ContentManager.get_category_list();
        List<Map<String, Object>> article_list = ContentManager.get_article_list("all");

        response.put("category_list", category_list);
        response.put("article_list", article_list);

        renderJson(response);
    }

    // protected methods

    // friendly methods

    // private methods

    // inner class

    // test main
}

