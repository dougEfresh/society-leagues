function LoginPage () {
    this.start = function (server) {
        casper.echo("Using " + server + " as test host");
        casper.start(server);
    };

    this.login = function (user, pass) {
        casper.then(function() {
            this.echo("Using " + user + ":" + pass + " to login ");
            this.fill('form#login', {'username': user, 'password': pass},  true);
        });

        casper.then(function() {
            this.capture("/tmp/test.png");
        });

    };
}