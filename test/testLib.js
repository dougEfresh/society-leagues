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

        if (season == 'TopGun') {
            stats  = this.evaluate(getTopGunStats);
            }
       else {
           stats  = this.evaluate(getStats(season));
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
         if (season == 'TopGun')
            newStats  = this.evaluate(getTopGunStats);
        else
            newStats  = this.evaluate(getStats(season));

         var homeStat = null;
         var newHomeStat = null;

         stats.forEach(function(s) {
                  if (s.teamId  == homeTeam) {
                  homeStat = s;
                  }
                  });
         newStats.forEach(function(s) {
                  if (s.teamId  == homeTeam) {
                 newHomeStat =s;
                  }
                  });

         test.assert(homeStat  != null, 'HomeStat  != null');
         test.assert(newHomeStat  != null, 'awayStat  != null');
         this.echo(JSON.stringify(homeStat));
         this.echo(JSON.stringify(newHomeStat));
         //this.echo(homeStat.forfeits);
         //this.echo(newHomeStat.forfeits);
         //test.assert(homeStat.forfeits != newHomeStat.forfeits, 'Forfeits !=');

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

module.exports = {
    user: user,
    pass: pass,
    server:server,
    width:width,
    height: height,
    timeout: timeout,
    login: login,
    scoreSeasonTest: scoreSeasonTest,
    scoreSubmitTest: scoreSubmitTest,
    playerResultTest: playerResultTest,
    authUser: authUser
};