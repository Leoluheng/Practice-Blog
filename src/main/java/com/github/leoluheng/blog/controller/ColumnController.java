package com.github.leoluheng.blog.controller;

        import com.github.leoluheng.blog.service.ColumnService;
        import com.github.leoluheng.blog.service.CommentService;
        import com.github.leoluheng.blog.service.ContentService;
        import com.github.leoluheng.blog.utility.Encodes;
        import com.jfinal.core.Controller;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

public class ColumnController extends Controller {

    ColumnService columnManager = ColumnService.getInstance();
    ContentService contentManager = ContentService.getInstance();

    public void index(){
        render("/WEB-INF/view/blog/column.html");
    }

    public void getColumn (){
        String param = getPara("column");
        param = Encodes.urlDecode(param);
        String column_summry =  columnManager.getSummary(param);
        Map<String, List<Map<String, Object>>> response = new HashMap<String, List<Map<String, Object>>>();
        List<Map<String, Object>> articleList = contentManager.get_article_list(param);

        List<Map<String, Object>> summary = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("summary", column_summry);
        summary.add(map);

        response.put("column_summary", summary);
        response.put("article_list", articleList);
        renderJson(response);
    }
}
