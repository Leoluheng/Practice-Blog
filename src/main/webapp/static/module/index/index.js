(function (window) {
    var vm_homePost = new Vue({
        el: "#home-post",
        data: {
            "article_list": []
        }
    });

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

            Vue.filter("trimDate", function (date) {
                if (date.length > 10) {
                    return date.substring(0, 10);
                } else {
                    return date;
                }
            });

            if (articleList.length === 0) {
                $("#home-post-list").html("<div class='home-post well clearfix'>\n<div class='post-title underline clearfix'>" +
                    "\n<h1>There is no articles posted yet!!!!</h1></div></div>");
            }
            else {
                vm_homePost.article_list = articleList;
            }

            if (links.length !== 0) {
                // vm_links.links= links;
                window.vm_sideWidgets.links = links;
            } else {
                $("#links").empty().html("<div class='padding10 list-group collapse in'><h1>Our blog site" +
                    " has no friends...Sad</h1></div>");
            }
        }
    })
})(window);
