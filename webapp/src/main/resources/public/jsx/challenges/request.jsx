var t = require('tcomb-form');
var React = require('react');
var UserStatsRow = require('../userStats.jsx');
var Form = t.form.Form;

//var moment = require('moment');
//var DatePicker = require('react-datepicker');

var RequestForm = React.createClass({
    getInitialState: function() {
        return {
            data: {

            }
        }
    },
    render: function() {

    }
});

var RequestChallengePage = React.createClass({
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

module.exports = RequestChallengePage;

