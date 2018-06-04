package com.github.leoluheng.blog.entity;

import com.jfinal.plugin.activerecord.Model;

public class CategoryAdder extends Model<CategoryAdder> {
    public static final CategoryAdder dao = new CategoryAdder().dao();
}
