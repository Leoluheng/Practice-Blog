package com.github.leoluheng.blog.entity;

import com.jfinal.plugin.activerecord.Model;

public class CommentAdder extends Model<CommentAdder> {
    public static final CommentAdder dao = new CommentAdder().dao();
}
