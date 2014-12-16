function createTokenHeader() {
    return {
            "X-Auth-Token": window.XauthToken
        };
}

function postRequest($,divId,url,dataHandler) {
    $.ajaxPrefilter("json script", function (options) {
        options.crossDomain = true;
    });
    var h = createTokenHeader();
    $.ajax({
        timeout: 1000,
        type: 'POST',
        url: 'http://localhost/api/v1/' + url,
        headers: h,
         async:   false
    }).done(function (data, textStatus, jqXHR) {
        $(divId).html(dataHandler(data));
    }).fail(function (jqXHR, textStatus, errorThrown) {
        $(divId).html("Error");
    });
}