package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.service.CommentService;
import com.github.leoluheng.blog.service.ContentService;
import com.github.leoluheng.blog.service.UserService;
import com.jfinal.core.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void index(String param) {

        Map<String, Object> article = contentManager.get_article(param);
        String tags = article.get("tags").toString();

        setAttr("category_url", contentManager.get_article_category(article.get("category").toString()));
        setAttr("article", article);
        setAttr("tags", contentManager.get_tags(tags));

        String thisUsername = getSessionAttr("username");
        setAttr("user_img", userManager.getTx(thisUsername));

        setAttr("comment_list", commentManager.get_Comment_List(Integer.parseInt(article.get("article_id").toString())));

        setAttr("latest_comment_list", commentManager.get_latest_comments());
        setAttr("hot_article_list", contentManager.get_hot_article_list());
        render("/blog/article.html");
    }

    // protected methods

    // friendly methods

    // private methods

    // inner class

    // test main
}
