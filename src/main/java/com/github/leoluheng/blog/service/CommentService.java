package com.github.leoluheng.blog.service;

import com.github.leoluheng.blog.config.Config;
import com.github.leoluheng.blog.entity.CommentAdder;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommentService {

    private static CommentService instance;

    private CommentService() {
    }

    public synchronized static CommentService getInstance() {
        if (null == instance) {
            return new CommentService();
        }
        return instance;
    }

    public List<Map<String, Object>> getCommentList(int article_id) {
        List<Map<String, Object>> comment_list = new ArrayList<Map<String, Object>>();
        List<CommentAdder> commentSheet = CommentAdder.dao.findByCache(Config.CACHE_BLOG, "commentList",
                "select user1.img as user_img, user1.username as user_username, comment1.parent_id as parent, " +
                        "comment1.text as text, format(comment1.create_time, 'YYYY-MM-DD HH:II:SS') as create_time, " +
                        "comment1.id as comment_id, user2.username as parentUser_username, comment2.text as parent_text " +
                        "FROM `vmaig_comments_comment` comment1 LEFT Join vmaig_comments_comment comment2 on " +
                        "comment1.parent_id = comment2.id left join `vmaig_auth_vmaiguser` user1 on user1.id " +
                        "= comment1.user_id LEFT join `vmaig_auth_vmaiguser` user2 on user2.id = comment2.user_id" +
                        " where comment1.article_id = ? order by comment1.create_time DESC", article_id);
        CommentAdder comment;
        for (int i = 0; i < commentSheet.size(); i++) {
            comment = commentSheet.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            String img = comment.get("user_img");
            if (img == null | StrKit.isBlank(img)) {
                map.put("user_img", "");
            } else {
                map.put("user_img", comment.get("user_img"));
            }
            map.put("showImg", Boolean.toString(!StrKit.isBlank(img)));
            map.put("user_username", comment.get("user_username"));
            map.put("parent", comment.get("parent"));
            map.put("text", comment.get("text"));

            //format time to 24hr
            map.put("create_time", comment.get("create_time"));
            map.put("commentId", comment.get("comment_id"));
            map.put("parentUser_username", comment.get("parentUser_username"));
            map.put("parent_text", comment.get("parent_text"));
            comment_list.add(map);
        }
        return comment_list;
    }

    public int getCommentsNum(int article_id) {
        List<CommentAdder> commentSheet = CommentAdder.dao.findByCache(Config.CACHE_BLOG, "commentList-article"
                + article_id, "select article_id from vmaig_comments_comment where article_id = ?", article_id);
        return commentSheet.size();
    }

    public List<Map<String, Object>> getLatestComments() {
        List<Map<String, Object>> latest_comments = new ArrayList<Map<String, Object>>();

        // fetch only 10 latest comments
        List<CommentAdder> commentSheet = CommentAdder.dao.find("SELECT `article`.en_title AS article_en_title," +
                " `comment`.text AS text,`comment`.user_id AS user_id,`user`.img AS user_img,`user`.username AS username " +
                "FROM vmaig_comments_comment AS `comment`, vmaig_auth_vmaiguser AS `user`, blog_article AS `article`" +
                " WHERE `comment`.user_id=`user`.id AND `comment`.article_id = `article`.id order by comment.create_time" +
                " DESC LIMIT 10");

        for (int i = 0; i < commentSheet.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            CommentAdder comment = commentSheet.get(i);

            map.put("enTitle", comment.get("article_en_title"));
            map.put("text", comment.get("text"));
            map.put("user_id", comment.get("user_id"));
            if (comment.get("user_img") == null) {
                map.put("user_img", "");
            } else {
                map.put("user_img", comment.get("user_img"));
            }
            map.put("username", comment.get("username"));
            map.put("hasOwnImg", !(comment.get("user_img") == null));
            latest_comments.add(map);
        }
        return latest_comments;
    }

    public int saveComment(int article_id, int user_id, String comment) {
        Date now = new Date();
        DateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String create_time = simpleformat.format(now);
        UserService userManager = UserService.getInstance();

        String content; //text
        int parent_id;  //parent_id

        if (!comment.substring(0, 1).contains("@")) {
            //parent_id === null
            parent_id = -1;
            content = comment;
        } else {
            content = comment.substring(comment.indexOf(":") + 1);
            parent_id = Integer.parseInt(comment.substring(comment.indexOf(",") + 2, comment.indexOf("]")));
        }

        CacheKit.remove(Config.CACHE_BLOG, "commentList");
        CacheKit.remove(Config.CACHE_BLOG,"commentList-article"+article_id);


        new CommentAdder().set("text", content).set("article_id", article_id).
                set("parent_id", parent_id).set("user_id", user_id).set("create_time", create_time).save();

        // set a notification to the author of parent comment
        if(parent_id != -1){
            userManager.sendNotification("reply",user_id, parent_id, article_id);
        }

        return getCommentId(user_id, create_time, article_id);
    }

    private int getCommentId(int user_id, String create_time, int article_id) {
        CommentAdder commentSheet = CommentAdder.dao.findFirst("select id from vmaig_comments_comment where " +
                "create_time = ? AND article_id = ? AND user_id = ?", create_time, article_id, user_id);
        if (commentSheet == null) {
            return -1;
        } else {
            return commentSheet.get("id");
        }
    }

    public Map<String, String> getViewData(String username, int commentId) {
        Map<String, String> commentData = new HashMap<String, String>();
        UserService userManager = UserService.getInstance();

        CommentAdder commentSheet = CommentAdder.dao.findFirst("select parent_id from vmaig_comments_comment " +
                "where id = ?", commentId);

        int parent_id = commentSheet.get("parent_id");

        if (-1 == parent_id) {
            commentSheet = CommentAdder.dao.findFirst("select format(create_time, 'YYYY-MM-DD HH:II:SS') as create_time, text from vmaig_comments_comment" +
                    " where id = ?", commentId);
            commentData.put("parent", "-1");

        } else {
            commentSheet = CommentAdder.dao.findFirst("select format(comment1.create_time, 'YYYY-MM-DD HH:II:SS') as create_time, " +
                    "comment1.text as text, comment2.text as parent_text,comment2.id as parent_id, user.username as parent_author" +
                    " from vmaig_comments_comment as comment1, vmaig_comments_comment as comment2, " +
                    "vmaig_auth_vmaiguser as user where comment1.id = ? AND comment1.parent_id = comment2.id " +
                    "AND comment2.user_id = user.id", commentId);
            commentData.put("parent", commentSheet.get("parent_id").toString());
            commentData.put("parent_text", commentSheet.get("parent_text").toString());
            commentData.put("parentUser_username", commentSheet.get("parent_author").toString());

        }
        commentData.put("create_time", commentSheet.get("create_time").toString());
        commentData.put("text", commentSheet.get("text").toString());
        commentData.put("user_img", userManager.getTx(username));
        commentData.put("showImg", Boolean.toString(!StrKit.isBlank(commentData.get("user_img"))));
        commentData.put("user_username", username);
        commentData.put("commentId", Integer.toString(commentId));

        return commentData;
    }

}
