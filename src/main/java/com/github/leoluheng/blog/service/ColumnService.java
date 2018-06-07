package com.github.leoluheng.blog.service;

import com.github.leoluheng.blog.entity.ColumnAdder;

public class ColumnService {
    public String get_summary(String column_name) {
        ColumnAdder columnSheet = ColumnAdder.dao.findFirst("select summary from blog_column where name = ?", column_name);
        return columnSheet.get("summary");
    }
}
