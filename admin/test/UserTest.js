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


    casper.run(function(){
        test.done();
    });
});
