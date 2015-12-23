var utils = require('utils');
var testlib = require('./testLib');

casper.test.begin('Test Home Page', function suite(test) {
    casper.start();
    casper.thenOpen(testlib.server + '/admin/home', function(){
    });
    testlib.login(test,testlib.user,testlib.pass);

    casper.then(function() {
        test.assertExists('#name',this.debugHTML());
    });

    casper.run(function(){
        test.done();
    });
});
