package com.github.leoluheng.blog.service;

import com.github.leoluheng.blog.entity.CategoryAdder;
import com.github.leoluheng.blog.entity.ContentAdder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentService {

    public Map<Integer, Map<String, Object>> get_carousel_page_list() {
        Map<Integer, Map<String, Object>> carousel_page_list = new HashMap<Integer, Map<String, Object>>();
        List<ContentAdder> contentSheet = ContentAdder.dao.find("select article.en_title as article_en_title, carousel.img as img, " +
                "carousel.title as title, carousel.summary as summary from `blog_article` article, `blog_carousel` carousel where carousel.article_id = article.id");
        ContentAdder content;
        for(int i = 0; i < contentSheet.size(); i++){
            content = contentSheet.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("article_en_title", content.get("article_en_title"));
            map.put("img", content.get("img"));
            map.put("title", content.get("title"));
            map.put("summary", content.get("summary"));
            carousel_page_list.put(i,map);
        }
        return carousel_page_list;
    }
    public Map<Integer, Map<String, Object>> search_article_list(String keyword){
        Map<Integer, Map<String, Object>> result_list = new HashMap<Integer,Map<String, Object>>();
        List<ContentAdder> resultSheet = ContentAdder.dao.find("select article.en_title as en_title, article.title as title, " +
                "article.tags as tags, article.img as img, article.summary as summary, format(article.pub_time, 'YYYY-MM-DD') as pub_time," +
                " article.view_times as view_times, category.name as categoryName, article.id as article_id " +
                "from `blog_article` article, `blog_category` category where category.id = article.category_id AND article.content like '%?%'", keyword);
        ContentAdder result;

        CommentService commentManager = new CommentService();

        for(int i = 0; i < resultSheet.size(); i++){
            result = resultSheet.get(i);
            Map<String, Object> map = new HashMap<String, Object>();


            map.put("en_title", result.get("en_title"));
            map.put("categoryName", tagMap(result));
            map.put("title", result.get("title"));
            map.put("get_tags", tagMap(result));
            map.put("summary", result.get("summary"));
            map.put("pub_time", result.get("pub_time"));
            int article_id = result.get("article_id");
            map.put("comment_num", commentManager.get_Comments_Num(article_id));
            map.put("view_times", result.get("view_times"));
            map.put("img", result.get("img"));

            map.put("page_obj", get_page_obj(i, resultSheet.size()));
            result_list.put(i,map);
        }
        return result_list;
    }
    public Map<String, Object> get_page_obj(int i, int n){
        Map<String, Object> page_obj = new HashMap<String, Object>();
        int pageNum = 2;
        if(1 != i && 1 == i%7){
            pageNum++;
            page_obj.put("has_previous", true);
            page_obj.put("number", pageNum);
            page_obj.put("previous_page_number", pageNum - 1);
            if((n - i - 1) > 8) {
                page_obj.put("has_next", true);
                page_obj.put("next_page_number", pageNum + 1);
            }else{
                page_obj.put("has_next", false);
            }
        }else{
            page_obj.put("has_previous", false);
            page_obj.put("number", pageNum - 1);
            page_obj.put("has_next", false);
        }
        return page_obj;
    }

    public Map<Integer,Map<String, Object>> get_article_list(String selector) {
        List<String> categories = new ArrayList<String>();
        categories.add("python");
        categories.add("django");
        categories.add("linux");
        categories.add("PE");
        categories.add("OllyDbg");
        categories.add("汇编");
        categories.add("其他");

        Map<String, String> sqlCommands = new HashMap<String, String>();
        sqlCommands.put("index", "select * from blog_article order by blog_article.pub_time DESC");
        sqlCommands.put("category", "select article.en_title as en_title, article.title as title, " +
                "article.tags as tags, article.img as img, article.summary as summary, format(article.pub_time, 'YYYY-MM-DD') as pub_time," +
                " article.view_times as view_times, category.name as categoryName, article.id as article_id " +
                "from `blog_article` article, `blog_category` category where category.name = ? AND category.id = article.category_id");
        sqlCommands.put("all","select * from blog_article");

        String sqlCommand;
        String category = "";
        if(categories.contains(selector)){
            category = selector;
            selector = "category";
        }

        sqlCommand = sqlCommands.get(selector);
        List<ContentAdder> contentSheet;

        CommentService commentManager = new CommentService();

        if(category.equals("")) {
            contentSheet = ContentAdder.dao.find(sqlCommand);
        }else{
            contentSheet = ContentAdder.dao.find(sqlCommand, category);
        }

        Map<Integer, Map<String, Object>> article_list = new HashMap<Integer,Map<String, Object>>();

        for(int i = 0; i < contentSheet.size(); i++){

            ContentAdder article = contentSheet.get(i);

            Map<String, Object> map = new HashMap<String, Object>();

            map.put("en_title", article.get("en_title"));
            map.put("categoryName", tagMap(article));
            map.put("title", article.get("title"));
            map.put("get_tags", tagMap(article));
            map.put("summary", article.get("summary"));
            map.put("pub_time", article.get("pub_time"));
            int article_id = article.get("article_id");
            map.put("comment_num", commentManager.get_Comments_Num(article_id));
            map.put("view_times", article.get("view_times"));
            map.put("img", article.get("img"));

            map.put("page_obj", get_page_obj(i, contentSheet.size()));
            article_list.put(i,map);
        }
        return article_list;
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

    public String get_article_category(String categoryName) {
        return "/category/" + categoryName ; 
    }

    public Map<String, Object> get_article(String en_title) {
        Map<String, Object> article = new HashMap<String, Object>();
        ContentAdder contentSheet = ContentAdder.dao.findFirst("select category.name as category, " +
                "format(article.pub_time,'YYYY-MM-DD') as pub_time, article.view_times as view_times, article.id as article_id " +
                "user.username as author, article.title as title, article.tags as tags, article.content as content from " +
                "`blog_article` article, `blog_category` as category, `vmaig_auth_vmaiguser` user where article.en_title = ? AND article.category_id = category.id AND" +
                "article.author_id = user.id", en_title);
        article.put("category", contentSheet.get("category"));
        article.put("pub_time", contentSheet.get("pub_time"));
        article.put("view_times", contentSheet.get("view_times"));
        article.put("author", contentSheet.get("author"));
        article.put("title", contentSheet.get("title"));
        article.put("tags", contentSheet.get("tags"));
        article.put("content", contentSheet.get("content"));

        return article;
    }

    public Map<Integer, Map<String, Object>> get_hot_article_list() {
        Map<Integer, Map<String, Object>> hot_article_list = new HashMap<Integer, Map<String, Object>>();
        List<ContentAdder> contentSheet = ContentAdder.dao.find("select article.en_title as en_title, article.title as title, " +
                "article.view_times as view_times from `blog_article` article order by view_times LIMIT 10");
        ContentAdder content;
        for(int i = 0; i < contentSheet.size(); i++){
            content = contentSheet.get(i);
            Map<String, Object>map = new HashMap<String, Object>();
            map.put("en_title", content.get("en_title"));
            map.put("title", content.get("title"));
            map.put("view_times", content.get("view_times"));
            hot_article_list.put(i,map);
        }
        return hot_article_list;
    }

    private List<String> tagMap (ContentAdder article){
        String tags = article.get("tags");
        return get_tags(tags);
    }

    public List<String> get_tags(String tags) {
        List<String> tagList = new ArrayList<String>();
        int g = 0;
        while(0 != tags.length()) {
            g = tags.indexOf(",");
            if(-1 == g){
                tagList.add(tags);
                break;
            }
            String tag = tags.substring(0, g);
            tagList.add(tag);
            tags = tags.substring(g);
        }
        return tagList;
    }

//    public String get_format_date(int article_id, String format){
//        ContentAdder contentSheet = ContentAdder.dao.findById(article_id);
//        return ;
//    }
}
