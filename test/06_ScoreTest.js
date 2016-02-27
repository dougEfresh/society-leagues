var utils = require('utils');
var testlib = require('./testLib');
var teamMatchCount = 0;
var teamMatchId = null;
var teamMatchDate = null;
var homeRacks = 0;
var awayRacks = 0;
var homeForfeits = 0;
var awayForfeits = 0;
var seasons = require('./seasons.json');

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
var scoreSeasonTest = function(test) {
    casper.then(function () {
        test.assertExists("#matches");
    });


    casper.then(function () {
        test.assertNotExists("#table-player-results-admin");
    });

    casper.then(function () {
        test.assertExists("#team-match-results");
    });

    casper.then(function () {
        test.assertEval(function() {
            return __utils__.findAll("#team-match-results > tbody > tr").length > 0;
        }, "found team match results");
    });

    casper.then(function () {
        teamMatchCount = this.evaluate(function() {
            return __utils__.findAll("#team-match-results > tbody > tr").length
        });
    });

    casper.then(function () {
        this.clickLabel('Add New');
    });

    casper.then(function () {
        var newCnt  = this.evaluate(function() {
            return __utils__.findAll("#team-match-results > tbody > tr").length
        });
        test.assert(newCnt - teamMatchCount == 1, 'Added a team match');
        teamMatchCount = newCnt;
    });

    casper.then(function () {
        var rows  = this.evaluate(function() {
            return __utils__.findAll("#team-match-results > tbody > tr")
        });
        teamMatchId = rows[0].id;
    });

    casper.then(function () {
        test.assertExists('#delete-' + teamMatchId);
    });

    casper.then(function () {
        this.click('#delete-' + teamMatchId);
    });

    casper.then(function () {
        test.assertNotExists('#delete-' + teamMatchId);
    });
};

casper.test.begin('Test Scores Page', function suite(test) {
    casper.start();
    casper.thenOpen(testlib.server + '/app/login', function(){
    });

    testlib.login(test,testlib.user,testlib.pass);

    casper.then(function () {
         test.assertExists("#home-app")
    });


    seasons.forEach(function(s) {
        casper.thenOpen(testlib.server + '/app/scores/' + s.id, function(){
        });

          scoreSeasonTest(test);
    });


    casper.run(function(){
        test.done();
    });
});
