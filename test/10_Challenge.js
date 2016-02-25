var utils = require('utils');
var testlib = require('./testLib');
var u = require('./user.json');
var slotId = null;
var date = null;

casper.test.begin('Test Home Page', function suite(test) {
    casper.start();
    casper.thenOpen(testlib.server + '/app/login', function(){
    });
    testlib.login(test,u.login);
    casper.thenOpen(testlib.server + '/app/challenge', function(){
    });
    casper.then(function() {
        test.assertExists('#challenge-app','challenge-app');
        test.assertExists('#challenge-date','challenge-date');
        test.assertExists('#challenge-users','challenge-users');
        test.assertExists('#challenge-slots','challenge-slots');
        test.assertExists('#challenge-submit','challenge-submit');
    });

    casper.then(function() {
        date = this.evaluate(function() {
            document.querySelector('#challenge-date').selectedIndex = 3;
            return  document.querySelector('#challenge-date').value;
        });
    });

    casper.then(function() {
        slotId = this.evaluate(function() {
            document.querySelector('#challenge-slots').selectedIndex = 3;
            return  document.querySelector('#challenge-slots').value;
        });
        this.echo(slotId);
    });

    casper.run(function(){
        test.done();
    });
});
