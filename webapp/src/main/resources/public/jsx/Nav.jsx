var React = require('react/addons');
var Bootstrap = require('react-bootstrap')
    ,Navbar = Bootstrap.Navbar
    ,Nav = Bootstrap.Nav
    ,DropdownButton = Bootstrap.DropdownButton
    ,DropdownMenu = Bootstrap.DropdownMenu
    ,Badge = Bootstrap.Badge
    ,NavItem = Bootstrap.NavItem
    ,MenuItem = Bootstrap.MenuItem;

var ReactRouterBootstrap = require('react-router-bootstrap')
    ,NavItemLink = ReactRouterBootstrap.NavItemLink
    ,MenuItemLink = ReactRouterBootstrap.MenuItemLink;
var Router = require('react-router')
    , RouteHandler = Router.RouteHandler;

var Util = require('./util.jsx');

var Home = React.createClass({
    contextTypes: {
        router: React.PropTypes.func
    },
    render: function () {
        return (<div>Home</div>);
    }
});

var SocietyNav = React.createClass({
    contextTypes: {
        router: React.PropTypes.func
    },
    getDefaultProps: function() {
        return {
            anon: false
        }
    },
    getInitialState: function() {
        return {
            key: 'home',
            loggedIn: false,
            user : {id: 0, name: 'Login'},
            viewUser: null
        }
    },
    componentDidMount: function() {
        if (this.props.anon) {
            return ;
        }
        var router  = this.context.router;
        Util.getData('/api/user',function(d){
            this.setState({user: d,loggedIn: true});
        }.bind(this),
            router
        );
    },
    componentWillReceiveProps: function (nextProps) {
    },
    getViewingUser: function() {
        return this.state.viewUser != null ? this.state.viewUser : this.state.user;
    },
    render: function() {
        var name = this.getViewingUser().name;
        if (this.props.anon || !this.state.loggedIn) {
            return (
                <Navbar inverse brand="Society" toggleNavKey={this.state.key}>
                    <Nav bsStyle="pills" fluid fixedTop activeKey={this.state.key} toggleNavKey={this.state.key}>
                    </Nav>
                </Navbar>
            );
        }
        return (
            <div>
            <Navbar inverse brand="Society" toggleNavKey={this.state.key}>
            <Nav bsStyle="pills" fluid fixedTop activeKey={this.state.key} toggleNavKey={this.state.key}>
                <NavItemLink to='home' params={{userId: this.getViewingUser().id}} eventKey={"home"}>Home</NavItemLink>
                <NavItemLink to='stats' params={{userId: this.getViewingUser().id}} eventKey={"Stats"}>Stats</NavItemLink>
                <ChallengeNav user={this.getViewingUser()} />
                <DropdownButton pullRight eventKey={"user"} title={name} navItem={true}>
                    <MenuItemLink  to='account' params={{userId: this.getViewingUser().id}} eventKey={"account"}>Account</MenuItemLink>
                    <MenuItem href="/api/logout" eventKey={"logout"}>Logout</MenuItem>
                </DropdownButton>
                <DropdownButton pullRight eventKey={"admin"} title={'Admin'} navItem={true}>
                    <MenuItemLink  to='account' params={{userId: this.getViewingUser().id}} eventKey={"account"}>Account</MenuItemLink>
                </DropdownButton>
            </Nav>
            </Navbar>
                <RouteHandler/>
            </div>
        );
    }
});

var ChallengeNav = React.createClass({
    getDefaultProps: function() {
        return {
            user : {id: 0}
        }
    },
    getInitialState: function() {
        return {
            sent: 0,
            pending: 0
        }
    },
    componentWillReceiveProps: function (nextProps) {
        if (nextProps.user.id != this.props.user.id) {
            this.update(nextProps.user);
        }
    },
    componentDidMount: function() {
        if (this.props.user.id != 0)
            this.update(this.props.user);

     },
    update: function(user) {
        Util.getData('/api/challenge/counters/' + user.id, function(d) {
            this.setState(
                {sent: d[0], pending:d[1]}
            );
        }.bind(this));
    },
    render: function() {
        var indicator = 'Challenges';
        if (this.state.sent + this.state.pending > 0) {
            indicator = (<span>Challenges <Badge>{this.state.sent + this.state.pending}</Badge></span>);
        }
        return (
            <DropdownButton  eventKey={"challenge"} title={indicator} navItem={true}>
                <MenuItemLink  to='sent' params={{userId: this.props.user.id}} eventKey={"sent"} >Sent <Badge>{this.state.sent}</Badge></MenuItemLink>
                <MenuItemLink  to='pending' params={{userId: this.props.user.id}} eventKey={"pending"}>Pending <Badge>{this.state.pending}</Badge></MenuItemLink>
                <MenuItemLink  to='request' params={{userId: this.props.user.id}} eventKey={"request"}>Make Request</MenuItemLink>
                <MenuItemLink  to='history' params={{userId: this.props.user.id}} eventKey={"history"}>History</MenuItemLink>
            </DropdownButton>
        );
    }


});

module.exports = SocietyNav;
