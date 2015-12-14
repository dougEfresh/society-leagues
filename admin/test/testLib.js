var user = casper.cli.has("user") ? casper.cli.get("user") : "dchimento@gmail.com";
var pass = casper.cli.has("password") ? casper.cli.get("password") : "abc123";
var server = casper.cli.has("server") ? casper.cli.get("server") : "http://localhost:8082";
var page = casper.cli.has("page") ? casper.cli.get("page") : "/";
var wait = casper.cli.has("wait") ? casper.cli.get("wait") : "appReady";
var width =  casper.cli.has("width") ? casper.cli.get("width") : 1028;
var height =  casper.cli.has("height") ? casper.cli.get("height") : 768;
var timeout = casper.cli.has("timeout") ? casper.cli.get("timeout") : 10000;
casper.options.viewportSize = {width:width, height: height};
var authUser = null;
var teamMatchCount = 0;
var teamMatchId = null;
var teamMatchDate = null;
var homeRacks = 0;
var awayRacks = 0;
var homeForfeits = 0;
var awayForfeits = 0;


var login = function (test,username,password) {
    casper.then(function(){
        test.assertExists("#login-app")
    });

    casper.then(function(){
        test.assertExists("#login")
    });
    casper.then(function () {
        this.fill('form#login', {
            'username': username,
            'password': password
        }, false);
    });
    casper.then(function () {
        this.click('#submit');
    });

};

var scoreSeasonTest = function(test) {
    casper.then(function () {
        test.assertExists("#matches");
    });


    casper.then(function () {
        test.assertNotExists("#player-results");
    });

    casper.then(function () {
        test.assertExists("#team-match-results");
    });

    casper.then(function () {
        test.assertEval(function() {
            return __utils__.findAll("#team-match-results > tbody > tr").length > 0;
        }, "found team match results");
    });

    casper.then(function () {
        teamMatchCount = this.evaluate(function() {
            return __utils__.findAll("#team-match-results > tbody > tr").length
        });
    });

    casper.then(function () {
        this.clickLabel('Add New');
    });

    casper.then(function () {
        var newCnt  = this.evaluate(function() {
            return __utils__.findAll("#team-match-results > tbody > tr").length
        });
        test.assert(newCnt - teamMatchCount == 1, 'Added a team match');
        teamMatchCount = newCnt;
    });

    casper.then(function () {
        var rows  = this.evaluate(function() {
            return __utils__.findAll("#team-match-results > tbody > tr")
        });
        teamMatchId = rows[0].id;
    });

    casper.then(function () {
        test.assertExists('#delete-' + teamMatchId);
    });

    casper.then(function () {
        this.click('#delete-' + teamMatchId);
    });

    casper.then(function () {
        test.assertNotExists('#delete-' + teamMatchId);
    });
};

var scoreSubmitTest = function(test) {
    casper.then(function () {
        var rows  = this.evaluate(function() {
            return __utils__.findAll("#team-match-results > tbody > tr")
        });
        teamMatchId = rows[0].id;
    });

    casper.then(function () {
        var month = Math.floor((Math.random() * 10) + 1);
        if (month < 10)
            month = '0' + month;
        var day = Math.floor((Math.random() * 25) + 1);
        if (day < 10) {
            day = '0' + day;
        }
        var year = Math.floor((Math.random() * 14) + 2000);
        teamMatchDate = year + '-' + month + '-'+  day;
        this.echo(teamMatchDate);
        homeRacks = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'homeRacks-'+teamMatchId);

        homeForfeits = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'homeForfeits-'+teamMatchId);

        awayRacks = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'awayRacks-'+teamMatchId);

        awayForfeits = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'awayForfeits-'+teamMatchId);

        awayRacks++;
        homeRacks++;
        homeForfeits++;
        awayForfeits++;
        this.fill('form#team-match-form', {
            'matches[0].date': teamMatchDate,
            'matches[0].homeRacks': homeRacks,
            'matches[0].awayRacks': awayRacks,
            'matches[0].homeForfeits': homeForfeits,
            'matches[0].awayForfeits': awayForfeits
        }, false);
    });

    casper.then(function () {
        this.click('#submit-team-match-scores');
    });
    casper.then(function () {
        test.assertExists("#form-select-date");
    });
    casper.then(function () {
        this.fill('form#form-select-date', {
            'select-date' : teamMatchDate
        });
    });

    casper.then(function () {
        test.assertExists("#date-" + teamMatchId);
    });

    casper.then(function () {
        var hr = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'homeRacks-'+teamMatchId);
        test.assert(hr == homeRacks, 'homeRacks eq');

        var ar = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'awayRacks-'+teamMatchId);
        test.assert(ar == awayRacks, 'awayRacks eq');

        var af = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'awayForfeits-'+teamMatchId);
        test.assert(af == awayForfeits, 'awayForfeits eq');

        var hf = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'homeForfeits-'+teamMatchId);
        test.assert(hf == homeForfeits, 'homeForfeits eq');

        var dt = this.evaluate(function(id) {
            return document.getElementById(id).value;
        },'date-'+teamMatchId);
        test.assert(dt == teamMatchDate, 'dates eq');
    });

};

module.exports = {
    user: user,
    pass: pass,
    server:server,
    width:width,
    height: height,
    timeout: timeout,
    login: login,
    scoreSeasonTest: scoreSeasonTest,
    scoreSubmitTest: scoreSubmitTest,
    authUser: authUser
};