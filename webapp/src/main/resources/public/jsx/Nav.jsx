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

var Util = require('./util.jsx');

var SocietyNav = React.createClass({
    getInitialState: function() {
        return {
            key: 'home',
            sent: 0,
            pending: 0,
            userContext: {loggedIn: false}
        }
    },
    componentDidMount: function() {
        if (!this.props.userContext.loggedIn)  {
            return;
        }
        this.updateState(this.props);
    },
    componentWillReceiveProps: function (nextProps) {
        this.updateState(nextProps);
    },
    updateState: function(nextProps) {
        Util.getData('/challenge/counters', function(d) {
            this.setState(
                {sent: d[0], pending:d[1]}
            );
        }.bind(this));

        this.setState({userContext: nextProps.userContext});
    },
    getViewingUser: function() {
        if (!this.state.userContext.loggedIn)
            return {id: 0};

        return this.state.userContext.viewUser != null ? this.state.userContext.viewUser : this.state.userContext.user;
    },
    render: function() {
        var name = this.state.userContext.loggedIn ? this.state.userContext.user.name : 'Anon';
        var disp = this.state.userContext.loggedIn ? 'inline' : 'none';

        var indicator = 'Challenges';
        if (this.state.sent + this.state.pending > 0) {
            indicator = (<span>Challenges <Badge>{this.state.sent + this.state.pending}</Badge></span>);
        }
        var navBarInstance = (
            <Navbar inverse brand="Blah" toggleNavKey={this.state.key}>
                <Nav bsStyle="pills" fluid fixedTop activeKey={this.state.key} toggleNavKey={this.state.key}>
                    <NavItemLink to='home' eventKey={"home"}>Home</NavItemLink>
                    <NavItemLink to='stats' params={{userId: this.getViewingUser().id}} eventKey={"Stats"}>Stats</NavItemLink>
                    <DropdownButton  eventKey={"challenge"} title={indicator} navItem={true}>
                        <MenuItemLink  to='request' params={{userId: this.getViewingUser().id}} eventKey={"sent"} >Sent <Badge>{this.state.sent}</Badge></MenuItemLink>
                        <MenuItemLink  to='request' params={{userId: this.getViewingUser().id}} eventKey={"pending"}>Pending <Badge>{this.state.pending}</Badge></MenuItemLink>
                        <MenuItemLink  to='request' params={{userId: this.getViewingUser().id}} eventKey={"request"}>Make Request</MenuItemLink>
                        <MenuItemLink  to='request' params={{userId: this.getViewingUser().id}} eventKey={"history"}>History</MenuItemLink>
                    </DropdownButton>
                    <DropdownButton pullRight eventKey={"user"} title={name} navItem={true}>
                        <MenuItemLink  to='account' params={{userId: this.getViewingUser().id}} eventKey={"account"}>Account</MenuItemLink>
                        <MenuItem href="/logout" eventKey={"logout"}>Logout</MenuItem>
                    </DropdownButton>
                     <DropdownButton pullRight eventKey={"admin"} title={'Admin'} navItem={true}>
                        <MenuItemLink  to='account' params={{userId: this.getViewingUser().id}} eventKey={"account"}>Account</MenuItemLink>
                    </DropdownButton>
                </Nav>
            </Navbar>
        );
        return (navBarInstance);
    }
});

var UserNav = React.createClass({

    render: function() {

    }
    });

module.exports = SocietyNav;
