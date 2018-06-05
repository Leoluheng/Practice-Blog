package com.github.leoluheng.blog.service;

import com.github.leoluheng.blog.entity.NavAdder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavService {
    public Map<Integer, Map<String, Object>> get_nav_list() {
        Map<Integer, Map<String, Object>> nav_list = new HashMap<Integer, Map<String, Object>>();
        List<NavAdder> navSheet = NavAdder.dao.find("select nav.name as name, nav.url as url from `blog_nav` nav");
        NavAdder nav;
        for(int i = 0; i < navSheet.size(); i++){
            nav = navSheet.get(i);
            Map<String, Object> map = new HashMap<String, Object>();

            map.put("name", nav.get("name"));
            map.put("url", nav.get("url"));
            nav_list.put(i,map);
        }
        return nav_list;
    }
}
