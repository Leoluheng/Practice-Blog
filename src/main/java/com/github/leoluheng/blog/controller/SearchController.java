package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.service.ContentService;
import com.github.leoluheng.blog.utility.Encodes;
import com.jfinal.core.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchController extends Controller {

    ContentService contentManager = ContentService.getInstance();
//    CommentService commentManager = CommentService.getInstance();

    public void index(){
        render("/WEB-INF/view/blog/search.html");
    }

    public void getResult(){
        String keyword = getPara("keyword");
        keyword = Encodes.urlDecode(keyword);
        Map<String,List<Map<String, Object>>> response = new HashMap<String, List<Map<String, Object>>>();
        List<Map<String, Object>> article_list = contentManager.searchArticleList(keyword);
        response.put("articleList", article_list);
        renderJson(response);
    }
}
