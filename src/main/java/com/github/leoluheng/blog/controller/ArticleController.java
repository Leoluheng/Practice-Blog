package com.github.leoluheng.blog.controller;

import com.github.leoluheng.blog.service.CommentService;
import com.github.leoluheng.blog.service.ContentService;
import com.github.leoluheng.blog.service.IpService;
import com.github.leoluheng.blog.service.UserService;
import com.jfinal.core.Controller;
import sun.net.util.IPAddressUtil;

import java.util.HashMap;
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
//        String address = getPara("address");
//        setAttr("address",address);
        render("/WEB-INF/view/blog/article.html");
    }

    public void getArticleContentAndComment() {
        String param = getPara("address");
        int articleId = contentManager.getArticleId(param);
        Map<String, Object> response = new HashMap<String, Object>();

        if (-1 == articleId) {
            renderJson(response);
        }
//        System.out.println(getRequest().getRemoteAddr());
        IpService ipManager = IpService.getInstance();
        String ip = getRequest().getRemoteAddr();
        ipManager.seenIp(articleId, ip);


        String username = getSessionAttr("username");
        String tx = userManager.getTx(username);
        //Maybe we can think of another way for login&identification ?
        response.put("user_img", tx);

        contentManager.updateViewtimes(articleId, ip);

        Map<String, Object> article = contentManager.getArticle(param);
        response.put("article", article);

        int article_id = Integer.parseInt(article.get("article_id").toString());
        response.put("commentList", commentManager.getCommentList(article_id));

        renderJson(response);
    }

    // protected methods

    // friendly methods

    // private methods

    // inner class

    // test main
}
