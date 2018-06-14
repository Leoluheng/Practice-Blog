(function () {
    $.ajax({
        type: "POST",
        url: "/sideWidgets",
        dataType: "json",
        success: function (data, textStatus) {
            // var error = data["error"];
            var hotArticleList = data["hot_article_list"];
            var latestCommentList = data["latest_comment_list"];

            Vue.filter("trimText", function(text){
                if(text.length > 200){
                    return text.substring(0, 200) + "...";
                }else{
                    return text;
                }
            });

            if(hotArticleList.length === 0){
                $("#hotest-post-list").empty();
            }else{
                var vm_hotArticle = new Vue({
                    el: "#hotest-post-list",
                    data: {
                        "hot_article_list": hotArticleList
                    }
                });
            }

            if(latestCommentList.length === 0){
                $("#latest-comment-list").empty();
            }else{
                var vm_latestComment = new Vue({
                   el: "#latest-comment-list",
                   data: {
                       "latest_comment_list": latestCommentList
                   }
                });
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            alert(XMLHttpRequest.responseText);
        }

    });
    // return false
})();

