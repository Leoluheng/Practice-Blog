package com.github.leoluheng.blog.service;


import com.github.leoluheng.blog.entity.LinkAdder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkService {
    private static LinkService instance;

    private LinkService(){}

    public synchronized static LinkService getInstance(){
        if(instance == null){
            return new LinkService();
        }
        return instance;
    }
    public Map<Integer, Map<String, Object>> get_links() {
        Map<Integer, Map<String, Object>> link_list = new HashMap<Integer, Map<String, Object>>();
        String[] color = new String[]{"primary", "success", "info", "warning", "danger"};

        List<LinkAdder> linkSheet = LinkAdder.dao.find("select link.title as title, link.url as url from" +
                " `vmaig_system_link` link");
        LinkAdder link;
        for(int i = 0; i < linkSheet.size(); i++){
            link = linkSheet.get(i);
            Map<String, Object> map = new HashMap<String, Object>();

            map.put("title", link.get("title"));
            map.put("url", link.get("url"));
            map.put("color",color[(int)(Math.random()*5)]);
            link_list.put(i,map);
        }
        return link_list;
    }
}
