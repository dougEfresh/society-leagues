var React = require('react/addons');
var Router = require('react-router')
    , RouteHandler = Router.RouteHandler
    , Route = Router.Route
    , DefaultRoute = Router.DefaultRoute;

var ChallengeRequest = require('./Request.jsx');
var SocietyNav = require('./Nav.jsx');
var Login = require('./Login.jsx');

var App = React.createClass({
    render: function () {
        return (
            <div>
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
//
//<DefaultRoute handler={Home} />
var routes = (
    <Route handler={App}>
        <DefaultRoute handler={Home} />
        <Route name="login" path="login" handler={Login} />
        <Route name="nav" path="/" handler={SocietyNav}>
            <Route name="home" path="home" handler={Home}/>
            <Route name="account" path=":userId/account" handler={Home}/>
            <Route name="challenge" path="challenge" handler={ChallengeRequest}>
                <Route name="request" path=":userId/request" handler={ChallengeRequest}/>
                <Route name="pending" path=":userId/pending" handler={ChallengeRequest}/>
                <Route name="sent" path=":userId/sent" handler={ChallengeRequest}/>
                <Route name="history" path=":userId/history" handler={ChallengeRequest}/>
            </Route>

            <Route name="stats" path=":userId/stats" handler={Stats}/>
        </Route>
    </Route>
);

Router.run(routes, function (Handler) {
    React.render(<Handler/>, document.getElementById('content'));
});