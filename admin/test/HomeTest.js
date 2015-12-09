var utils = require('utils');
var testlib = require('./testLib');

casper.test.begin('Test Home Page', function suite(test) {
    casper.start();
    casper.thenOpen(testlib.server + '/', function(){
    });

    testlib.login(test,testlib.user,testlib.pass);
    /*
    casper.then(function () {
        testlib.login
        this.fill('form#login', {
            'username': user,
            'password': pass
        }, false);
    });

    casper.then(function () {
        this.click('#submit');
    });
    */

     casper.then(function () {
         test.assertExists("#home-app")
    });


    /*
    casper.then(function(){
        testlib.init();
    });

    casper.then(function() {
        test.assertExists('#home-app');
    });
    casper.then(function() {
        this.click('#request-link');
    });
    casper.then(function(){
        this.waitForSelector('#app-ready',function(){},testlib.notReady('app-ready'),testlib.timeout);
    });

    casper.then(function(){
        this.waitForSelector('#request-app',function(){},testlib.notReady('request-app'),testlib.timeout);
    });
    casper.thenOpen(testlib.server + '/index.html#/app/home', function(){
    });
    casper.then(function(){
        this.waitForSelector('#app-ready',function(){},testlib.notReady('app-ready'),testlib.timeout);
    });
    casper.then(function() {
        test.assertExists('#home-app');
    });

    casper.then(function() {
        test.assertExists('#upcoming-challenges');
    });

    casper.then(function() {
        var matchDao = new MatchDao(testlib.db);
        var matches = matchDao.getUpcomingChallenges(testlib.authUser);
        if (matches.length == 0) {
            test.assertExists('#no-challenges');
        } else {
            matches.forEach(function(m) {
                test.assertExists('#challenge-' + m.getId());
            });
        }
    });

    casper.then(function() {
        var matchDao = new MatchDao(testlib.db);
        var recent = matchDao.getResults(testlib.authUser);
        if (recent.length == 0) {
            test.assertExists('#no-recent-matches');
        } else {
            recent.forEach(function(m) {
                test.assertExists('#recent-' + m.getId());
            });
        }
    });
*/
    casper.run(function(){
        test.done();
    });
});
