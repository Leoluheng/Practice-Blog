package com.github.leoluheng.blog.entity;

import com.jfinal.plugin.activerecord.Model;

public class ColumnAdder extends Model<ColumnAdder> {
    public static final ColumnAdder dao = new ColumnAdder().dao();
}
