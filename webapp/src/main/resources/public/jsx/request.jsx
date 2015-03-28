var React = require('react/addons');
var t = require('tcomb-form');
var Form = t.form.Form;
var Util = require('./util.jsx');
var Bootstrap = require('react-bootstrap');
var Input = Bootstrap.Input;
var Button = Bootstrap.Button;
var Panel = Bootstrap.Panel;
var moment = require('moment');
//var DatePicker = require('react-datepicker');
//var classSet = require('classnames');

var RequestPage = React.createClass({
    getInitialState: function() {
        var m = moment();
        return {
            data: {
                date: m,
                opponent: null,
                slots: []
            }
        }
    },
    onChange: function() {
        this.setState({data: {opponent: this.refs.opponent.getValue()}});
    },
    render: function() {
        return (
            <div>
                <Panel header={'Choose your enemy'} footer={'Submit'}>
                    <ChallengeDate  ref='date' date={this.state.data.date}/>
                    <ChallengeUsers ref='opponent' onChange={this.onChange}/>
                    <ChallengeType player={this.state.data.opponent} />
                    <TimeSlots display={this.state.data.opponent !== null} date={this.state.data.date} />
                </Panel>
            </div>
        )
    }
});

var ChallengeDate = React.createClass({
    getInitialState: function() {
        return {
            data: {
                date: this.props.date.format('YYYY-MM-DD')
            }
        }
    },
    handleDateChange: function() {
        if (this.refs.date.getValue().length != 10)
            return;

        this.setState(
            {data: {date: this.refs.date.getValue()}}
        );
    },
    render: function() {
        return (
            <div>
                <Input ref="date" type={'text'} value={this.state.data.date} onChange={this.handleDateChange} />
            </div>
        );
    }
});

var ChallengeUsers = React.createClass({
    getInitialState: function(){
        return {
            opponent : null,
            potentials: []
        }
    },
    getValue: function() {
        return this.state.opponent;
    },
    componentDidMount: function() {
        Util.getData('/challenge/potentials', function(d) {
            this.setState({potentials: d});
        }.bind(this));
    },
    handleChange: function() {
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
    render: function() {
        var potentials = [];
        potentials.push(<option key={"-1"} value={-1}>{"-----"}</option>);
        this.state.potentials.forEach(function(p){
            potentials.push(<option key={p.user.id} value={p.user.id}>{p.user.name}</option>);
        });

        return (
            <Input ref='opponent' type={"select"} label={"Choose Player"} onChange={this.handleChange} >
                {potentials}
            </Input>
        );
    }
});

var ChallengeType = React.createClass({

    getInitialState: function() {
        return {
                nine: true,
                eight: true,
                player: null
        }
    },
    componentWillReceiveProps: function(nextProps) {
        this.setState({ player: nextProps.player });
    },
    componentDidMount: function() {
        if (this.props.player === null) {
            return;
        }
        this.setState({
                nine: this.props.player.hasNine,
                eight: this.props.player.hasEight,
                player: this.props.player
        })
    },
    render: function() {
        if (this.state.player !== null) {
            return (
                <div>
                    <Input type='checkbox' label='9' disable={this.state.nine}></Input>
                    <Input type='checkbox' label='8' disable={this.state.eight}></Input>
                </div>
            );
        } else {
            return <div></div>;
        }
    }
});

var TimeSlots = React.createClass({
    getInitialState: function() {
    return {
        slots: {},
        selected: [],
        type: t.struct({
            time: t.list(t.Str)
        }),
        display: false
    }   
    },
    componentWillReceiveProps: function(nextProps) {
        this.setState({display: nextProps.display });
    },
    componentDidMount: function() {
        Util.getData('/challenge/slots/' + this.props.date.format('YYYY-MM-DD'), function(d) {
	var slots = {};
	slots[d.id + ''] = d.time;
	var type = t.list(t.enums(slots));
	this.setState({slots: slots});
	}.bind(this));
    },
    render: function() {
           return (<div className={this.state.display ? 'form-show' : 'form-hide'}> <Form className={this.props.className} type={this.state.type}/> </div>);
    }
});

var RequestChallenge = React.createClass({
    getDefaultProps: function() {
        return {
            url:"/challenge/request" ,
            potentials:"/challenge/potentials" ,
            slots:"/challenge/slots" ,
            userPlayers:"/userPlayers",
            userStats:"/userStats"
        }
    },
    getInitialState: function() {
        return {
            date: "2015-04-06", data: [], players: []
        };
    },
    getPotentials: function() {
        console.log("Getting Data...");
        $.ajax({
            async: true,
            url: this.props.potentials,
            dataType: 'json',
            success: function(data) {
                this.setState({data: data, user: this.state.players});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.potentials, status, err.toString());
            }.bind(this)
        });
    },
    getPlayer: function() {
        console.log("Getting User...");
        $.ajax({
            async: true,
            url: this.props.userPlayers,
            dataType: 'json',
            success: function(data) {
                this.setState({players: data, data: this.state.data});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.potentials, status, err.toString());
            }.bind(this)
        });
    },
    componentDidMount: function() {
        this.getPlayer();
        this.getPotentials();

    },
    sendChallenge: function(c) {

    },
    onChallengeRequest: function(d) {
        var challenge = {};
        if (this.state.players[0].division.type === d.opponent.division.type ) {
            challenge.challenger = this.state.players[0];
        } else {
            challenge.challenger = this.state.players[1];
        }
        challenge.opponent = d.opponent;
        challenge.challengeTimes = [];
        d.times.forEach(function(t){
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
             success: function(data) {
                 console.log(JSON.stringify(data));
             }.bind(this),
             error: function(xhr, status, err) {
                 console.error(this.props.url, status, err.toString());
             }.bind(this)
         });
    },
    render: function() {
        var rows = [];
          this.state.data.forEach(function (c) {
              rows.push(<ChallengeRow challenge={c} key={c.user.id} handleChallenge={this.onChallengeRequest} />);
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

module.exports = {render: render};

