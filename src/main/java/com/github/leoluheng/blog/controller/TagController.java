package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.service.CommentService;
import com.github.leoluheng.blog.service.ContentService;
import com.github.leoluheng.blog.utility.Encodes;
import com.jfinal.core.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagController extends Controller {
    ContentService contentManager = ContentService.getInstance();
    CommentService commentManager = CommentService.getInstance();
    public void index(){
        render("/WEB-INF/view/blog/tag.html");
    }

    public void getTagPage(){
        String param = getPara("tag");
        param = Encodes.urlDecode(param);

        Map<String, List<Map<String, Object>>> response = new HashMap<String, List<Map<String, Object>>>();
        List<Map<String,Object>> article_list =  contentManager.getArticleList(param);
        response.put("article_list", article_list);
        renderJson(response);
    }
}
