function createTokenHeader() {
    return {
            "X-Auth-Token": readCookie("X-Auth-Token")
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
        url: 'http://localhost:8080/api/v1/' + url,
        headers: h
    }).done(function (data, textStatus, jqXHR) {
        $(divId).html(dataHandler(data));
    }).fail(function (jqXHR, textStatus, errorThrown) {
        $(divId).html("Error");
    });
}