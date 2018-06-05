package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.service.ContentService;
import com.jfinal.core.Controller;

public class SearchController extends Controller {

    ContentService contentManager = new ContentService();

    public void index(String param){
        setAttr("article_list", contentManager.get_article_list("all"));

        ////NO implementation of SEARCH exactly!!!!!!!!!!!!!!!!!!

        render("all_post.html");
    }
}
