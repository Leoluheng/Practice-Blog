package com.github.leoluheng.blog.service;

import com.github.leoluheng.blog.config.Config;
import com.github.leoluheng.blog.entity.CategoryAdder;
import com.github.leoluheng.blog.entity.ContentAdder;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.jfinal.kit.StrKit;

import javax.swing.text.AbstractDocument;
import java.util.*;

public class ContentService {

    private static ContentService instance;

    private ContentService() {
    }

    public synchronized static ContentService getInstance() {
        if (instance == null) {
            return new ContentService();
        }
        return instance;
    }

    public void updateViewtimes(int articleId, String ip) {
        IpService ipManager = IpService.getInstance();
        if (articleId == -1) {
            return;
        }
        if (ipManager.ipHasReadArticle(articleId, ip)) { //return true if ip haven't seen before == new view
            ContentAdder articleSheet = ContentAdder.dao.findById(articleId);
            int viewTimes = articleSheet.get("view_times");
            articleSheet.set("view_times", ++viewTimes).update();
        }
    }

    public List<Map<String, Object>> getCarouselPageList() {
        List<Map<String, Object>> carousel_page_list = new ArrayList<Map<String, Object>>();
        List<ContentAdder> contentSheet = ContentAdder.dao.find("select article.en_title as article_en_title, carousel.img as img, " +
                "carousel.title as title, carousel.summary as summary from `blog_article` article, `blog_carousel` carousel where carousel.article_id = article.id");
        ContentAdder content;
        for (ContentAdder aContentSheet : contentSheet) {
            content = aContentSheet;
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("article_en_title", content.get("article_en_title"));
            map.put("img", content.get("img"));
            map.put("title", content.get("title"));
            map.put("summary", content.get("summary"));
            carousel_page_list.add(map);
        }
        return carousel_page_list;
    }


    ///////////////////////////////////////////
    public List<Map<String, Object>> searchArticleList(String keyword) {
        String term = "%";
        term += keyword + "%";
        List<Map<String, Object>> result_list = new ArrayList<Map<String, Object>>();
        List<ContentAdder> resultSheet = ContentAdder.dao.find("SELECT article.en_title AS enTitle,article.title AS title," +
                "article.tags AS tags,article.img AS img,article.summary AS summary,article.pub_time AS pub_time,article.view_times AS view_times," +
                "category.NAME AS category,article.id AS article_id FROM `blog_article` article,`blog_category` category " +
                "WHERE category.id=article.category_id AND (article.content LIKE ? OR article.title LIKE ? OR " +
                "article.summary LIKE ? OR article.tags LIKE ?)", term, term, term, term);
        ContentAdder result;

        CommentService commentManager = CommentService.getInstance();

        for (ContentAdder contentAdder : resultSheet) {
            result = contentAdder;
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("enTitle", result.get("enTitle"));
            map.put("category", result.get("category"));
            map.put("title", result.get("title"));
            map.put("getTags", tagMap(result, "tags"));
            map.put("summary", result.get("summary"));
            map.put("pub_time", result.get("pub_time"));
            int article_id = result.get("article_id");
            map.put("comment_num", commentManager.getCommentsNum(article_id));
            map.put("view_times", result.get("view_times"));
            map.put("img", result.get("img"));

            map.put("page_obj", null);////////////////////////////
            result_list.add(map);
        }
        return result_list;
    }

    public Map<String, Object> getPageObj(int i, int n) {
        Map<String, Object> page_obj = new HashMap<String, Object>();
        int pageNum = 2;
        if (1 != i && 1 == i % 7) {
            pageNum++;
            page_obj.put("has_previous", true);
            page_obj.put("number", pageNum);
            page_obj.put("previous_page_number", pageNum - 1);
            if ((n - i - 1) > 8) {
                page_obj.put("has_next", true);
                page_obj.put("next_page_number", pageNum + 1);
            } else {
                page_obj.put("has_next", false);
            }
        } else {
            page_obj.put("has_previous", false);
            page_obj.put("number", pageNum - 1);
            page_obj.put("has_next", false);
        }
        return page_obj;
    }

    public List<Map<String, Object>> getArticleList(String selector) {
        List<String> categories = new ArrayList<String>();
        categories.add("python");
        categories.add("django");
        categories.add("linux");
        categories.add("PE");
        categories.add("OllyDbg");
        categories.add("汇编");
        categories.add("其他");
        ////////////////////////////////
        List<String> tags = new ArrayList<String>();
        tags.add("其他");
        tags.add("测试");
        ////////////////////////////////

        Map<String, String> sqlCommands = new HashMap<String, String>();
        sqlCommands.put("index", "select article.en_title as enTitle, article.title as title, " +
                "article.tags as tags, article.img as img, article.summary as summary, article.pub_time as pub_time, " +
                "article.view_times as view_times, category.name as category, article.id as id " +
                "from `blog_article` article, `blog_category` as category order by pub_time DESC");
        sqlCommands.put("category", "select article.en_title as enTitle, article.title as title, " +
                "article.tags as tags, article.img as img, article.summary as summary, article.pub_time as pub_time," +
                " article.view_times as view_times, category.name as category, article.id as id " +
                "from `blog_article` article, `blog_category` as category where category.name = ? AND category.id = article.category_id");
        sqlCommands.put("all", "select article.en_title as enTitle, article.title as title, " +
                "article.tags as tags, article.img as img, article.summary as summary, article.pub_time as pub_time, " +
                "article.view_times as view_times, category.name as category, article.id as id  from blog_article article, blog_category category where category.id = article.category_id");
        sqlCommands.put("tag", "select article.en_title as enTitle, article.title as title, " +
                "article.tags as tags, article.img as img, article.summary as summary, article.pub_time as pub_time, " +
                "article.view_times as view_times, category.name as category, article.id as id  from blog_article article, blog_category category where category.id = article.category_id" +
                " AND article.tags like ?");
        String sqlCommand;
        String keyword = "";
        if (categories.contains(selector)) {
            keyword = selector;
            selector = "category";
        } else if (tags.contains(selector)) {
            keyword = "%" + selector + "%";
            selector = "tag";
        }

        sqlCommand = sqlCommands.get(selector);
        List<ContentAdder> contentSheet;

        CommentService commentManager = CommentService.getInstance();

        if (keyword.equals("")) {
            //If using cache here... well some articles won't be fetched and displayed due to how this
            // api getArticleList is built... my bad(sobering facepalm).

            //But it is strongly recommended to use cache for better performance
//            contentSheet = ContentAdder.dao.findByCache(Config.CACHE_BLOG, "articleList",sqlCommand);
            contentSheet = ContentAdder.dao.find(sqlCommand);
        } else {
            contentSheet = ContentAdder.dao.find(sqlCommand, keyword);
        }

        List<Map<String, Object>> article_list = new ArrayList<Map<String, Object>>();

        for (ContentAdder contentAdder : contentSheet) {

            Map<String, Object> map = new HashMap<String, Object>();

            map.put("enTitle", contentAdder.get("enTitle"));
            String categoryName = contentAdder.get("category");
            map.put("category", categoryName);
            map.put("title", contentAdder.get("title"));
            List<String> tagMap = tagMap(contentAdder, "tags");
            map.put("getTags", tagMap);
            map.put("summary", contentAdder.get("summary"));
            map.put("pub_time", contentAdder.get("pub_time"));
            int article_id = contentAdder.get("id");
            map.put("comment_num", commentManager.getCommentsNum(article_id));
            map.put("view_times", contentAdder.get("view_times"));
            map.put("img", contentAdder.get("img"));
            map.put("showImg", !StrKit.isBlank(map.get("img").toString()));
            map.put("showTag", !tagMap.isEmpty());
            map.put("page_obj", null);///////////////////////////////
            article_list.add(map);
        }
        return article_list;
    }


    public Object getPageObj() {
        return null;
    }

    public List<Map<String, Object>> get_category_list() {
        List<CategoryAdder> categorySheet = CategoryAdder.dao.find("select name from blog_category");
        List<Map<String, Object>> category_list = new ArrayList<Map<String, Object>>();

        for (CategoryAdder categoryAdder : categorySheet) {
            Map<String, Object> map = new HashMap<String, Object>();
            String str = categoryAdder.get("name");
            map.put("name", str);
            category_list.add(map);
        }
        return category_list;
    }

    public String getArticleCategory(String categoryName) {
        return "/category/" + categoryName;
    }

    public Map<String, Object> getArticle(String en_title) {
        Map<String, Object> article = new HashMap<String, Object>();
        ContentAdder contentSheet = ContentAdder.dao.findFirstByCache(Config.CACHE_BLOG, "article-" + en_title, "select category.name as category, " +
                "article.pub_time as pub_time, article.view_times as view_times, article.id as article_id, " +
                "user.username as author, article.title as title, article.tags as tags, article.content as content from " +
                "`blog_article` as article, `blog_category` as category, `vmaig_auth_vmaiguser` as user where article.en_title = ? AND article.category_id = category.id AND" +
                " article.author_id = user.id", en_title);
        if (contentSheet == null) {
            return article;
        }
        article.put("category", contentSheet.get("category"));
        article.put("pub_time", contentSheet.get("pub_time"));
        article.put("view_times", contentSheet.get("view_times"));
        article.put("author", contentSheet.get("author"));
        article.put("title", contentSheet.get("title"));
        article.put("tags", contentSheet.get("tags"));
        article.put("content", contentSheet.get("content"));
        article.put("article_id", contentSheet.get("article_id"));

        return article;
    }

    public List<Map<String, Object>> getHotArticleList() {
        List<Map<String, Object>> hot_article_list = new ArrayList<Map<String, Object>>();
        List<ContentAdder> contentSheet = ContentAdder.dao.find("select article.en_title as enTitle, article.title as title, " +
                "article.view_times as view_times from `blog_article` article order by view_times LIMIT 10");
        ContentAdder content;
        for (ContentAdder aContentSheet : contentSheet) {
            content = aContentSheet;
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("enTitle", content.get("enTitle"));
            map.put("title", content.get("title"));
            map.put("view_times", content.get("view_times"));
            hot_article_list.add(map);
        }
        return hot_article_list;
    }

    private List<String> tagMap(ContentAdder article, String type) {
        String data = article.get(type);
        return getTags(data);
    }

    public List<String> getTags(String tags) {
        List<String> tagList = Lists.newArrayList();
//        int g = 0;
//        while(0 != tags.length()) {
//            g = tags.indexOf(",");
//            if(-1 == g){
//                tagList.add(tags);
//                break;
//            }
//            String tag = tags.substring(0, g);
//            tagList.add(tag);
//            tags = tags.substring(g+1);
//        }
        if (!StrKit.isBlank(tags)) {
            tagList = Splitter.on(",").splitToList(tags);
        }
        return tagList;
    }

//    public String getUserImg(String username) {
//        if (username == null) {
//            return "";
//        } else {
//            String img;
//            ContentAdder user = ContentAdder.dao.findFirstByCache(Config.CACHE_BLOG, "userTx", "select user.img from vmaig_auth_vmaiguser user " +
//                    "where user.username = ?", username);
//            img = user.get("img");
//            return img;
//        }
//    }

    public int getArticleId(String param) {
        ContentAdder articleSheet = ContentAdder.dao.findFirst("select id from blog_article where en_title = ?", param);
        if (articleSheet == null) {
            return -1;
        } else {
            return articleSheet.get("id");
        }
    }

//    public String get_format_date(int article_id, String format){
//        ContentAdder contentSheet = ContentAdder.dao.findById(article_id);
//        return ;
//    }
}
