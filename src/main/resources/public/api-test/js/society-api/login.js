function login($) {
    $.ajaxPrefilter("json script", function (options) {
        options.crossDomain = true;
    });

    $.ajax({
        timeout: 1000,
        type: 'POST',
        url: 'http://localhost/api/v1/auth/login',
        headers: {
            "X-Auth-Username": "email_608@domain.com",
            "X-Auth-Password": "password_608"
        },
        async:   false
    }).done(function (data, textStatus, jqXHR) {
        var response = "Login Token: " +  data['X-Auth-Token'];
        $('#loginResponse').html(response);
        window.XauthToken = data['X-Auth-Token'];
        console.info("Window Token is " + window.XauthToken);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        $('#loginResponse').html("Error");
    });
}