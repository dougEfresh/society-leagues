var React = require('react/addons');
var Bootstrap = require('react-bootstrap');
var Navbar = Bootstrap.Navbar;
var Nav = Bootstrap.Nav;
var NavItem = Bootstrap.NavItem;
var DropdownButton = Bootstrap.DropdownButton;
var MenuItem = Bootstrap.MenuItem;
var Badge = Bootstrap.Badge;

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
        if (this.props.location === "") {
            this.setState({key: 'home'});
        }
        if (this.props.location.indexOf('stats') >= 0) {
            this.setState({key: 'stats'})
        }

        if (this.props.location.indexOf('challenge') >= 0) {
            this.setState({key: 'challenge'})
        }
        //TODO check to see if logged in
        Util.getData('/challenge/counters', function(d) {
            this.setState({sent: d[0], pending:d[1]});
        }.bind(this));
    },
    render: function() {
        //<Badge>10</Badge>
        var navBarInstance = (
            <Navbar brand="Society" toggleNavKey={this.state.key}>
                <Nav bsStyle="pills" fluid fixedTop activeKey={this.state.key} toggleNavKey={this.state.key}>
                    <NavItem eventKey={"home"} href={'home.html'}>Home</NavItem>
                    <NavItem eventKey={"stats"} href={'home.html?stats='}>Stats</NavItem>
                    <DropdownButton  eventKey={"challenge"} title={"Challenges"} navItem={true}>
                        <MenuItem eventKey={"sent"} href={'home.html?challenge=sent'}>Sent <Badge>{this.state.sent}</Badge></MenuItem>
                        <MenuItem eventKey={"pending"} href={'home.html?challenge=pending'}>Pending <Badge>{this.state.pending}</Badge></MenuItem>
                        <MenuItem eventKey={"request"} href={'home.html?challenge=request'}>Make Request</MenuItem>
                        <MenuItem eventKey={"history"} href={'home.html?challenge=history'}>History</MenuItem>
                    </DropdownButton>
                </Nav>
            </Navbar>
        );
        return (navBarInstance);
    }
});

function render() {
    React.render(<SocietyNav location={window.location.search}/>, document.getElementById('societyNav'));
}

module.exports = {render: render};
