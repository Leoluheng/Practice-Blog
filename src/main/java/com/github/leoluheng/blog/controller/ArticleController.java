package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.service.CommentService;
import com.github.leoluheng.blog.service.ContentService;
import com.github.leoluheng.blog.service.UserService;
import com.jfinal.core.Controller;

import java.util.List;

public class ArticleController extends Controller {
    ContentService contentManager = new ContentService();
    UserService userManager = new UserService();
    CommentService commentManager = new CommentService();
    // members
    // static block
    static {

    }
    // constructors

    // properties

    // public methods

    ///////////////////////////////////
    public void XXXXXXX() {
        //////////////////////////////////////
        render("/view/layout/");
        setAttr("category_url", contentManager.get_article_category());
        setAttr("article", contentManager.get_article());
        setAttr("tags", contentManager.get_tags());
        setAttr("user_img", userManager.getImg());
        setAttr("comment_list", commentManager.get_Object_comments());
        setAttr("latest_comment_list", commentManager.get_Object_latest_comments());
        setAttr("hot_article_list", contentManager.get_hot_article_list());
    }

    // protected methods

    // friendly methods

    // private methods

    // inner class

    // test main
}
