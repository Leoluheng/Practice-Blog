package com.github.leoluheng.blog.service;

import com.github.leoluheng.blog.entity.CategoryAdder;
import com.github.leoluheng.blog.entity.ContentAdder;
import com.jfinal.json.Json;
import org.eclipse.jetty.util.ajax.JSON;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentService {

    public Object get_carousel_page_list() {

        return null;
    }

    public Map<Integer,Map<String, Object>> get_article_list() {
        CommentService commentManager = new CommentService();
        List<ContentAdder> contentSheet = ContentAdder.dao.find("select title from blog_article");
        Map<Integer, Map<String, Object>> article_list = new HashMap<Integer,Map<String, Object>>();
        for(int i = 0; i < contentSheet.size(); i++){
            ContentAdder article = contentSheet.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("en_title", article.get("en_title"));
            map.put("categoryName", get_article_category(Integer.parseInt(article.get("category_id").toString())));
            map.put("title", article.get("title"));
            List<String> tagMap = new ArrayList<String>();
            String tags = article.get("tags");
            int g = 0;
            while(0 != tags.length()) {
                g = tags.indexOf(",");
                if(-1 == g){
                    tagMap.add(tags);
                    break;
                }
                String tag = tags.substring(0, g);
                tagMap.add(tag);
                tags = tags.substring(g);
            }
            map.put("get_tags", tagMap);
            map.put("img", article.get("img"));
            map.put("summary", article.get("summary"));
            map.put("pub_time", article.get("pub_time"));
            int article_id = article.get("article_id");
            map.put("comment_num", commentManager.get_Comments_Num(article_id));
            map.put("view_times", article.get("view_times"));

            article_list.put(i,map);
        }
        return null;
    }

    public Object get_page_obj() {
        return null;
    }

    public Map<String, String> get_category_list() {
        List<CategoryAdder> categorySheet = CategoryAdder.dao.find("select name from blog_category");
        Map<String, String> category_list  = new HashMap<String, String>();
        for(int i = 0; i < categorySheet.size(); i++){
            String str = categorySheet.get(i).get("name");
            category_list.put("name", str);
        }
        return category_list;
    }

    public String get_article_category(int id) {
        return "/category/" + "" ; //////////////////////////////////
    }

    public Object get_article() {
        return null;
    }

    public Object get_tags() {
        return null;
    }

    public Object get_hot_article_list() {
        return null;
    }
//    public String get_format_date(int article_id, String format){
//        ContentAdder contentSheet = ContentAdder.dao.findById(article_id);
//        return ;
//    }
}
