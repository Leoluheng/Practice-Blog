package com.github.leoluheng.blog.service;

import com.github.leoluheng.blog.entity.CommentAdder;
import com.jfinal.kit.StrKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentService {

    private static CommentService instance;

    private CommentService(){}

    public synchronized static CommentService getInstance(){
        if(null == instance){
            return new CommentService();
        }
        return instance;
    }
    public List<Map<String, Object>> get_Comment_List(int article_id) {
        List<Map<String, Object>> comment_list = new ArrayList<Map<String, Object>>();
        List<CommentAdder> commentSheet = CommentAdder.dao.find("select user1.img as user_img, user1.username as user_username, " +
                "comment1.parent_id as parent, comment1.text as text, format(comment1.create_time, 'YYYY-MM-DD HH:II:SS') as create_time, " +
                "comment1.id as comment_id, user2.username as parentUser_username, comment2.text as parent_text " +
                "from `vmaig_comments_comment` comment1, `vmaig_comments_comment` comment2, `vmaig_auth_vmaiguser` user1, `vmaig_auth_vmaiguser` user2 " +
                "where comment1.article_id = ? AND comment2.id = comment1.parent_id AND user1.id = comment1.user_id AND user2.id = comment2.user_id", article_id);
        CommentAdder comment;
        for(int i = 0; i < commentSheet.size(); i++){
            comment = commentSheet.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("user_img", comment.get("user_img"));
            map.put("user_username", comment.get("user_username"));
            map.put("parent", comment.get("parent"));
            map.put("text", comment.get("text"));
            map.put("create_time", comment.get("create_time"));
            map.put("comment_id", comment.get("comment_id"));
            map.put("parentUser_username", comment.get("parentUser_username"));
            map.put("parent_text", comment.get("parent_text"));
            comment_list.add(map);
        }
        return comment_list;
    }

    public int get_Comments_Num(int article_id){
        List<CommentAdder> commentSheet = CommentAdder.dao.find("select article_id from vmaig_comments_comment where article_id = ?", article_id);
        return commentSheet.size();
    }

    public List<Map<String, Object>> get_latest_comments() {
        List<Map<String, Object>> latest_comments = new ArrayList<Map<String, Object>>();
        List<CommentAdder> commentSheet = CommentAdder.dao.find("SELECT `article`.en_title AS article_en_title, `comment`.text AS text,`comment`.user_id AS user_id,`user`.img AS user_img,`user`.username AS username " +
                "FROM vmaig_comments_comment AS `comment`, vmaig_auth_vmaiguser AS `user`, blog_article AS `article` WHERE `comment`.user_id=`user`.id AND `comment`.article_id = `article`.id");
        for(int i = 0; i < commentSheet.size(); i++){
            Map<String, Object> map = new HashMap<String, Object>();
            CommentAdder comment = commentSheet.get(i);

            map.put("enTitle", comment.get("article_en_title"));
            map.put("text", comment.get("text"));
            map.put("user_id", comment.get("user_id"));
            if(comment.get("user_img") == null){
                map.put("user_img", "");
            }else{
                map.put("user_img", comment.get("user_img"));
            }
            map.put("username", comment.get("username"));
            map.put("hasOwnImg", !(comment.get("user_img") == null));
            latest_comments.add(map);
        }
        return latest_comments;
    }
}
