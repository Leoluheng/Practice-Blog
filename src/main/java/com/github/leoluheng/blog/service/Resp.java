package com.github.leoluheng.blog.service;

import java.util.Map;

public class Resp {
    private static Resp instance;

    private Resp() {}
    public synchronized static Resp getInstance(){
        if(instance == null){
            return new Resp();
        }else{
            return instance;
        }
    }

    private boolean result;
    private String message;
    private Object responseData;

    public void setResult(boolean rt) {
        this.result = rt;
    }

    public void setMsg(String msg) {
        this.message = msg;
    }

    public void setData(Map<String, String> map) {
        this.responseData = map;
    }

    public String getResponse() {
        return message;
    }
}
