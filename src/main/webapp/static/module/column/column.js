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
                $(".well").empty();
            } else {
                var vm_columnArticleList = new Vue({
                    el: "#column-post",
                    data: {
                        "article_list": articleList
                    }
                });
            }
        }
    });
})();