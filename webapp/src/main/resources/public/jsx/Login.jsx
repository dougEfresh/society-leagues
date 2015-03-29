var React = require('react/addons');
var Bootstrap = require('react-bootstrap')
    ,Button = Bootstrap.Button
    ,Input = Bootstrap.Input
    ,Label = Bootstrap.Label;


var Login = React.createClass({
    getInitialState: function () {
        return {
            error: false,
            loggedIn: false
        };
    },
    handleSubmit: function(e){
        var user = this.refs.username.getValue();
        var password = this.refs.password.getValue();
        console.log('Logging in: ' + user);
        $.ajax({
            async: true,
            processData: true,
            url: '/authenticate',
            data: {username: user, password: password},
            method: 'post',
            success: function (d) {
                window.location = '/app/home.html'
            }.bind(this),
            error: function (xhr, status, err) {
                console.error('/authenticate', status, err.toString());
            }.bind(this)
        });

    },
    render: function () {
        if (this.props.loggedIn) {
            return null;
        }
        var errors = this.state.error ? <p>Bad login information</p> : '';
        return (
            <div>
                <Label><Input type='text' ref='username' placeholder="username" defaultValue="login1"/></Label>
                <Label><Input type='text' ref='password' placeholder="password" defaultValue="login1"/></Label>
                <Button onClick={this.handleSubmit} type="submit">login</Button>
            </div>
        );
    }
});


module.exports = Login;
