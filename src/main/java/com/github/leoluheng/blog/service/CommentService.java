package com.github.leoluheng.blog.service;

import com.github.leoluheng.blog.entity.CommentAdder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentService {

    public Object get_Object_comments() {
        return null;
    }

    public int get_Comments_Num(int article_id){
        List<CommentAdder> commentSheet = CommentAdder.dao.find("select article_id from vmaig_comments_comment where article_id = ?", article_id);
        return commentSheet.size();
    }

    public Map<Integer,Map<String, Object>> get_Object_latest_comments() {
        Map<Integer, Map<String, Object>> latest_comments = new HashMap<Integer, Map<String, Object>>();
        List<CommentAdder> commentSheet = CommentAdder.dao.find("SELECT `article`.en_title AS en_title, `comment`.text AS text,`comment`.user_id AS user_id,`user`.img AS user_img,`user`.username AS username " +
                "FROM vmaig_comments_comment AS `comment`, vmaig_auth_vmaiguser AS `user`, blog_article AS `article` WHERE `comment`.user_id=`user`.id AND `comment`.article_id = `article`.id");
        for(int i = 0; i < commentSheet.size(); i++){
            Map<String, Object> map = new HashMap<String, Object>();
            CommentAdder comment = commentSheet.get(i);

            map.put("en_title", comment.get("en_title"));
            map.put("text", comment.get("text"));
            map.put("user_id", comment.get("user_id"));
            map.put("user_img", comment.get("user_img"));
            map.put("username", comment.get("username"));

            latest_comments.put(i,map);
        }
        return latest_comments;
    }
}
