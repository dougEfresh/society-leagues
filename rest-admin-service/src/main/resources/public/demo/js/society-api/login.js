function login($) {
  login($,$('#login').username.value,$('#login').password.value);
}


function login($,username,password) {
    $.ajaxPrefilter("json script", function (options) {
        options.crossDomain = true;
    });
    var l = new Object();
    l.username = username;
    l.password = password;
    $.ajax({
        timeout: 1000,
        type: 'POST',
        url: '/api/v1/auth/authenticate',
        data: l,
        async:   false
    }).done(function (data, textStatus, jqXHR) {
        var response = "Login Token: " +  data['X-Auth-Token'];
        $('#loginResponse').html(response);
        window.XauthToken = data['X-Auth-Token'];
        console.info("Token is " + data['X-Auth-Token']);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        $('#loginResponse').html("Error");
    });
}
