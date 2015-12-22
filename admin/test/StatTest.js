var utils = require('utils');
var testlib = require('./testLib');

casper.test.begin('Test Stats Page', function suite(test) {
    casper.start();
    casper.thenOpen(testlib.server + '/admin/login', function(){
    });
    testlib.login(test,testlib.user,testlib.pass);
    casper.thenOpen(testlib.server + '/admin/stats/564200b4986c5a8a80d11c72', function(){
    });
    casper.then(function() {
        test.assertExists('#stats-app',"stats found");
    });
    casper.then(function() {
        test.assertExists('#topgun-stats',"topgun stats");
    });
     casper.then(function() {
        test.assertExists('#users-stats-search',"user seach stats");
    });
    casper.run(function(){
        test.done();
    });
});
