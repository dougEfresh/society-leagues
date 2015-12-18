var utils = require('utils');
var testlib = require('./testLib');

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
        this.clickLabel("Thurs 8 Ball");
    });

    testlib.playerResultTest(test);

    casper.then(function () {
        this.clickLabel("Weds 8 Ball");
    });

    testlib.playerResultTest(test);

    casper.then(function () {
        this.clickLabel("Scramble");
    });

    testlib.playerResultTest(test);

    casper.run(function(){
        test.done();
    });
});
