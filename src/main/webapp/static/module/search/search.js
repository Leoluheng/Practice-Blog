(function(){
    var keyword = location.search.substring(3);
    $.ajax({
        type: "post",
        url: "/search/getResult",
        dataType: "json",
        data: {
            "keyword": keyword
        },
        success: function (data, textStatus) {
            var articleList = data["articleList"];
            if (articleList.length === 0) {
                $("#all-post-list .all-post").empty().html('<div class="post-content"><i>No posts related to "' +
                    keyword + '" is found</i></div>');
            } else {
                var vm_searchResult = new Vue({
                    el: "#all-post-list",
                    data: {
                        "articleList": articleList
                    },
                    filters: {
                        trimText : function(text){
                            if(text.length > 200){
                                return text.substring(0, 200) + "...";
                            }else{
                                return text;
                            }
                        }
                    }
                });
            }
        },
        error: function () {

        }
    });
})();