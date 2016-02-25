var utils = require('utils');
var testlib = require('./testLib');
var teamMatchCount = 0;
var teamMatchId = null;
var teamMatchDate = null;
var homeRacks = 0;
var awayRacks = 0;
var homeForfeits = 0;
var awayForfeits = 0;

function getStats() {
    var rows = document.querySelectorAll('#table-team-standings > tbody > tr');
    var stats = [];

    for (var i = 0, row; row = rows[i]; i++) {
        var a = row.cells[2].querySelector('a[href*="app"]');
        if (a == undefined || a == null)
           a = row.cells[1].querySelector('a[href*="app"]');
        var id = a.id.replace('team-standing-link-','');
        //var l = row.cells[2].querySelector('span');
        var stat = {};
        stat['rank'] = row.cells[0].textContent;
        stat['teamId'] = a.id.replace('team-standing-link-','');
        stat['wins'] = row.cells[3].textContent;
        stat['lost'] = row.cells[4].textContent;
        stat['rw'] = row.cells[5].textContent;
        stat['rl'] = row.cells[6].textContent;
        stat['forfeits'] = row.cells[8].textContent;
        stats.push(stat);
    }
    return stats;
}

casper.test.begin('Test Scores Page', function suite(test) {
    casper.start();
    casper.thenOpen(testlib.server + '/app/login', function(){
    });

    testlib.login(test,testlib.user,testlib.pass);

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
        this.click("#TopGun-scores")
    });
    casper.then(function () {
        test.assertExists("#score-app")
    });

    testlib.scoreSeasonTest(test);

    casper.then(function () {
        this.click("#Thurs8Ball-scores")
    });

    testlib.scoreSeasonTest(test);

    casper.then(function () {
        this.click("#Weds8Ball-scores")
    });

    testlib.scoreSeasonTest(test);

    casper.then(function () {
        this.click("#Tues9Ball-scores")
    });

    testlib.scoreSeasonTest(test);

    casper.then(function () {
        this.click("#Weds8Ball-scores")
    });

    testlib.scoreSeasonTest(test);

    casper.then(function () {
        this.echo('Submit test Weds');
        this.click("#Weds8Ball-scores")
    });

    testlib.scoreSubmitTest(test);

    casper.then(function () {
        this.echo('Submit test Thurs');
        this.click("#Thurs8Ball-scores")
    });

    testlib.scoreSubmitTest(test);

    casper.then(function () {
        this.echo('Submit test Tues');
        this.click("#Tues9Ball-scores")
    });

    testlib.scoreSubmitTest(test);

    casper.then(function () {
        this.echo('Submit test Top Gun');
        this.click("#TopGun-scores")
    });

    testlib.scoreSubmitTest(test);

    casper.then(function () {
        this.echo('Submit test scramble');
        this.click("#Scramble-scores")
    });

    testlib.scoreSubmitTest(test);

    casper.run(function(){
        test.done();
    });
});
