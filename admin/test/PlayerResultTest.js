var utils = require('utils');
var testlib = require('./testLib');
var teamMatchId = null;
var playerMatchId = null;
var playerMatchCount = 0;
var homeRacks = 0;
var awayRacks = 0;

casper.test.begin('Test Result Page', function suite(test) {
    casper.start();
    casper.thenOpen(testlib.server + '/admin/login', function(){
    });

    testlib.login(test,testlib.user,testlib.pass);

    casper.then(function () {
         test.assertExists("#home-app")
    });

    casper.thenOpen(testlib.server + '/admin/scores', function(){
    });
    casper.then(function () {
         test.assertExists("#score-app")
    });

    casper.then(function () {
         test.assertExists("#TopGun")
    });

    casper.then(function () {
        test.assertExists("#Thurs8Ball")
    });
    casper.then(function () {
        test.assertExists("#Weds8Ball")
    });

    casper.then(function () {
        test.assertExists("#Tues9Ball")
    });

    casper.then(function () {
        test.assertExists("#Scramble")
    });

    casper.then(function () {
        this.echo("Thurs 8 Ball - player results");
        this.clickLabel("Thurs 8 Ball");
    });

    //testlib.playerResultTest(test, 'Thurs 8 Ball');

    casper.then(function () {
        this.clickLabel("Weds 8 Ball");
    });

    //testlib.playerResultTest(test);

    casper.then(function () {
        this.clickLabel("Scramble");
    });

    //testlib.playerResultTest(test);

    casper.then(function () {
        this.clickLabel("Tues 9 Ball");
    });

    casper.then(function () {
        var rows  = this.evaluate(function() {
            return __utils__.findAll("#team-match-results > tbody > tr")
        });
        teamMatchId = rows[0].id;
    });

    casper.then(function () {
        this.click('#player-results-' + teamMatchId);
    });

    casper.then(function () {
        test.assertExists("#player-results");
    });
    casper.then(function () {
        playerMatchCount = this.evaluate(function() {
            return __utils__.findAll("#table-player-results-admin > tbody > tr").length
        });
    });
    casper.then(function () {
        this.clickLabel('Add');
    });

    casper.then(function () {
        test.assertExists("#player-results");
        test.assertExists("#table-player-results-admin");
    });

    casper.then(function () {
        var m  = this.evaluate(function() {
            return __utils__.findAll("#table-player-results-admin > tbody > tr").length
        });
        test.assert(m == playerMatchCount+1, "Tues 9 PlayerMatchCount++");
        playerMatchCount = m;
    });
    casper.then(function () {
        playerMatchId  = this.evaluate(function() {
            var id = null;
            $('#table-player-results-admin > tbody > tr').each(function() {
                if (this.id == undefined || this.id.indexOf("forfeit") >= 0)
                    return;
                id = this.id;
            });
            return id;
        });
        test.assert(playerMatchId != null && playerMatchId != undefined, "PlayerMatchId Found");
    });
    casper.then(function () {
        this.click('#delete-player-result-' + playerMatchId);
    });

    casper.then(function () {
        var m = this.evaluate(function() {
            return __utils__.findAll("#table-player-results-admin > tbody > tr").length
        });
        this.echo(playerMatchId);
        test.assert(m == playerMatchCount-1, "Tues 9 PlayerMatchCount-- " + m + " " + playerMatchCount);
        playerMatchCount  = m;
    });

    casper.then(function () {
         playerMatchId  = this.evaluate(function() {
            var id = null;
            $('#table-player-results-admin > tbody > tr').each(function() {
                if (this.id == undefined || this.id.indexOf("forfeit") >= 0)
                    return;
                id = this.id;
            });
            return id;
        });
        test.assert(playerMatchId != null && playerMatchCount != undefined)
    });

    casper.then(function () {

        homeRacks = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'homeRacks-'+ playerMatchId);

        awayRacks = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'awayRacks-'+ playerMatchId);


          this.fill('form#team-match-form', {
            'playerResults[0].homeRacks': homeRacks +1,
            'playerREsults[0].awayRacks': awayRacks +1,
        }, false);
    });

    casper.then(function(){
        this.click('#player-results-submit');
    });

    casper.then(function(){
        var hr = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'homeRacks-'+ playerMatchId);

        var ar = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'awayRacks-'+ playerMatchId);

        test.assertExists('#player-results-submit');
        test.assert(hr == homeRacks+1);
        test.assert(ar == awayRacks+1);
    });

    casper.run(function(){
        test.done();
    });
});
