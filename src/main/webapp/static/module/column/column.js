(function () {
    var column = location.search.substring(8);
    $.ajax({
        type: "post",
        url: "/column/getColumn",
        dataType: "json",
        data: {
            "column": column
        },
        success: function (data, textStatus) {
            var articleList = data["article_list"];
            var columnSummary = data["column_summary"];
            var summary = columnSummary[0]["summary"];

            if(summary === ""){
                $("#column-summary").text("*****This column doesn't hava a description*****");
            }else{
                $("#column-summary").text(summary);
            }

            if (articleList.length === 0) {
                $(".well").empty().html("<div class='home-post well clearfix'>\n<div class='post-title underline clearfix'>" +
                    "\n<h1>There is no articles posted yet!!!!</h1></div></div>");;
            } else {
                var vm_columnArticleList = new Vue({
                    el: "#column-post",
                    data: {
                        "article_list": articleList
                    },
                    filters: {
                        trimText: function(text){
                            // debugger
                            if (text.length > 200) {
                                return text.substring(0, 200) + "...";
                            } else {
                                return text;
                            }
                        },
                        trimDate: function(date){
                            // debugger
                            if (date.length > 10) {
                                return date.substring(0, 10)+"...";
                            } else {
                                return date;
                            }
                        }
                    }
                });
            }
        }
    });
})();