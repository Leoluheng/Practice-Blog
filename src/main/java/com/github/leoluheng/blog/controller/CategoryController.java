package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.service.ContentService;
import com.jfinal.core.Controller;

public class CategoryController extends Controller {

    ContentService contentManager = ContentService.getInstance();
    public void index(String param){
        setAttr("article_list",contentManager.get_article_list(param));
        render("category.html");

    }

}
