package com.github.leoluheng.blog.controller;

        import com.github.leoluheng.blog.service.ColumnService;
        import com.github.leoluheng.blog.service.CommentService;
        import com.github.leoluheng.blog.service.ContentService;
        import com.jfinal.core.Controller;

public class ColumnController extends Controller {

    ColumnService columnManager = ColumnService.getInstance();
    ContentService contentManager = ContentService.getInstance();
    CommentService commentManager = CommentService.getInstance();

    public void index(){
        String param = getPara("column");
        setAttr("column_summary", columnManager.getSummary(param));
        setAttr("article_list", contentManager.get_article_list(param));
        setAttr("hot_article_list", contentManager.get_hot_article_list());
        setAttr("latest_comment_list", commentManager.get_latest_comments());
        render("/WEB-INF/view/blog/column.html");
    }
}
