package com.github.leoluheng.blog.service;

import com.github.leoluheng.blog.entity.ColumnAdder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColumnService {
    private static ColumnService instance;

    private ColumnService(){}

    public synchronized static ColumnService getInstance(){
        if(instance == null){
            return new ColumnService();
        }
        return instance;
    }

    public String getSummary(String column_name) {
        ColumnAdder columnSheet = ColumnAdder.dao.findFirst("select summary from blog_column where name = ?", column_name);
        return columnSheet.get("summary");
    }

    public List<String> getColumnList() {
        List<ColumnAdder> columnSheet = ColumnAdder.dao.find("select name from blog_column");
       List<String> columnList = new ArrayList<String>();
        String columnName;
        for (ColumnAdder aColumnSheet : columnSheet) {
            columnName = aColumnSheet.get("name");
            columnList.add(columnName);
        }
        return columnList;
    }
}
