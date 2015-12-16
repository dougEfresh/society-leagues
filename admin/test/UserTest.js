var utils = require('utils');
var testlib = require('./testLib');
var fName = Math.random();
var lName = Math.random();
var email = Math.random();

casper.test.begin('Test User Page', function suite(test) {
    casper.start();
    casper.thenOpen(testlib.server + '/admin/login', function(){
    });

    testlib.login(test,testlib.user,testlib.pass);

    casper.then(function () {
         test.assertExists("#home-app")
    });

    casper.thenOpen(testlib.server + '/admin/user', function(){
    });


    casper.then(function () {
         test.assertExists("#user-app")
    });

    casper.then(function () {
         test.assertExists("#users")
    });
    casper.then(function () {
         this.clickLabel("Add New")
    });
    casper.then(function () {
        test.assertExists("#user-edit")
    });

    casper.then(function () {
        test.assertExists("#user-form")
    });


    casper.then(function () {
        test.assertExists("#user-form")
    });


    casper.then(function () {
        this.fill('form#user-form', {
            'firstName': fName,
            'lastName': lName,
            'login': email,
            'role': 'ADMIN',
            'handicapSeasons[0].handicap' : 'D'
        }, false);
    });

    casper.then(function () {
        this.click('#submit');
    });

    casper.then(function () {
        test.assertExists("#user-form")
    });

    casper.then(function () {
        var f = this.evaluate(function(){
            return document.getElementById("firstName").value
        });

        test.assert(f == fName, 'Fname');

        f = this.evaluate(function(){
            return document.getElementById("lastName").value
        });
        test.assert(f == lName, 'Lname');

        f = this.evaluate(function() {
            return document.getElementById("login").value;
        });
        test.assert(f == email, 'login');

        f = this.evaluate(function() {
            return document.getElementById("TopGun-hc").value;
        });
        test.assert(f == 'D', 'Handicap');
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
