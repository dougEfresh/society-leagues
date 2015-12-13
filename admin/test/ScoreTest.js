var utils = require('utils');
var testlib = require('./testLib');
var teamMatchCount = 0;
var teamMatchId = null;

casper.test.begin('Test User Page', function suite(test) {
    casper.start();
    casper.thenOpen(testlib.server + '/login', function(){
    });

    testlib.login(test,testlib.user,testlib.pass);

    casper.then(function () {
         test.assertExists("#home-app")
    });

    casper.thenOpen(testlib.server + '/scores', function(){
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

    casper.run(function(){
        test.done();
    });
});
