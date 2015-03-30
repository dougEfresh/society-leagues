var React = require('react/addons');
var Router = require('react-router')
    , RouteHandler = Router.RouteHandler
    , Route = Router.Route;

var Util = require('./util.jsx');
var ChallengeRequest = require('./Request.jsx');
var SocietyNav = require('./Nav.jsx');
var Login = require('./Login.jsx');

var App = React.createClass({
    //TODO use localStorage to save state
    getInitialState: function () {
        return {
            user: null,
            viewUser: null,
            loggedIn: false
        };
    },
    componentWillMount: function() {
        this.update();
    },
    update: function() {
        Util.getData('/user',function(d){
            this.setState({user: d,loggedIn: true});
        }.bind(this));
    },
    render: function () {
        return (
            <div>
                <SocietyNav userContext={this.state} />
                <Login onLogin={this.update} loggedIn={this.state.loggedIn}/>
                <RouteHandler/>
            </div>
        );
    }
});

var Stats = React.createClass({
    render: function () {
        return (<div>Stats</div>);
    }
});

var Home = React.createClass({
    contextTypes: {
        router: React.PropTypes.func
    },
    render: function () {
        return (<div>Home</div>);
    }
});

var routes = (
    <Route handler={App} path="/">
        <Route name="login" handler={Login}/>
        <Route name="home" path=":userId/home" handler={Home}/>
        <Route name="account" path=":userId/account" handler={Home}/>
        <Route name="request" path=":userId/challenge/request" handler={ChallengeRequest}/>
        <Route name="stats" path=":userId/stats" handler={Stats}/>
    </Route>
);

Router.run(routes, function (Handler) {
    React.render(<Handler/>, document.getElementById('content'));
});