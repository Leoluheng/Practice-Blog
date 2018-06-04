package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.service.ColumnService;
import com.github.leoluheng.blog.service.ContentService;
import com.jfinal.core.Controller;

public class ColumnController extends Controller {

    ColumnService columnManager = new ColumnService();
    ContentService contentManager = new ContentService();

    public void python(){
        setAttr("column_summary", columnManager.get_summary("python"));
//        setAttr("article_list", contentManager.get_article_list("python"))
    }
}
