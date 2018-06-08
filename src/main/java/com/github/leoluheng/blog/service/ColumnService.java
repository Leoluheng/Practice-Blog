package com.github.leoluheng.blog.service;

import com.github.leoluheng.blog.entity.ColumnAdder;

public class ColumnService {
    private static ColumnService instance;

    private ColumnService(){}

    public synchronized static ColumnService getInstance(){
        if(instance == null){
            return new ColumnService();
        }
        return instance;
    }

    public String get_summary(String column_name) {
        ColumnAdder columnSheet = ColumnAdder.dao.findFirst("select summary from blog_column where name = ?", column_name);
        return columnSheet.get("summary");
    }
}
