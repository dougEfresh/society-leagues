var $ = require('jquery');

var sendData = function(data,url,callback) {
    console.log("Sending data: " + JSON.stringify(data));
    $.ajax({
        async: true,
        processData: false,
        url: url,
        contentType: 'application/json',
        dataType: 'json',
        data: JSON.stringify(data),
        method: 'post',
        success: function (d) {
            console.log("Got " + JSON.stringify(d) + " back from server");
            callback(d);
        }.bind(this),
        error: function (xhr, status, err) {
            console.error(url, status, err.toString());
            return false;
        }.bind(this)
    });
};

var getData = function(url,callback) {
    console.log("Getting data from " + url);
     $.ajax({
            url:url,
            dataType: 'json',
            success: function (d) {
                callback(d);
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(url, status, err.toString());
            }.bind(this)
     });
};

module.exports = {sendData: sendData, getData: getData};