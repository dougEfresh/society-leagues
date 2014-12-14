function login($) {
    $.ajaxPrefilter("json script", function (options) {
        options.crossDomain = true;
    });

    $.ajax({
        timeout: 1000,
        type: 'POST',
        url: 'http://localhost:8080/api/v1/auth/login',
        headers: {
            "X-Auth-Username": "email_608@domain.com",
            "X-Auth-Password": "password_608"
        }
    }).done(function (data, textStatus, jqXHR) {
        var response = "Login Token: " + data.token;
        createCookie("X-Auth-Token", data.token, 25);
        $('#loginResponse').html(response);

        //var preLoginInfo = JSON.parse($.cookie('dashboard.pre.login.request'));
        //window.location = preLoginInfo.url;
    }).fail(function (jqXHR, textStatus, errorThrown) {
        $('#loginResponse').html("Error");
    });
}