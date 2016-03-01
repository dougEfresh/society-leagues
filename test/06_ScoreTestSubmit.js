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
var stats = [];
var newStats = [];
var homeTeam = null;
var awayTeam = null;
var homeState = null;
var awayState = null;
var homeWin = false;


function getStats() {
    var rows = document.querySelectorAll('#table-team-standings > tbody > tr');
    var stats = [];

    for (var i = 0; i <  rows.length; i++) {
        var row = rows[i];
        var a = row.cells[2].querySelector('a[href*="app"]');
        var stat = {};
        stat['rank'] = row.cells[0].textContent;
        stat['teamId'] = a.id.replace('team-standing-link-','');
        stat['wins'] = row.cells[3].textContent;
        stat['lost'] = row.cells[4].textContent;
        stat['rw'] = row.cells[5].textContent;
        stat['rl'] = row.cells[6].textContent;
        stat['pct'] = row.cells[7].textContent;
        stats.push(stat);
    }
    return stats;
}

function getChallengeStats() {
    var rows = document.querySelectorAll('#table-team-standings > tbody > tr');
    var stats = [];

    for (var i = 0; i <  rows.length; i++) {
        var row = rows[i];
        var a = row.cells[1].querySelector('a[href*="app"]');
        var stat = {};
        stat['rank'] = row.cells[0].textContent;
        stat['teamId'] = a.id.replace('team-standing-link-','');
        stat['points'] = row.cells[3].textContent;
        stat['wins'] = row.cells[4].textContent;
        stat['lost'] = row.cells[5].textContent;
        stat['rw'] = row.cells[6].textContent;
        stat['rl'] = row.cells[7].textContent;
        stat['pct'] = row.cells[8].textContent;
        stats.push(stat);
    }
    return stats;
}

var scoreSubmitTest = function(test,season) {
    casper.then(function () {
        var rows  = this.evaluate(function() {
            return __utils__.findAll("#team-match-results > tbody > tr")
        });
        teamMatchId = rows[0].id;
    });

     casper.then(function () {
        var month = Math.floor((Math.random() * 10) + 1);
        if (month < 10)
            month = '0' + month;
        var day = Math.floor((Math.random() * 25) + 1);
        if (day < 10) {
            day = '0' + day;
        }
        var year = Math.floor((Math.random() * 14) + 2000);
        teamMatchDate = year + '-' + month + '-'+  day;
        this.echo(teamMatchDate);
        homeRacks = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'homeRacks-'+teamMatchId);

        homeForfeits = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'homeForfeits-'+teamMatchId);

        awayRacks = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'awayRacks-'+teamMatchId);

        awayForfeits = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'awayForfeits-'+teamMatchId);

        //awayRacks++;
        //homeRacks++;
        if (homeRacks == 0) {
           homeRacks = 5
        }
        if (awayRacks == 0)
           awayRacks = 5;

        homeForfeits++;
        awayForfeits++;

        if (homeRacks >= awayRacks) {
               homeRacks = awayRacks-1;
               homeWin = false;
        } else {
               homeWin = true;
               homeRacks = awayRacks+1;
        }

        homeTeam = this.evaluate(function(id) {
            return $('#'+ id  + ' option:not(:selected)')[0].value;
        },'home-'+ teamMatchId);
        awayTeam = this.evaluate(function(id) {
            return $('#'+ id  + ' option:not(:selected)')[2].value;
        },'away-'+ teamMatchId);

        if (season.challenge) {
            stats  = this.evaluate(getChallengeStats);
        } else {
            stats  = this.evaluate(getStats);
        }

        this.fill('form#team-match-form', {
            'matches[0].date': teamMatchDate,
            'matches[0].homeRacks': homeRacks,
            'matches[0].awayRacks': awayRacks,
            'matches[0].homeForfeits': homeForfeits,
            'matches[0].awayForfeits': awayForfeits,
            'matches[0].home.id': homeTeam,
            'matches[0].away.id': awayTeam
        }, false);
    });

    casper.then(function () {
        this.click('#submit-team-match-scores');
    });
    casper.then(function () {
        test.assertExists("#form-select-date");
    });
    casper.then(function () {
        this.fill('#form-select-date', {
            'select-date' : teamMatchDate
        });
    });

    casper.then(function () {
        test.assertExists("#date-" + teamMatchId);
    });

    casper.then(function () {
        var hr = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'homeRacks-'+teamMatchId);
        test.assert(hr == homeRacks, 'homeRacks eq');

        var ar = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'awayRacks-'+teamMatchId);
        test.assert(ar == awayRacks, 'awayRacks eq');

        var af = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'awayForfeits-'+teamMatchId);
        test.assert(af == awayForfeits, 'awayForfeits eq');

        var hf = this.evaluate(function(id) {
            return parseInt(document.getElementById(id).value);
        },'homeForfeits-'+teamMatchId);
        test.assert(hf == homeForfeits, 'homeForfeits eq');

        var dt = this.evaluate(function(id) {
            return document.getElementById(id).value;
        },'date-'+teamMatchId);

        var ht = this.evaluate(function(id) {
            return document.getElementById(id).value;
        },'home-'+teamMatchId);

        var at = this.evaluate(function(id) {
            return document.getElementById(id).value;
        },'away-'+teamMatchId);
        test.assert(at == awayTeam, 'awayTeam eq');
    });

  casper.then(function () {
         if (season.challenge)
            newStats  = this.evaluate(getChallengeStats);
        else
            newStats  = this.evaluate(getStats);

         var homeStat = null;
         var newHomeStat = null;

         stats.forEach(function(s) {
             if (s.teamId  == homeTeam) {
                 homeStat = s;
             }
         });
         newStats.forEach(function(s) {
                  if (s.teamId  == homeTeam) {
                      newHomeStat = s;
                  }
         });
      
         test.assert(homeStat  != null, 'HomeStat  != null');
         test.assert(newHomeStat  != null, 'awayStat  != null');
         this.echo(JSON.stringify(homeStat));
         this.echo(JSON.stringify(newHomeStat));

         if (homeWin) {
             test.assert(homeStat.rw != newHomeStat.rw, 'rw !=');
             test.assert(homeStat.wins != newHomeStat.wins, 'wins !=');
             }
         else {
             test.assert(homeStat.rl != newHomeStat.rl, 'rl !=');
             test.assert(homeStat.lost != newHomeStat.lost, 'lost !=');
             }
    });

};



function processSeason(season,test) {
    casper.thenOpen(testlib.server + '/app/scores/' + season.id, function(){
    });


   casper.then(function () {
       this.echo('Processing ' + season.formattedName);
         test.assertExists('#team-match-form');
    });
    casper.then(function () {
      //  this.click("#" + season +  "-scores")
    });

    scoreSubmitTest(test,season);
}

casper.test.begin('Test Scores Page', function suite(test) {
    casper.start();
    casper.thenOpen(testlib.server + '/app/login', function(){
    });

    testlib.login(test,testlib.user,testlib.pass);

    casper.then(function () {
         test.assertExists("#home-app")
    });

    seasons.forEach(function(s) {
        processSeason(s,test);
    });

    casper.run(function(){
        test.done();
    });
});
