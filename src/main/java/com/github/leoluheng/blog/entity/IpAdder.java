package com.github.leoluheng.blog.entity;

import com.jfinal.plugin.activerecord.Model;

public class IpAdder extends Model<IpAdder> {
    public final static IpAdder dao = new IpAdder().dao();
}
