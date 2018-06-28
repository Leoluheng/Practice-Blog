(function () {
    var address = location.search.substring(9);
    var articleId;
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
            var user_img = data["user_img"]; //String

            var changehrtoHR = function (hr) {
                return parseInt(hr) + 12;
            };

            function CommentQuote(user_name, commend_id) {
                comment = document.getElementById('comment');
                comment.value = "@['" + user_name + "', " + commend_id + "]: ";
                comment.focus();
                comment.setSelectionRange(comment.value.length, comment.value.length);
            }

            if (article === null) {
                $("#vmaig-content").html("<div><h1><b>The requested article does not exist.</b></h1></div>");
                if (commentList.length === 0) {
                    $(".vmaig-comment").find("ul").remove(); //or use empty() so that later comments added by this user can be simply inserted
                    var noComment = '<div class="vmaig-comment-content">Nobody left any comment yet...</div>';
                    $(".vmaig-comment").find(".vmaig-comment-edit").after(noComment);
                }
            } else {
                var vm_article = new Vue({
                    el: "#vmaig-content",
                    data: {
                        "article": article,
                        "commentList": commentList,
                        "user_img": user_img
                    },
                    filters: {
                        trimText: function (text) {
                            if (text.length > 200) {
                                return text.substring(0, 200) + "...";
                            } else {
                                return text;
                            }
                        },
                        trimDate: function (date) {
                            var list = date.split(',').join("");
                            return list.slice(0, 4) + "-" + list.slice(4, 6) + "-" + list.slice(6, 8) +
                                " " + changehrtoHR(list.slice(8, 10)).toString() + ":" + list.slice(10, 12) + ":" + list.slice(12);
                        }
                    },
                    methods: {
                        reply: function (username, id) {
                            return CommentQuote(username, id);
                        }
                    }
                });
                $('#submitComment').click(function () {
                    $.ajax({
                        type: "POST",
                        url: "/comment",
                        data: {
                            "comment": $("#comment").val(), //comment + parent_id
                            "article_id": articleId     //article_id
                            //user_id found in session
                        },
                        dataType: "json",
                        beforeSend: function (xhr) {
                            xhr.setRequestHeader("csrf-token", $.cookie('csrf-token'));
                        },
                        success: function (data, textStatus) {
                            var newComment;
                            if (data["error"] != null) {
                                var error = data["error"];
                                alert(error);
                                $("#comment").val("");
                                return;
                            }

                            vm_article.commentList.unshift(data);
                            $("#comment").val("");
                        },
                        error: function (XMLHttpRequest, textStatus, errorThrown) {
                            alert(XMLHttpRequest.responseText);
                        }

                    });
                    return false;
                });
            }
            articleId = article["article_id"];
        },
        error: function () {

        }
    });

})();