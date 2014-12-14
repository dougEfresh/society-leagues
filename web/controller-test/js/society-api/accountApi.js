function accountInfo($) {
    var dataHandler = function (data) {
        var response = "AccountInfo";
        for (var name in data ) {
            response += "<div class=accountInfoField>" + name + "</div>";
            response += "<div class=accountInfoData>" + data[name]+ "</div>"
        }
        return response;
    };
    postRequest($,'#accountInfo','account/info',dataHandler);
}