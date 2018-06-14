package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.service.CommentService;
import com.github.leoluheng.blog.service.ContentService;
import com.github.leoluheng.blog.service.UserService;
import com.jfinal.core.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleController extends Controller {
    ContentService contentManager = ContentService.getInstance();
    UserService userManager = UserService.getInstance();
    CommentService commentManager = CommentService.getInstance();
    // members
    // static block
    static {

    }
    // constructors

    // properties

    // public methods

    public void index() {
        render("/WEB-INF/view/blog/article.html");
    }

    public void getArticleContentAndComment(){
        Map<String, Object> response = new HashMap<String, Object>();

        String param = getPara("address");
        Map<String, Object> article = contentManager.get_article(param);
        response.put("article", article);

        int article_id = Integer.parseInt(article.get("article_id").toString());
        response.put("commentList", commentManager.get_Comment_List(article_id));

        renderJson(response);
    }

    // protected methods

    // friendly methods

    // private methods

    // inner class

    // test main
}
