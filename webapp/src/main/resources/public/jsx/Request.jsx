var React = require('react/addons');
var Util = require('./util.jsx');
var Bootstrap = require('react-bootstrap');
var MultiSelector = require('./MultiSelector.jsx');
var Input = Bootstrap.Input;
var Button = Bootstrap.Button;
var Panel = Bootstrap.Panel;
var Badge = Bootstrap.Badge;
var moment = require('moment');

var RequestPage = React.createClass({
    getInitialState: function () {
        var m = moment();
        return {
            data: {
                date: m.format('YYYY-MM-DD'),
                challenger: null,
                opponent: null,
                slots: []
            }
        }
    },
    onChange: function () {
        this.setState({
                data: {
                    opponent: this.refs.opponent.getValue(),
                    date: this.refs.date.getValue(),
                    slots: this.refs.slot.getValue()
                }
            }
        );
    },
    handleClick: function(){
        console.log('asdsd');
    },
    render: function () {
        var submit = (<Button bsStyle='primary' onClick={this.handleClick}>Submit</Button>);
        return (
            <div>
                <Panel header={'Request'} footer={submit}>
                    <ChallengeDate ref='date' date={this.state.data.date} onChange={this.onChange}/>
                    <ChallengeUsers ref='opponent' onChange={this.onChange}/>
                    <ChallengeType ref='type' opponent={this.state.data.opponent}/>
                    <ChallengeSlot ref='slot' display={this.state.data.opponent !== null} date={this.state.data.date}/>
                </Panel>
            </div>
        )
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
        Util.getData('/challenge/potentials', function (d) {
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
            nine: false,
            eight: false,
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
    onChange: function(){
        this.setState({
            nine: this.refs.nine.getChecked(),
            eight: this.refs.eight.getChecked()
        })
    },
    render: function () {
        if (this.state.opponent === null) {
            return null;
        }
        var nineLabel = (<Badge>9</Badge>);
        var eightLabel = (<Badge>8</Badge>);
        return (
            <div>
                <Input className="nine-ball" ref='nine' type='checkbox' label={nineLabel}   checked={this.state.nine} onChange={this.onChange}></Input>
                <Input className="eight-ball" ref='eight' type='checkbox' label={eightLabel} checked={this.state.eight} onChange={this.onChange}></Input>
            </div>
        );
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
        this.setState({display: nextProps.display});
    },
    componentDidMount: function () {
    },
    handleChange: function () {

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

function render() {
    if (window.location.search.indexOf('request') !== -1) {
        React.render(<RequestPage />, document.getElementById('content'));
    }
}

module.exports = RequestPage;

