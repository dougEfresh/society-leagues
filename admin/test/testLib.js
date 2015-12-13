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

module.exports = {
    user: user,
    pass: pass,
    server:server,
    width:width,
    height: height,
    timeout: timeout,
    login: login,
    scoreSeasonTest: scoreSeasonTest,
    authUser: authUser
};