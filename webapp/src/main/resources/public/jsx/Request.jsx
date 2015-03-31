var React = require('react/addons');
var Util = require('./util.jsx');
var Bootstrap = require('react-bootstrap');
var MultiSelector = require('./MultiSelector.jsx');
var Input = Bootstrap.Input;
var Table = Bootstrap.Table;
var Button = Bootstrap.Button;
var Panel = Bootstrap.Panel;
var Badge = Bootstrap.Badge;
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
            userId : 0
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
                }
            }
        );
    },
    handleClick: function(){
        var challenge = {};
        challenge.challenger = {id: this.state.userId};
        challenge.opponent = {id: this.state.data.opponent.user.id};
        challenge.slots = [];
        this.refs.slots.getValue().forEach(function(s){
            challenge.slots.push({id: s});
        });
        challenge.nine = this.refs.types.getValue().nine;
        challenge.eight = this.refs.types.getValue().eight;
        console.log(JSON.stringify(challenge));
        Util.sendData(challenge,'/challenge/request', function(d) {
	    this.setState({data: 
			   {opponent: null, date: this.state.date}
			  });
        }.bind(this));

    },
    render: function () {
        var submit = (<Button bsStyle='primary' onClick={this.handleClick}>Submit</Button>);
        return (
            <div>
              <PendingChallenges userId={this.state.userId}/>
                <Panel header={'Request'} footer={submit}>
                    <ChallengeDate  ref='date' date={this.state.data.date} onChange={this.onChange}/>
                    <ChallengeUsers ref='opponent' userId={this.state.userId} onChange={this.onChange}/>
                    <ChallengeType  ref='types' opponent={this.state.data.opponent} onChange={this.onChange}/>
                    <ChallengeSlot  ref='slots' display={this.state.data.opponent !== null} date={this.state.data.date} onChange={this.onChange}/>
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
            rows.push(<tr><td>{c.date}</td></tr>);
        });
        return (
            <div>
                <Panel collapsable defaultExpanded header={'Pending'}>
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
            </div>
        );
    }
});

var ChallengeDate = React.createClass({
    getInitialState: function () {
        return {selected: this.props.date}
    },
    handleDateChange: function () {
        if (this.refs.date.getValue().length != 10)
            return;

        this.setState(
          {selected: this.refs.date.getValue()}
        );
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
        if (nextProps.userId == '0' || nextProps.userId == this.props.userId)
            return;
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
        return ({ nine: this.refs.nine.getChecked(), eight: this.refs.eight.getChecked()});
    },
    render: function () {
        if (this.state.opponent === null) {
            return null;
        }
        var nineLabel = (<Badge>9</Badge>);
        var eightLabel = (<Badge>8</Badge>);
        var games = [];
        if (this.state.opponent.nineBallPlayer) {
            games.push(<Input key='9' className="nine-ball" ref='nine' type='checkbox' label={nineLabel} />);
        }

        if (this.state.opponent.eightBallPlayer) {
            games.push(<Input key='8' className="eight-ball" ref='eight' type='checkbox' label={eightLabel}/>);
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
        return (
            <div className={this.state.display ? 'form-show' : 'form-hide'}>
                <MultiSelector ref={'slots'}
                               filter={false}
                               url={'/challenge/slots/' + this.state.date}
                               field={'time'}
                               label={'Slot'}/>
            </div>
        );
    }
});

var RequestChallenge = React.createClass({
    getDefaultProps: function () {
        return {
            url: "/challenge/request",
            potentials: "/challenge/potentials",
            slots: "/challenge/slots",
            userPlayers: "/userPlayers",
            userStats: "/userStats"
        }
    },
    getInitialState: function () {
        return {
            date: "2015-04-06", data: [], players: []
        };
    },
    getPotentials: function () {
        console.log("Getting Data...");
        $.ajax({
            async: true,
            url: this.props.potentials,
            dataType: 'json',
            success: function (data) {
                this.setState({data: data, user: this.state.players});
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(this.props.potentials, status, err.toString());
            }.bind(this)
        });
    },
    getPlayer: function () {
        console.log("Getting User...");
        $.ajax({
            async: true,
            url: this.props.userPlayers,
            dataType: 'json',
            success: function (data) {
                this.setState({players: data, data: this.state.data});
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(this.props.potentials, status, err.toString());
            }.bind(this)
        });
    },
    componentDidMount: function () {
        this.getPlayer();
        this.getPotentials();

    },
    sendChallenge: function (c) {

    },
    onChallengeRequest: function (d) {
        var challenge = {};
        if (this.state.players[0].division.type === d.opponent.division.type) {
            challenge.challenger = this.state.players[0];
        } else {
            challenge.challenger = this.state.players[1];
        }
        challenge.opponent = d.opponent;
        challenge.challengeTimes = [];
        d.times.forEach(function (t) {
            challenge.challengeTimes.push(d.date + 'T' + t);
        });
        console.log(JSON.stringify(challenge));
        $.ajax({
            processData: false,
            async: true,
            contentType: 'application/json',
            url: this.props.url,
            dataType: 'json',
            method: 'post',
            data: JSON.stringify(challenge),
            success: function (data) {
                console.log(JSON.stringify(data));
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    render: function () {
        var rows = [];
        this.state.data.forEach(function (c) {
            rows.push(<ChallengeRow challenge={c} key={c.user.id} handleChallenge={this.onChallengeRequest}/>);
        }.bind(this));

        return (
            <div>
                <table className="table">
                    <thead>
                    <tr>
                        <th>Action</th>
                        <th>Player</th>
                    </tr>
                    </thead>
                    <tbody>
                    {rows}
                    </tbody>
                </table>
            </div>);
    }
});

module.exports = RequestPage;

