(function () {
    var address = location.search.substring(9);
    $.ajax({
        type: "post",
        url: "/article/getArticleContentAndComment",
        dataType: "Json",
        data: {
            "address": address
        },
        success: function (data, textStatus) {
            var article = data["article"]; //Map<String, object>
            var commentList = data["commentList"]; //List<Map<String, Object>>

            if (article === null) {
                $("#vmaig-content").html("<div><h1><b>The requested article does not exist.</b></h1></div>");
            } else {
                var vm_article = new Vue({
                    el: "#vmaig-content",
                    data: {
                        "article": article
                    },
                    filters: {
                        trimText: function (text) {
                            if (text.length > 200) {
                                return text.substring(0, 200) + "...";
                            } else {
                                return text;
                            }
                        }
                    }
                });
            }

            if (commentList.length === 0) {
                $(".vmaig-comment").find("ul").remove(); //or use empty() so that later comments added by this user can be simply inserted
            } else {
                var vm_comment = new Vue({
                    el: "#xxx",
                    data: {
                        "commentList": commentList
                    },
                    methods: {
                        reply: function(comment){
                            return CommentQuote(comment.user_username, comment.comment_id);
                        }

                    }
                });
                console.log(vm_comment.commentList);
            }

        },
        error: function () {

        }
    });
})();