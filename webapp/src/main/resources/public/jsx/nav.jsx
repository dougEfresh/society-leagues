var React = require('react/addons');
var Bootstrap = require('react-bootstrap')
    ,Navbar = Bootstrap.Navbar
    ,Nav = Bootstrap.Nav
    ,DropdownButton = Bootstrap.DropdownButton
    ,Badge = Bootstrap.Badge;

var ReactRouterBootstrap = require('react-router-bootstrap')
    ,NavItemLink = ReactRouterBootstrap.NavItemLink
    ,MenuItemLink = ReactRouterBootstrap.MenuItemLink;

var Util = require('./util.jsx');

var SocietyNav = React.createClass({
    getInitialState: function() {
        return {
            key: 'home',
            sent: 0,
            pending: 0
        }
    },
    componentDidMount: function() {
        /*
        if (this.props.location === "") {
            this.setState({key: 'home'});
        }
        if (this.props.location.indexOf('stats') >= 0) {
            this.setState({key: 'stats'})
        }

        if (this.props.location.indexOf('challenge') >= 0) {
            this.setState({key: 'challenge'})
        }
        */
        //TODO check to see if logged in
        Util.getData('/challenge/counters', function(d) {
            this.setState({sent: d[0], pending:d[1]});
        }.bind(this));
    },
    render: function() {
	var indicator = 'Challenges';
	if (this.state.sent + this.state.pending > 0) {
	    indicator = (<span>Challenges <Badge>{this.state.sent + this.state.pending}</Badge></span>);
    }
        var navBarInstance = (
            <Navbar brand="Blah" toggleNavKey={this.state.key}>
                <Nav bsStyle="pills" fluid fixedTop activeKey={this.state.key} toggleNavKey={this.state.key}>
                    <NavItemLink to='home' eventKey={"home"}>Home</NavItemLink>
                    <DropdownButton  eventKey={"stats"} title={'Stats'} navItem={true}>
                        <MenuItemLink to='stats' eventKey={"text"} href={'home.html?stats=text'}>Text</MenuItemLink>
                        <MenuItemLink to='stats' eventKey={"graphs"} href={'home.html?stats=graph'}>Graphs</MenuItemLink>
                    </DropdownButton>
                    <DropdownButton  eventKey={"challenge"} title={indicator} navItem={true}>
                        <MenuItemLink  to='request' eventKey={"sent"} >Sent <Badge>{this.state.sent}</Badge></MenuItemLink>
                        <MenuItemLink  to='request' eventKey={"pending"}>Pending <Badge>{this.state.pending}</Badge></MenuItemLink>
                        <MenuItemLink  to='request' eventKey={"request"}>Make Request</MenuItemLink>
                        <MenuItemLink  to='request' eventKey={"history"}>History</MenuItemLink>
                    </DropdownButton>
                </Nav>
            </Navbar>
        );
        return (navBarInstance);
    }
});

module.exports = SocietyNav;
