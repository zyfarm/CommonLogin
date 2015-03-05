(function () {
    var pullPost = function (url, params, callback) {
        if (params != undefined) {
            $.post(url, params, callback);
        } else {
            $.post(url, callback);
        }
    }

    var fetchNewPost = function (data) {
        for (var i = 0; data.length; i++) {
            $('#post_last_update_user').append(data[i].nick);
            $('#post_last_favorite').append(data[i].favorite_num);
            $('#post_last_update_event').append(data[i].favorite_num);
            $('#post_title').append(data[i].title);
            $('#post_content').append(data[i].post_content);
        }
    }

    pullPost("/api/qa/question_list", fetchNewPost);
})();