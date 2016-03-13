var utils = require('utils');
var testlib = require('./testLib');
var u = require('./user.json');
var seasons = require('./seasons.json');
function processSeason(season,test) {
    casper.thenOpen(testlib.server + '/app/scores/' + season.id, function(){
    });
    casper.then(function () {
        this.click("#sheets")
    });
    casper.then(function () {
        test.assertExists(".game-type")
    });
    casper.then(function () {
        this.back();
    });

}

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

    seasons.forEach(function(s) {
        processSeason(s,test);
    });

    casper.run(function(){
        test.done();
    });
});
