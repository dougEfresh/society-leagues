phantom.page.injectJs('LoginPage.js');
var loginPage = new LoginPage();
var utils = require('utils');

casper.test.begin('Testing Login Page', function suite(test) {
        loginPage.start(server);
        loginPage.login(user,pass);

        casper.then(function() {
            test.assertTitle('Society Home','Verified Home Title');
        });

        casper.run(function() {
            test.done();
        });
    }
);

