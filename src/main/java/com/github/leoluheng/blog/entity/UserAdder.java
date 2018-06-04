package com.github.leoluheng.blog.entity;

import com.jfinal.plugin.activerecord.Model;

public class UserAdder extends Model<UserAdder> {
    public static final UserAdder dao = new UserAdder().dao();
}
