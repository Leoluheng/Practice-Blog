(function(){
    $.ajax({
       type: "post",
       url: "/article/getArticleContent",
       dataType: "Json",
       success: function(data, textStatus){
           var article = data["article"]; //Map<String, object>
           var commentList = data["articleList"]; //List<Map<String, Object>>

           if(article === null){
               $("#vmaig-content").html("<div><h1><b>The requested article does not exist.</b></h1></div>");
           }else{
               var vm_article = new Vue({
                  el: ""
               });
           }
       },
       error: function(){

       }
    });
})();