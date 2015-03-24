var React = require('react/addons');
var t = require('tcomb-form');
var Form = t.form.Form;
var Util = require('./util.jsx');
var Bootstrap = require('react-bootstrap');
var Input = Bootstrap.Input;
var Button = Bootstrap.Button;
var moment = require('moment');
//var DatePicker = require('react-datepicker');

var RequestPage = React.createClass({
    getInitialState: function() {
        var m = moment();
        return {
            date: m
        }
    },
    changeDate: function(d) {
        this.setState({date: moment(d)});
    },
    render: function() {
        return (<div>
            <ChallengeDate date={this.state.date} handleDateChange={this.changeDate}/>
            <ChallengeUsers date={this.state.date}/>
        </div>)
    }
});

var DateType = t.struct({
    date: t.Str
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
        console.log('Date:' + JSON.stringify(this.refs.form.getValue()));
        if (this.refs.form.getValue() === null)
            return;
        var d = this.refs.form.getValue().date;
        if (d !== undefined) {
            this.setState({data:this.refs.form.getValue()});
            if (d.length === 10) {
                this.props.handleDateChange(this.refs.form.getValue().date);
            }
        }
    },
    render: function() {
        return (
            <div>
                <Form ref="form" type={DateType} value={this.state.data} onChange={this.handleDateChange} />
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
    componentDidMount: function() {
        Util.getData('/challenge/potentials', function(d) {
            this.setState({potentials: d});
        }.bind(this));
    },
    handleChange: function(e) {
        var op = null;
        this.state.potentials.forEach(function(p){
            if (p.user.id == e.target.value) {
                op = p;
            }
        });
        this.setState({opponent: op});
    },
    render: function() {
        var potentials = [];
        potentials.push(<option key={"-1"} value={-1}>{"-----"}</option>);
        this.state.potentials.forEach(function(p){
            potentials.push(<option key={p.user.id} value={p.user.id}>{p.user.name}</option>);
        });
        var eightDisable = function(p) {
            return  this.state.opponent == null || this.state.opponent.eightBallPlayer === null || this.state.opponent.eightBallPlayer === undefined;
        }.bind(this);
        var nineDisable = function(p) {
            return  this.state.opponent == null || this.state.opponent.nineBallPlayer === null || this.state.opponent.eightBallPlayer === undefined;
        }.bind(this);

        return (
            <div>
            <Input type={"select"} label={"Choose Player"} onChange={this.handleChange} defaultValue={this.state.opponent} >
                {potentials}
            </Input>
                <div style={{display: eightDisable(this.state.opponent) ? 'none' : 'inline' }}>
                <Input disabled={eightDisable(this.state.opponent)} type="checkbox" label={"8"}></Input>
                </div>
                 <div style={{display: nineDisable(this.state.opponent) ? 'none' : 'inline' }}>
                     <Input type="checkbox" label={"9"}></Input>
                 </div>
		 <TimeSlots date={this.props.date} />
            </div>
        );
    }
});

var TimeSlots = React.createClass({
    getInitialState: function() {
    return {
          slots: {},
 	  selected: [],
	  type: t.struct({
	    time: t.list(t.Str)
	  })
    }   
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
    if (this.props.date === null || this.props.date === undefined ) {
        return (<div></div>)
    } else {
           return (<Form type={this.state.type}/>);
	}  
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

var UserStat = React.createClass({
    render: function() {
        return (
            <div>
                <tr>
                    <td>{this.props.name}</td>
                </tr>
            </div>
        );
    }
});

function render() {
    React.render(<RequestPage />, document.getElementById('content'));
}

module.exports = {render: render};

