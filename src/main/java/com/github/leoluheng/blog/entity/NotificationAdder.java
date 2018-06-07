package com.github.leoluheng.blog.entity;

import com.jfinal.plugin.activerecord.Model;

public class NotificationAdder extends Model<NotificationAdder> {
    public static final NotificationAdder dao = new NotificationAdder().dao();
}
