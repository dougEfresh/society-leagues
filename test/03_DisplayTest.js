var utils = require('utils');
var testlib = require('./testLib');
var u = require('./user.json');
var seasons = require('./seasons.json');
var stats = [];
function getStats() {
    var rows = document.querySelectorAll('#table-team-standings > tbody > tr');
    var stats = [];

    for (var i = 0, row; row = rows[i]; i++) {
        var a = row.cells[2].querySelector('a[href*="app"]');
        if (a == undefined || a == null)
           a = row.cells[1].querySelector('a[href*="app"]');
        var stat = {};
        stat['rank'] = row.cells[0].textContent;
        stat['teamId'] = a.id.replace('team-standing-link-','');
        stat['wins'] = row.cells[3].textContent;
        stat['lost'] = row.cells[4].textContent;
        stat['rw'] = row.cells[5].textContent;
        stat['rl'] = row.cells[6].textContent;
        stats.push(stat);
    }
    return stats;
}

function processStats(test,season) {
    for(var i = 0; i< stats.length; i++) {
        verifyStat(stats[i],(i+1),test,season);
    }
}

function verifyStat(stat,rank,test,season) {
    casper.then(function() {
        var r = stat.rank;
        test.assert(r == rank + "", "Should be rank " + rank);
    });

    casper.then(function() {
        var r = stat.teamId;
        test.assert(r != undefined, "Should have link");
        this.click('#team-standing-link-' + r);
    });

    casper.then(function() {
        test.assertExists('#team-standing-link-' + stat.teamId);
    });

    casper.then(function() {
        if (!season.challenge) {
            //test.assertExists('#table-player-results-' + stat.teamId);
        //} else {
            test.assertExists('#table-team-members-' + stat.teamId);
        }
    });
}


function processSeason(season,test) {
    casper.thenOpen(testlib.server + '/app/display/' + season.id, function(){
    });
    casper.then(function(){
        this.echo('Processing season ' + season.formattedName);
        test.assertExists('#display-app');
    });
    casper.then(function() {
        test.assertExists('#season-'+ season.id);
    });

    casper.then(function () {
        stats = this.evaluate(getStats);
        test.assert(stats != null && stats.length > 0, 'Found Standings');
    });

    casper.then(function() {
        processStats(test,season);
    });
}

casper.test.begin('Test Display', function suite(test) {
    casper.start();
    casper.thenOpen(testlib.server + '/app/login', function(){
    });
    testlib.login(test,testlib.user,testlib.pass);

    seasons.forEach(function(s) {
        processSeason(s,test);
    });


    casper.run(function(){
        test.done();
    });
});
