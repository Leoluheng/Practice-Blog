(function () {
    var category = location.search.substring(10);
    $.ajax({
        type: "post",
        url: "/category/doCategoryArticle",
        dataType: "json",
        data: {
            "category": category
        },
        success: function (data, textStatus) {
            var articleList = data["articleList"];

            if (articleList === 0) {
                $("#all-post-list").empty().html("<div class='home-post well clearfix'>\n<div class='post-title underline clearfix'>" +
                    "\n<h1>There is no articles posted yet!!!!</h1></div></div>");
            } else {
                var vm_article = new Vue({
                    el: "#all-post-list",
                    data: {
                        "article_list": articleList
                    },
                    filters: {
                        trimText: function (text) {
                            if (text.length > 200) {
                                return text.substring(0, 200) + "...";
                            } else {
                                return text;
                            }
                        },
                        trimDate: function(date){
                            if (date.length > 10) {
                                return date.substring(0, 10);
                            } else {
                                return date;
                            }
                        }
                    }
                });
            }
        },
        error: {

        }
    });
})();