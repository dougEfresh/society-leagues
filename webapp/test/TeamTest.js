var utils = require('utils');
var testlib = require('./testLib');
var teamName = Math.random();
var season = null;
var members = 0;
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
            return  document.querySelector('#team-seasons').value;
        });
    });

    casper.then(function () {
        this.fill('#team-form', {
            'name': teamName,
            'season.id' : season
        }, false);
    });

    casper.then(function(){

        var user = this.evaluate(function() {
            var i = 0;
            while(i<500) {
                var opt = $('#team-members > option')[i];
                if (!opt.selected) {
                    opt.selected = true;
                    $('#team-members').find('option[value = "' + opt.value + '"]').attr('selected',true);
                    return opt;
                }
                i++;
            }
        });
        members = this.evaluate(function() {
            return  $('#team-members > option[selected]').length
        });
        test.assert(members == 1, 'Members == 1');
    });

    casper.then(function () {
        this.click('#submit');
    });

    casper.then(function () {
        test.assertExists("#team-form")
    });


    casper.then(function () {
        var f = this.evaluate(function(){
            return document.getElementById("name").value
        });

        test.assert(f == teamName, 'TeamName');

        f = this.evaluate(function(){
            return document.getElementById("team-seasons").value
        });
        test.assert(f == season, 'Season');

         f = this.evaluate(function() {
            return  $('#team-members > option[selected]').length
        });
        test.assert(f == members, 'Members');
    });

    casper.run(function(){
        test.done();
    });
});
