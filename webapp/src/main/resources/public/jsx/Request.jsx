var React = require('react/addons');
var Util = require('./util.jsx');
var MultiSelector = require('./MultiSelector.jsx');
var Bootstrap = require('react-bootstrap')
,Input = Bootstrap.Input
,Table = Bootstrap.Table
,Button = Bootstrap.Button
,Panel = Bootstrap.Panel
,Badge = Bootstrap.Badge;
var moment = require('moment');

var RequestPage = React.createClass({
    contextTypes: {
        router: React.PropTypes.func
    },
    getInitialState: function () {
        var m = moment();
        return {
            data: {
                date: m.format('YYYY-MM-DD'),
                opponent: null
            },
            userId : 0,
            submitted: false,
            valid: false,
            errors: []
        }
    },
    componentDidMount: function() {
        this.setState({userId: parseInt(this.context.router.getCurrentParams().userId)});
    },
    onChange: function () {
        this.setState({
                data: {
                    date: this.refs.date.getValue(),
                    opponent: this.refs.opponent.getValue()
                },
                valid : this.isValid()
            }
        );
    },
    getErrors: function() {
        var errors = [];
        var challenge = this.getChallenge();
        if (!challenge.opponent)
            errors.push('Need an opponent');
        if (!challenge.nine && !challenge.eight)
            errors.push('Please choose game type');
        if (challenge.slots.length == 0)
            errors.push('Please choose a time');

        return errors;
    },
    isValid: function(){
        return this.getErrors().length == 0;
    },
    getChallenge: function(){
        var challenge = {};
        challenge.challenger = {id: this.state.userId};
        challenge.opponent = this.refs.opponent && this.refs.opponent.getValue() ? {id: this.refs.opponent.getValue().user.id} : null;
        challenge.slots = [];
        if (this.refs.slots && this.refs.slots.getValue()) {
            this.refs.slots.getValue().forEach(function (s) {
                challenge.slots.push({id: s});
            });
        }
        challenge.nine =  this.refs.types ?  this.refs.types.getValue().nine : false;
        challenge.eight = this.refs.types ?  this.refs.types.getValue().eight : false;
        return challenge;
    },
    handleClick: function(){
        if (!this.isValid) {
            this.setState({errors: this.getErrors()});
            return ;
        }
        var challenge = this.getChallenge();
        Util.sendData(challenge,'/challenge/request', function(d) {
            this.setState(this.getInitialState());
        }.bind(this));

    },
    render: function () {
        var submit = (<Button bsStyle='primary' disabled={!this.state.valid} onClick={this.handleClick}>Challenge</Button>);
        return (
            <div>
              <PendingChallenges userId={this.state.userId}/>
                <Panel header={'Request'} footer={submit}>
                    <ChallengeDate  ref='date' date={this.state.data.date} onChange={this.onChange}/>
                    <ChallengeUsers ref='opponent' userId={this.state.userId} onChange={this.onChange}/>
                    <ChallengeType  ref='types' opponent={this.state.data.opponent} onChange={this.onChange} />
                    <ChallengeSlot  ref='slots' display={this.state.data.opponent !== null} date={this.state.data.date} onChange={this.onChange} />
                </Panel>
            </div>
        )
    }
});

var PendingChallenges = React.createClass({
    getDefaultProps: function() {
        return {
            userId: 0
        }
    },
    getInitialState: function() {
        return {
            challenges: []
        }
    },
    componentWillMount: function() {
        if (this.props.userId  == 0) {
            return;
        }
        Util.getData('/challenges/pending/' + this.props.userId, function(d){
            this.setState({challenges: d});
        }.bind(this))
    },
    componentWillReceiveProps: function (nextProps) {
        if (nextProps.userId  == 0) {
            return;
        }
        Util.getData('/challenges/pending/' + nextProps.userId, function(d){
            this.setState({challenges: d});
        }.bind(this))
    },
    render: function() {
        if (this.props.userId == 0) {
            return null;
        }
        var rows = [];
        this.state.challenges.forEach(function(c){
            rows.push(<tr key={c.id}><td >{c.date}</td></tr>);
        });
        var header = (<span>Pending <Badge>{this.state.challenges.length}</Badge></span>);
        return (
                <Panel collapsable defaultCollapsed  header={header}>
                    <Table striped bordered condensed hover>
                        <thead>
                        <tr>
                        <th>Date</th>
                        </tr>
                        </thead>
                        <tbody>
                        {rows}
                        </tbody>
                    </Table>
                </Panel>
        );
    }
});

var PendingRow = React.createClass({
    getDefaultProps: function() {
        return {
            challenge : null
        }
    },
    render: function() {
        return null;
    }
});

var ChallengeDate = React.createClass({
    getInitialState: function () {
        return {selected: this.props.date}
    },
    handleDateChange: function () {
        this.setState(
          {selected: this.refs.date.getValue()}
        );
        this.props.onChange();
    },
    getValue: function() {
        return this.refs.date.getValue();
    },
    componentDidMount: function () {

    },
    render: function () {
        var dates = [];
        var now = moment().format('YYYY-MM-DD');
        dates.push((<option key={now} value={now}>{now}</option>));
        [1, 2, 3, 4].forEach(function (d) {
            var date = moment().add(d, 'weeks').format('YYYY-MM-DD');
            dates.push((<option key={date} value={date}>{date}</option>));
        });
        return (
            <div>
                <Input ref="date" type={'select'} onChange={this.handleDateChange}>
                    {dates}
                </Input>
            </div>
        );
    }
});

var ChallengeUsers = React.createClass({
    propTypes: {
        userId:  React.PropTypes.number
    },
    getInitialState: function () {
        return {
            opponent: null,
            potentials: []
        }
    },
    getValue: function () {
        return this.state.opponent;
    },
    componentDidMount: function () {
        if (this.props.userId == '0')
            return;
        Util.getData('/challenge/potentials/' + this.props.userId, function (d) {
            this.setState({potentials: d});
        }.bind(this));
    },
    componentWillReceiveProps: function (nextProps) {
        if (nextProps.userId == '0' || nextProps.userId == this.props.userId) {
            return;
        }
         Util.getData('/challenge/potentials/' + nextProps.userId, function (d) {
            this.setState({potentials: d});
        }.bind(this));
    },
    handleChange: function () {
        var op = this.refs.opponent.getValue();
        var opponent = null;
        this.state.potentials.forEach(function (p) {
            if (p.user.id == op) {
                opponent = p;
            }
        });
        //TODO: FIX ME
        this.state.opponent = opponent;
        this.props.onChange();
    },
    render: function () {
        var potentials = [];
        potentials.push(<option key={"-1"} value={-1}>{"-----"}</option>);
        this.state.potentials.forEach(function (p) {
            potentials.push(<option key={p.user.id} value={p.user.id}>{p.user.name}</option>);
        });

        return (
            <Input ref='opponent' type={'select'} label={'Choose your enemy'} onChange={this.handleChange}>
                {potentials}
            </Input>
        );
    }
});

var ChallengeType = React.createClass({
    getInitialState: function () {
        return {
            opponent: null
        }
    },
    componentWillReceiveProps: function (nextProps) {
        this.setState({opponent: nextProps.opponent});
    },

    componentDidMount: function () {
        if (this.props.opponent === null) {
            return;
        }
        this.setState({
            opponent: this.props.opponent
        })
    },
    getValue: function() {
        return ({ nine: this.refs.nine == undefined ? false : this.refs.nine.getChecked(), eight: this.refs.eight == undefined ? false : this.refs.eight.getChecked()});
    },
    render: function () {
        if (this.state.opponent === null) {
            return null;
        }
        var nineLabel = (<Badge>9</Badge>);
        var eightLabel = (<Badge>8</Badge>);
        var games = [];
        if (this.state.opponent.nineBallPlayer) {
            games.push(<Input key='9' className="nine-ball" ref='nine' type='checkbox' label={nineLabel} onChange={this.props.onChange}></Input>);
        }

        if (this.state.opponent.eightBallPlayer) {
            games.push(<Input key='8' className="eight-ball" ref='eight' type='checkbox' label={eightLabel} onChange={this.props.onChange} ></Input>);
        }

        return (<div>{games}</div>);
    }
});

var ChallengeSlot = React.createClass({
    getInitialState: function () {
        return {
            display: false,
            date: this.props.date
        }
    },
    componentWillReceiveProps: function (nextProps) {
        this.setState(
            {display: nextProps.display, date: nextProps.date});
    },
    getValue: function() {
        return this.refs.slots.getValue();
    },
    render: function () {
        if (!this.state.date)
            return null;

        return (
                <MultiSelector ref={'slots'}
                               filter={false}
                               url={'/challenge/slots/' + this.state.date}
                               field={'time'}
                               label={'Slot'}
                               onChange={this.props.onChange}
                    />

        );
    }
});

module.exports = RequestPage;

