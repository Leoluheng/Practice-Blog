package com.github.leoluheng.blog.entity;

import com.jfinal.plugin.activerecord.Model;

public class NewsAdder extends Model<NewsAdder> {
    public static final NewsAdder dao = new NewsAdder().dao();
}
