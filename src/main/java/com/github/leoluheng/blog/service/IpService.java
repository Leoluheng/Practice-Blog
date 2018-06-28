package com.github.leoluheng.blog.service;

import com.github.leoluheng.blog.entity.IpAdder;

public class IpService {

    private static IpService instance;
    private IpService(){}

    public synchronized static IpService getInstance(){
        if(instance == null){
            return new IpService();
        }else{
            return instance;
        }
    }

    public void seenIp(int articleId, String ip){
        if(ipHasReadArticle(articleId,ip)){
            new IpAdder().set("ip", ip).set("articleId",articleId).save();
        }
    }

    public boolean ipHasReadArticle(int articleId, String ip) {
        IpAdder ipSheet = IpAdder.dao.findFirst("select * from vmaig_user_ip where ip =" +
                " ? AND articleId = ?", ip, articleId);
        return ipSheet == null;

    }
}
