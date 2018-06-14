(function () {
    $.ajax({
        type: "POST",
        url: "/doIndex",
        dataType: "json",
        success: function (data, textStatus) {
            // var error = data["error"];
            var links = data["links"];
            var articleList = data["article_list"];

            Vue.filter("trimText", function (text) {
                if (text.length > 200) {
                    return text.substring(0, 200) + "...";
                } else {
                    return text;
                }
            });

            if (articleList.length === 0) {
                $("#home-post-list").html("<div class='home-post well clearfix'>\n<div class='post-title underline clearfix'>" +
                    "\n<h1>There is no articles posted yet!!!!</h1></div></div>");
            }
            else {
                var vm_homePost = new Vue({
                    el: "#homePost",
                    data: {
                        "article_list": articleList
                    }
                });
            }

            if(links.length === 0){
                $("#links").empty();
            }else{
                var vm_links = new Vue({
                    el: "#links",
                    data: {
                        "links": links
                    }
                });
            }
        }
    })
})();
