var utils = require('utils');
var testlib = require('./testLib');
var u = require('./user.json');


function countTopPlayers(id) {
        var rows = document.querySelectorAll(id + ' > tbody > tr');
        return rows == undefined || rows == null ? 0 : rows.length;
}

casper.test.begin('Test Home Page', function suite(test) {
    casper.start();
    casper.thenOpen(testlib.server + '/app/login', function(){
    });
    testlib.login(test,u.login);

    casper.then(function() {
        test.assertExists('#name',"Pass Name");
    });

    casper.then(function() {
        test.assertExists('#home-link', "Home Link");
    });

    casper.then(function() {
        test.assertExists('#my-teams', "My Teams");
    });

    casper.then(function() {
        test.assertExists('#my-stats', "My Stats");
    });


    casper.then(function() {
        test.assertExists('#top-players', "Top Players");
    });

    casper.then(function() {
        if (u.challenge) {
            test.assertExists('#challenge', "Challenge");
            test.assertExists('#challenge-standings', "Challenge Standings");
            test.assertExists('#challenge-schedule', "Challenge Schedule");
        }
    });


    /**
     * Verify top players has rows
     */
    casper.then(function() {
        if (u.handicapSeasons) {
            u.handicapSeasons.forEach(function(s) {
                if (!s.season.active)
                    return;

                test.assertExists('#top-players-' + s.season.id, '#top-players-' + s.season.id);
                var cnt = this.evaluate(countTopPlayers,'#top-players-' + s.season.id);
                test.assertTrue(cnt  >0 , "Top  players for " + s.season.id + " " + cnt);
            }.bind(this));
        }
     });

    casper.run(function(){
        test.done();
    });
});
