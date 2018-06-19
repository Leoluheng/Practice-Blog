(function(){
    $.ajax({
        type: "post",
        url: "/navUser",
        dataType: "json",
        success: function(data, textStatus){
            var vm_navUser = new Vue({
                el: "#vmaig-navbar-collapse",
                data: {
                    "username": data["username"],
                    "img": data["img"],
                    "showImg": data["showImg"],
                    "is_active": data["is_active"],
                    "user_notificationNum": data["user_notificationNum"]
                }
            });
        }
    });
})();