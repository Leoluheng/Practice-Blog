(function(){
    function CommentQuote(user_name, commend_id) {
        comment = document.getElementById('comment');
        comment.value = "@['" + user_name + "', " + commend_id + "]: ";
        comment.focus();
        ;
        comment.setSelectionRange(comment.value.length, comment.value.length);
    };

    $('#vmaig-comment-form').submit(function () {
        $.ajax({
            type: "POST",
            url: "/comment",
            data: {"comment": $("#comment").val()},
            beforeSend: function (xhr) {
                xhr.setRequestHeader("X-CSRFToken", $.cookie('csrftoken'));
            },
            success: function (data, textStatus) {
                $("#comment").val("");
                $(".vmaig-comment ul").prepend(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert(XMLHttpRequest.responseText);

            }

        });
        return false;
    });
})();