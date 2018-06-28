(function () {
    var tag = location.search.substring(5);
    $.ajax({
        type: "post",
        url: "/tag/getTagPage",
        dataType: "json",
        data: {
            "tag": tag
        },
        success: function (data, textStatus) {
            var article_list = data["article_list"];

            if (article_list.length === 0) {
                $("#all-post-list").empty().html("<div class='home-post well clearfix'>\n<div class='post-title underline clearfix'>" +
                    "\n<h1>There is no articles posted yet!!!!</h1></div></div>");
            } else {
                var vm_tag = new Vue({
                    el: "#all-post-list",
                    data: {
                        "article_list": article_list
                    },
                    filters: {
                        trimText: function(text){
                            if(text.length > 200){
                                return text.substring(0,200) + "...";
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