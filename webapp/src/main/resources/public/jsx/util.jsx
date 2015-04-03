var $ = require('jquery');
var moment = require('moment');

var sendData = function(data,url,callback,router) {
    console.log("Sending data: " + JSON.stringify(data));
    $.ajax({
        async: true,
        processData: false,
        url: url,
        contentType: 'application/json',
        dataType: 'json',
        data: JSON.stringify(data),
        method: 'post',
        statusCode: {
            401: function () {
                console.log('I Need to Authenticate');
                if (router) {
                    router.transitionTo('login', null, {from: router.getCurrentPath()});
                }
            }
        },
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

var getData = function(url,callback,router) {
    console.log("Getting data from " + url);
     $.ajax({
         url:url,
         dataType: 'json',
         statusCode: {
             401: function () {
                console.log('I Need to Authenticate');
                 if (router) {
                     router.transitionTo('login', null, {from: router.getCurrentPath()});
                 }
             }
         },
         success: function (d) {
             callback(d);
         }.bind(this),
         error: function (xhr, status, err) {
             console.error(url, status, err.toString());
         }.bind(this)
     });
};

/**
 * Figure out the next challenge day (which is Sunday)
 */
function getNextChallengeDay() {
    for (var i = 0; i < 8 ; i++ ) {
        var day = moment().add(i,'days');
        if (day.weekday() == 0) {
            return day.format('YYYY-MM-DD')
        }
    }
    console.error('Could not find next challenge date');
}

module.exports = {sendData: sendData, getData: getData, nextChallengeDate: getNextChallengeDay};