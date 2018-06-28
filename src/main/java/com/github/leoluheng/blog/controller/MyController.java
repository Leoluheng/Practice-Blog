/*
 * Copyright 2010 Guangdong Etone Technology Co.,Ltd.
 * All rights reserved.
 */
package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.service.*;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.plugin.ehcache.CacheName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 一句简洁的说明
 *
 * @author <a href="mailto:heng.lu@uwaterloo.ca">HengLu</a>
 * @since $$Id$$
 */
public class MyController extends Controller {
    // members
    ContentService contentManager = ContentService.getInstance();
    LinkService linkManager = LinkService.getInstance();
    NavService navManager = NavService.getInstance();
    UserService userManager = UserService.getInstance();
    CommentService commentManager = CommentService.getInstance();

    // static block
    static {

    }
    // constructors

    // properties

    // public methods
    public void index() {
        render("blog/index.html");
        System.out.println(this.getRequest().getRemoteAddr());
    }

    public void doIndex() {
        //declaration of the server response data structure
        Map<String, List<Map<String, Object>>> response = new HashMap<String, List<Map<String, Object>>>();

        //in fact, this attribute is never used
        setAttr("nav_list", navManager.get_nav_list());  //modified to be an Ajax request in nav.html


        String is_active = getSessionAttr("is_active");
        setAttr("is_active", is_active);

        //pack up homepage article_list data
//        setAttr("article_list", contentManager.getArticleList("index"));
        List<Map<String, Object>> article_list = contentManager.getArticleList("index");
        response.put("article_list", article_list);

        ///////////////////////////////////////////////////////////
        //pack up page_obj data////////////////////////////////////
        setAttr("page_obj", contentManager.getPageObj());/////////////////////////////////////////
        ///////////////////////////////////////////////////////////

        //pack up links data
//        setAttr("links", linkManager.getLinks());
        List<Map<String, Object>> link_list = linkManager.getLinks();
        response.put("links", link_list);

        //pass server response to frontend
        renderJson(response);
    }

    public void carousel() {
        Map<String, List<Map<String, Object>>> response = new HashMap<String, List<Map<String, Object>>>();

        //pack up carousel_list data
//        setAttr("carousel_page_list", contentManager.getCarouselPageList());
        List<Map<String, Object>> carousel_list = contentManager.getCarouselPageList();
        response.put("carousel_list", carousel_list);

        renderJson(response);
    }

    public void sideWidgets() {
        Map<String, List<Map<String, Object>>> response = new HashMap<String, List<Map<String, Object>>>();

        //pack up hot_article_list
//        setAttr("hot_article_list", contentManager.getHotArticleList());
        List<Map<String, Object>> hot_article_list = contentManager.getHotArticleList();
        response.put("hot_article_list", hot_article_list);

        //pack up latest_comment_list
//        setAttr("latest_comment_list", commentManager.getLatestComments());
        List<Map<String, Object>> latest_comment_list = commentManager.getLatestComments();
        response.put("latest_comment_list", latest_comment_list);

        renderJson(response);
    }

    @Before(CacheInterceptor.class)
    @CacheName("navColumn")
    public void navColumn() {
        ColumnService columnManager = ColumnService.getInstance();
        List<String> columnList = columnManager.getColumnList();
        renderJson(columnList);
    }

//    @Before(CacheInterceptor.class)
//    @CacheName("navUser")
    public void navUser() {
        String username = getSessionAttr("username");
        UserService userManager = UserService.getInstance();
        Map<String, Object> response = new HashMap<String, Object>();
        String img = userManager.getTx(username);
        response.put("is_active", userManager.isActive(username));
        response.put("username", username);
        if (img == null || img == "") {
            img = "";
            response.put("showImg", false);
        }else{
            response.put("showImg", true);
        }
        response.put("img",img);

        response.put("user_notificationNum", userManager.getUserNotificationNum(username));

        renderJson(response);
    }

    // protected methods

    // friendly methods

    // private methods

    // inner class

    // test main
}

