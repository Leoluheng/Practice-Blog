package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.service.CommentService;
import com.github.leoluheng.blog.service.ContentService;
import com.github.leoluheng.blog.utility.Encodes;
import com.jfinal.core.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryController extends Controller {

    ContentService contentManager = ContentService.getInstance();
    CommentService commentManager = CommentService.getInstance();

    public void index(){
        render("/WEB-INF/view/blog/category.html");
    }

    public void doCategoryArticle(){
        String param = getPara("category");
        param = Encodes.urlDecode(param);
        List<Map<String, Object>> articleList = contentManager.getArticleList(param);
        Map<String, List<Map<String, Object>>> response = new HashMap<String, List<Map<String, Object>>>();

        response.put("articleList", articleList);
        renderJson(response);
    }

}
