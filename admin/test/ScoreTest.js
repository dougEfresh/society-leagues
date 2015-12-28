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
        this.click("#TopGun")
    });
    casper.then(function () {
        test.assertExists("#score-app")
    });

    testlib.scoreSeasonTest(test);

    casper.then(function () {
        this.click("#Thurs8Ball")
    });

    testlib.scoreSeasonTest(test);

    casper.then(function () {
        this.click("#Weds8Ball")
    });

    testlib.scoreSeasonTest(test);

    casper.then(function () {
        this.click("#Tues9Ball")
    });

    testlib.scoreSeasonTest(test);

    casper.then(function () {
        this.click("#Weds8Ball")
    });

    testlib.scoreSeasonTest(test);

    casper.then(function () {
        this.echo('Submit test Weds');
        this.click("#Weds8Ball")
    });

    testlib.scoreSubmitTest(test);

    casper.then(function () {
        this.echo('Submit test Thurs');
        this.click("#Thurs8Ball")
    });

    testlib.scoreSubmitTest(test);

    casper.then(function () {
        this.echo('Submit test Tues');
        this.click("#Tues9Ball")
    });

    testlib.scoreSubmitTest(test);

    casper.then(function () {
        this.echo('Submit test Top Gun');
        this.click("#TopGun")
    });

    testlib.scoreSubmitTest(test);

    casper.then(function () {
        this.echo('Submit test scramble');
        this.click("#Scramble")
    });

    testlib.scoreSubmitTest(test);

    casper.run(function(){
        test.done();
    });
});
