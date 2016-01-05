var utils = require('utils');
var testlib = require('./testLib');
var stats = [];
function getStats() {
    var rows = document.querySelectorAll('#table-leaders > tbody > tr');
    var stats = [];

    for (var i = 0, row; row = rows[i]; i++) {
        var a = row.cells[1].querySelector('a[href*="app"]');
        //var l = row.cells[2].querySelector('span');
        var stat = {};
        stat['rank'] = row.cells[0].textContent;
        stat['userId'] = a.id.replace('leader-user-link-','');
        stats.push(stat);
    }
    return stats;
}

function processStats(test) {
    for(var i = 0; i< stats.length; i++) {
        verifyStat(stats[i],(i+1),test);
    }
}

function verifyStat(stat,rank,test) {
    casper.then(function() {
        var r = stat.rank;
        test.assert(r == rank + "", "Should be rank " + rank);
    });

    casper.then(function() {
        var r = stat.userId;
        test.assert(r != undefined, "Should have link");
        this.click('#leader-user-link-' + r);
    });

    casper.then(function() {
        test.assertExists('#user-results-' + stat.userId);
    });

    casper.then(function() {
        this.back();
    });

    casper.then(function() {
        test.assertExists('#leader-user-link-' + stat.userId);
    });
}


casper.test.begin('Test Home Page', function suite(test) {
    casper.start();
    casper.thenOpen(testlib.server + '/app/home', function(){
    });
    testlib.login(test,testlib.user,testlib.pass);

    casper.then(function() {
        test.assertExists('#Tues9Ball-leaders');
        this.click('#Tues9Ball-leaders');
    });

    casper.then(function() {
        test.assertExists('#table-leaders','table-leaders');
    });

    casper.then(function () {
        stats  = this.evaluate(getStats);
        test.assert(stats != null && stats.length > 0, 'Found leaders');
    });

    casper.then(function () {
        processStats(test);
    });


    casper.then(function() {
        test.assertExists('#Weds8Ball-leaders');
        this.click('#Weds8Ball-leaders');
    });

    casper.then(function() {
        test.assertExists('#table-leaders','table-leaders');
    });

    casper.then(function () {
        stats  = this.evaluate(getStats);
        test.assert(stats != null && stats.length > 0, 'Found leaders');
    });

    casper.then(function () {
        processStats(test);
    });

    casper.then(function() {
        test.assertExists('#Thurs8Ball-leaders');
        this.click('#Thurs8Ball-leaders');
    });

    casper.then(function() {
        test.assertExists('#table-leaders','table-leaders');
    });

    casper.then(function () {
        stats  = this.evaluate(getStats);
        test.assert(stats != null && stats.length > 0, 'Found leaders');
    });

    casper.then(function () {
        processStats(test);
    });

    casper.then(function() {
        test.assertExists('#Scramble-leaders');
        this.click('#Scramble-leaders');
    });

    casper.then(function() {
        test.assertExists('#table-leaders','table-leaders');
    });

    casper.then(function () {
        stats  = this.evaluate(getStats);
        test.assert(stats != null && stats.length > 0, 'Found leaders');
    });

    casper.then(function () {
        processStats(test);
    });


    casper.run(function(){
        test.done();
    });
});
