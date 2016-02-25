var utils = require('utils');
var testlib = require('./testLib');

casper.test.begin('Test Home Page', function suite(test) {
    casper.start();
    casper.thenOpen(testlib.server + '/app/login', function(){
    });
    testlib.login(test,testlib.user,testlib.pass);

    casper.then(function() {
        test.assertExists('#name',"Pass Name");
    });

    casper.then(function () {
        test.assertExists("#home-app")
    });

    casper.then(function () {
        test.assertExists("#TopGun-scores")
    });

    casper.then(function () {
        test.assertExists("#Thurs8Ball-scores")
    });
    casper.then(function () {
        test.assertExists("#Weds8Ball-scores")
    });

    casper.then(function () {
        test.assertExists("#Tues9Ball-scores")
    });

    casper.then(function () {
        test.assertExists("#Scramble-scores")
    });

    casper.then(function () {
        this.click("#Thurs8Ball-scores")
    });
     casper.then(function () {
        this.clickLabel("Sheets")
    });
    casper.then(function () {
        test.assertExists(".game-type")
    });
    casper.then(function () {
        this.back();
    });

    casper.then(function () {
        this.click("#Scramble-scores")
    });
     casper.then(function () {
        this.clickLabel("Sheets")
    });
    casper.then(function () {
        test.assertExists(".game-type")
    });
    casper.then(function () {
        this.back();
    });

    casper.then(function () {
        this.click("#Tues9Ball-scores")
    });
     casper.then(function () {
        this.clickLabel("Sheets")
    });
    casper.then(function () {
        test.assertExists(".game-type")
    });
    casper.then(function () {
        this.back();
    });
    casper.then(function () {
        this.click("#Weds8Ball-scores")
    });
     casper.then(function () {
        this.clickLabel("Sheets")
    });
    casper.then(function () {
        test.assertExists(".game-type")
    });
    casper.then(function () {
        this.back();
    });

    casper.run(function(){
        test.done();
    });
});
