package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.service.CommentService;
import com.github.leoluheng.blog.service.UserService;
import com.jfinal.core.Controller;

import java.util.HashMap;
import java.util.Map;

public class CommentController extends Controller {
    public void index(){
        UserService userManager = UserService.getInstance();
        CommentService commentManager = CommentService.getInstance();

        String comment = getPara("comment");
        int article_id = Integer.parseInt(getPara("article_id")); //article_id
        String username = getSessionAttr("username");
        Map<String, String> viewData = new HashMap<String, String>();

        if(username == null){
            viewData.put("error", "please login first.");
            renderJson(viewData);
            return;
        }
        int user_id = userManager.getId(username);

        int commentId = commentManager.saveComment(article_id, user_id, comment);
        viewData = commentManager.getViewData( username, commentId);

        renderJson(viewData);
    }
}
