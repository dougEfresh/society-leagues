var utils = require('utils');
var testlib = require('./testLib');
var teamMatchCount = 0;
var teamMatchId = null;
var teamMatchDate = null;
var homeRacks = 0;
var awayRacks = 0;
var homeForfeits = 0;
var awayForfeits = 0;

casper.test.begin('Test Scores Page', function suite(test) {
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
        this.clickLabel("Top Gun")
    });

    testlib.scoreSeasonTest(test);

    casper.then(function () {
        this.clickLabel("Thurs 8 Ball")
    });

    testlib.scoreSeasonTest(test);

    casper.then(function () {
        this.clickLabel("Weds 8 Ball")
    });

    testlib.scoreSeasonTest(test);

    casper.then(function () {
        this.clickLabel("Tues 9 Ball")
    });

    testlib.scoreSeasonTest(test);

    casper.then(function () {
        this.clickLabel("Weds 8 Ball")
    });

    testlib.scoreSeasonTest(test);

    casper.then(function () {
        this.echo('Submit test Weds');
        this.clickLabel("Weds 8 Ball")
    });

    testlib.scoreSubmitTest(test);

    casper.then(function () {
        this.echo('Submit test Thurs');
        this.clickLabel("Thurs 8 Ball")
    });

    testlib.scoreSubmitTest(test);

    casper.then(function () {
        this.echo('Submit test Tues');
        this.clickLabel("Tues 9 Ball")
    });

    testlib.scoreSubmitTest(test);

    casper.then(function () {
        this.echo('Submit test Top Gun');
        this.clickLabel("Top Gun")
    });

    testlib.scoreSubmitTest(test);

    casper.then(function () {
        this.echo('Submit test scramble');
        this.clickLabel("Scramble")
    });

    testlib.scoreSubmitTest(test);

    casper.run(function(){
        test.done();
    });
});
