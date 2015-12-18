var utils = require('utils');
var testlib = require('./testLib');
var teamName = Math.random();
var season = null;
casper.test.begin('Test User Page', function suite(test) {
    casper.start();
    casper.thenOpen(testlib.server + '/admin/login', function(){
    });

    testlib.login(test,testlib.user,testlib.pass);
    casper.then(function () {
         test.assertExists("#home-app")
    });
    casper.thenOpen(testlib.server + '/admin/team', function(){
    });
    casper.then(function () {
         test.assertExists("#team-app")
    });
    casper.then(function () {
         test.assertExists("#teams")
    });
    casper.then(function () {
         this.clickLabel("Add New")
    });
    casper.then(function () {
        test.assertExists("#team-edit")
    });

    casper.then(function () {
        test.assertExists("#team-form")
    });
    casper.then(function () {
        season = this.evaluate(function() {
            document.querySelector('#team-seasons').selectedIndex = 3;
            return   document.querySelector('#team-seasons').value;
        });
    });

    casper.then(function () {
        this.fill('#team-form', {
            'name': teamName,
            'season.id' : season
        }, false);
    });

    casper.then(function(){
        this.evaluate(function() {
            $('#team-members').select2('open');
		$('#team-members input.select2-input').val("Chimento");
        });
    }).waitForSelector('.select2-drop li', function() {
        this.evaluate(function() {
            // ** important bit ** select2 ignores click events on the dropdown, so you need to use mouseup
		$('.select2-drop li').mouseup();
        })
    }).waitWhileSelector('.select2-drop li', function() {
        this.evaluate(function() {
		$('.write button.reply-send').click();
        })
    });

    casper.then(function () {
        this.click('#submit');
    });

    casper.then(function () {
        test.assertExists("#team-form")
    });

    /*
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

*/
    casper.run(function(){
        test.done();
    });
});
