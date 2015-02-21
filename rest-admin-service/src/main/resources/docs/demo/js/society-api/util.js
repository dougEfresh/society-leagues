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
        data:
    }).done(function (data, textStatus, jqXHR) {
        $(divId).html(dataHandler(data));
    }).fail(function (jqXHR, textStatus, errorThrown) {
        $(divId).html("Error");
    });
}

function genericDataHandler($,type,endpoint) {
    var dataHandler = function (data) {
        var response = "";
        if ($.isArray(data)) {
            for (var i = 0; i < data.length; i++) {
                response += writeResponseData(data[i], type);
            }
        } else {
            response += writeResponseData(data,type);
        }
        return response;
    };
    postRequest($,'#'+ type + "Info", type  + '/' + endpoint ,dataHandler);
}

function writeResponseData(data,type) {
    var response = "";
    for (var name in data ) {
        response += "<div class=" + type + "InfoField>" + name + "</div>";
        response += "<div class=" + type + "InfoData>" + data[name]+ "</div>";
    }
    return response;
}
