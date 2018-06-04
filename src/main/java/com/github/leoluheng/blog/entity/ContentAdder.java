package com.github.leoluheng.blog.entity;

import com.jfinal.plugin.activerecord.Model;

public class ContentAdder extends Model<ContentAdder> {
    public static final ContentAdder dao = new ContentAdder().dao();

}
