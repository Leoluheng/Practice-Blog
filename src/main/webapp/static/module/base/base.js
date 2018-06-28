(function (window) {
    var vm_sideWidgets = new Vue({
        el: "#vmaig-side",
        data: {
            "hot_article_list": [],
            "latest_comment_list": [],
            "links": []
        }
    });
    window.vm_sideWidgets = vm_sideWidgets;

    $.ajax({
        type: "POST",
        url: "/sideWidgets",
        dataType: "json",
        success: function (data, textStatus) {
            // var error = data["error"];
            var hotArticleList = data["hot_article_list"];
            var latestCommentList = data["latest_comment_list"];

            Vue.filter("trimText", function (text) {
                if (text.length > 200) {
                    return text.substring(0, 200) + "...";
                } else {
                    return text;
                }
            });

            if (hotArticleList.length === 0) {
                $("#hotest-post-list").empty();
            } else {
                vm_sideWidgets.hot_article_list = hotArticleList;
            }
            if (latestCommentList.length === 0) {
                $("#latest-comment-list").empty();
                // }else if(latestCommentList.length > 10){
                //     vm_sideWidgets.latest_comment_list = latestCommentList.slice(0,10);
            } else {
                vm_sideWidgets.latest_comment_list = latestCommentList;
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            alert(XMLHttpRequest.responseText);
        }

    });
    // return false
})(window);

