var user = casper.cli.has("user") ? casper.cli.get("user") : "dchimento@gmail.com";
var pass = casper.cli.has("password") ? casper.cli.get("password") : "abc123";
///var server = casper.cli.has("server") ? casper.cli.get("server") : "https://leagues.societybilliards.com";
var server = casper.cli.has("server") ? casper.cli.get("server") : "http://localhost:8082";
var page = casper.cli.has("page") ? casper.cli.get("page") : "/";
var wait = casper.cli.has("wait") ? casper.cli.get("wait") : "appReady";
var width =  casper.cli.has("width") ? casper.cli.get("width") : 1028;
var height =  casper.cli.has("height") ? casper.cli.get("height") : 768;
var timeout = casper.cli.has("timeout") ? casper.cli.get("timeout") : 10000;
casper.options.viewportSize = {width:width, height: height};
var authUser = null;
var teamMatchCount = 0;
var playerMatchCount = 0;
var playerMatchId = null;
var teamMatchId = null;
var teamMatchDate = null;
var homeRacks = 0;
var awayRacks = 0;
var homeForfeits = 0;
var awayForfeits = 0;
var homeTeam = null;
var awayTeam = null;
var homeState = null;
var awayState = null;
var homeWin = false;
var stats = [];

function getTopGunStats() {
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
        stat['points'] = row.cells[3].textContent;
        stat['wins'] = row.cells[4].textContent;
        stat['lost'] = row.cells[5].textContent;
        stat['rw'] = row.cells[6].textContent;
        stat['rl'] = row.cells[7].textContent;
        stat['forfeits'] = row.cells[8].textContent;
        stats.push(stat);
    }
    return stats;
}
function getStats(season) {
return function(season) {
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
        stat['teamId'] = id;
        stat['wins'] = row.cells[3].textContent;
        stat['lost'] = row.cells[4].textContent;
        stat['rw'] = row.cells[5].textContent;
        stat['rl'] = row.cells[6].textContent;
        stat['forfeits'] = row.cells[8].textContent;

        if (season == 'Tues9Ball') {
        stat['wins'] = row.cells[3].textContent;
        stat['lost'] = row.cells[4].textContent;
        stat['rw'] = row.cells[7].textContent;
        stat['rl'] = row.cells[8].textContent;
        stat['forfeits'] = row.cells[9].textContent;
        }

        stats.push(stat);
    }
    return stats;
    }
}

var login = function (test,user) {
    casper.then(function(){
        test.assertExists("#login-app")
    });

    casper.then(function(){
        test.assertExists("#login-legacy");
        this.click("#login-legacy");
    });

    casper.then(function(){
        test.assertExists("#login")
    });
    casper.then(function () {
        this.fill('form', {
            'username': user,
            'password': "abd123"
        }, false);
    });
    casper.then(function () {
        this.click('#submit');
    });

};

var playerResultTest = function(test,season) {
   casper.then(function () {

        var rows  = this.evaluate(function() {
            return __utils__.findAll("#team-match-results > tbody > tr")
        });
        teamMatchId = rows[0].id;
    });

    casper.then(function () {
        this.click('#player-results-' + teamMatchId, season + '#player-results-' + teamMatchId);
    });

    casper.then(function () {
        //this.debugHTML();
        test.assertExists("#table-player-results-admin", season + ' #table-player-results-admin');
    });
    casper.then(function () {
        playerMatchCount = this.evaluate(function() {
            return __utils__.findAll("#table-player-results-admin > tbody > tr").length
        });
    });
    casper.then(function () {
        this.clickLabel('Add');
    });

    casper.then(function () {
        test.assertExists("#table-player-results-admin");
    });
    casper.then(function () {
        var m  = this.evaluate(function() {
            return __utils__.findAll("#table-player-results-admin > tbody > tr").length
        });
        test.assert(m == playerMatchCount+1, season + "  PlayerMatchCount++");
        playerMatchCount = m;
    });

    casper.then(function () {
        playerMatchId   = this.evaluate(function() {
            var id = null;
            $('#table-player-results-admin > tbody > tr').each(function() {
                if (this.id == undefined || this.id.indexOf("forfeit") >= 0)
                    return;
                id = this.id;
            });
            return id;
        });
        test.assert(playerMatchId != null && playerMatchId != undefined , "PlayerMatchId Found");
    });

    casper.then(function () {
        test.assertExists('#delete-player-result-' + playerMatchId);
        this.click('#delete-player-result-' + playerMatchId);
    });
     casper.then(function () {
         var m = this.evaluate(function() {
             return __utils__.findAll("#table-player-results-admin > tbody > tr").length
         });
         test.assert(m == playerMatchCount-1, season + " PlayerMatchCount--");
         playerMatchCount  = m;
    });

    casper.then(function () {
        playerMatchId   = this.evaluate(function() {
            var id = null;
            $('#table-player-results-admin > tbody > tr').each(function() {
                if (this.id == undefined || this.id.indexOf("forfeit") >= 0)
                    return;
                id = this.id;
            });
            return id;
        });
        test.assert(playerMatchId != null && playerMatchId != undefined, "PlayerMatchId found");
    });

    casper.then(function () {
        var checked = this.evaluate(function (id) {
            return document.getElementById(id).checked;
        },'home-' + playerMatchId);
        test.assertExists('#home-' + playerMatchId);
        test.assertExists('#away-' + playerMatchId);
        this.click('#home-' + playerMatchId);
        //this.echo(checked);
        var newCheckState = checked;
        this.wait(800, function() {
            newCheckState = this.evaluate(function (id) {
                return document.getElementById(id).checked;
            },'home-' + playerMatchId);
            //this.echo(newCheckState);
            test.assert(checked == !newCheckState, 'checked == !newCheckStat');
            var aState= null;
            if (newCheckState) {
                aState = this.evaluate(function(id) {
                    return document.getElementById(id).checked;
                },'away-' +playerMatchId);
                test.assert(newCheckState  != awayState, 'newCheckState  != awayState');
            } else {
                 aState= this.evaluate(function(id) {
                    return document.getElementById(id).checked;
                },'away-' +playerMatchId);
                test.assert(newCheckState  != awayState, 'newCheckState  != awayState');
            }
            awayState = aState;
            homeState = newCheckState;
        }.bind(this));
    });

  casper.then(function () {
      test.assert(awayState != null, 'awayState != null' );
      test.assert(homeState != null, 'homeState != null');
      test.assert(homeState != awayState, 'homeState != awayStat')
  });

    casper.then(function(){
        this.click('#player-results-submit');
    });

    casper.then(function(){
        test.assertExists('#player-results-submit');
    });

    casper.then(function() {
        var hc = this.evaluate(function (id) {
            return document.getElementById(id).checked;
        },'home-' + playerMatchId);

        var ac = this.evaluate(function (id) {
            return document.getElementById(id).checked;
        },'away-' + playerMatchId);

        test.assert(hc == homeState, 'HomeState is submitted');
        test.assert(ac== awayState, 'AwayState is submitted');
    })
};

module.exports = {
    user: user,
    pass: pass,
    server:server,
    width:width,
    height: height,
    timeout: timeout,
    login: login,
    playerResultTest: playerResultTest,
    authUser: authUser
};