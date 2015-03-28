var React = require('react/addons');
var Router = require('react-router')
  , RouteHandler = Router.RouteHandler
  , Route = Router.Route;


var ChallengeRequest = require('./request.jsx');
var SocietyNav = require('./nav.jsx');

var App = React.createClass({
  render: function () {
    return (
        <div>
            <SocietyNav />
            <RouteHandler/>
        </div>
    );
  }
});

var Stats = React.createClass({
    render: function() {
        return (<div>Stats</div>);
    }
});

var Home = React.createClass({
    render: function() {
        return (<div>Home</div>);
    }
});

var routes = (
  <Route handler={App} path="/">
      <Route name="home" path="home" handler={Home} />
      <Route name="request" path="challenge/request" handler={ChallengeRequest} />
      <Route name="stats" path="stats" handler={Stats} />
  </Route>
);

Router.run(routes, function (Handler) {
  React.render(<Handler/>, document.getElementById('content'));
});

