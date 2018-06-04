package com.github.leoluheng.blog.controller;

import com.jfinal.core.Controller;

public class CategoryController extends Controller {

    public void index(String param){
        render("category.html");
        setAttr("","");
    }

}
