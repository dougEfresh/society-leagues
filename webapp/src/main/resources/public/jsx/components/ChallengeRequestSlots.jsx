var React = require('react/addons');
var ReactPropTypes = React.PropTypes;
var Bootstrap = require('react-bootstrap')
    ,Input = Bootstrap.Input
    ,Button = Bootstrap.Button
    ,ListGroup = Bootstrap.ListGroup
    ,ListGroupItem = Bootstrap.ListGroupItem;

var Util = require('../util.jsx');
var ChallengeActions = require('../actions/ChallengeActions.jsx');

var ChallengeRequestSlots = React.createClass({
    propTypes: {
        slots: ReactPropTypes.array.isRequired,
        date: ReactPropTypes.string.isRequired
    },
    getInitialState: function () {
        return {
            available : {}
        }
    },
    componentWillReceiveProps: function(props) {
        console.log('SLOTS NEW PROPS : ' + JSON.stringify(props));
    },
    componentDidMount: function() {
        Util.getData('/api/challenge/slots/' + this.props.date, function (slots) {
            var available = {};
            slots.forEach(function(slot) {
                available[slot.id] = slot;
            });
            this.setState({available: available});
        }.bind(this));
    },
    onChange: function() {
        var slots = [];
        this.refs.slots.getValue().forEach(function(s) {
                slots.push(this.state.available[s]);
            }.bind(this)
        );
        ChallengeActions.addSlots(slots);
    },
    getOptions: function() {
        var options = [];
        for (var key in this.state.available) {
            options.push(<option key={key} value={key}>{this.state.available[key].time}</option>);
        }
        return options;
    },
    onRemove: function(item) {
        ChallengeActions.removeSlot(this.state.available[item.target.textContent]);
    },
    render: function() {
        var chosen = [];
        this.props.slots.forEach(function(s) {
            var removeIcon = (
                <i className="fa fa-times" >
                    <div style={{display: 'none'}}>{s.id}</div>
                </i>);
            chosen.push(<ListGroupItem  key={s.id}><Button onClick={this.onRemove}>{removeIcon}</Button>{s.time}</ListGroupItem>);
        }.bind(this));

        var disp = this.props.slots.length == 0  ? 'none' : 'inline';
        var chosenGroup = (
            <div style={{display: disp}}>
            <ListGroup label={"Chosen"}>
                {chosen}
            </ListGroup>
            </div>);
        return (
            <div>
                {chosenGroup}
                <Input type='select' multiple ref='slots' label={'Choose Time'} onChange={this.onChange} >{this.getOptions()}</Input>
            </div>
        );
    }
});


module.exports = ChallengeRequestSlots;