var RequestChallengePage = React.createClass({
    getInitialState: function() {
        return {
            data: [], players: []
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

var ChallengeRow = React.createClass({
    getInitialState: function() {
        return {hide: true};
    },
    handleClick: function() {
        this.setState({hide: !this.state.hide});
    },
    render: function() {
        return (
            <div>
            <tr>
                <td><button type="button" onClick={this.handleClick} name="challenge" /></td>
                <td>{this.props.challenge.user.name}</td>
            </tr>
                <RequestChallengeRow challenge={this.props.challenge} hide={this.state.hide} onChallenge={this.props.handleChallenge} />
            </div>
        );
    }
});

var RequestChallengeRow = React.createClass({

    handleClick: function() {
        var challenge = {};
        if (this.refs.nineball.getDOMNode().checked) {
            challenge.opponent = this.props.challenge.nineBallPlayer;
        } else {
            challenge.opponent = this.props.challenge.eightBallPlayer;
        }
        challenge.date = this.refs.date.getDOMNode().value;
        challenge.times = [];
        challenge.times.push(this.refs.time.getDOMNode().value);
        this.props.onChallenge(challenge);
    },
    render: function() {
        var disp = this.props.hide ? "none" : "inline";
        return (
        <div style={{"display": disp}}>
            9ball <input ref="eightball" type="checkbox" />
            8ball <input ref="nineball" type="checkbox" />
            Date: <input ref="date" type="text" />
            Time: <input ref="time" type="text" />
            Challenge: <button type="button" onClick={this.handleClick} />
        </div>
        );
    }
});

var Submit = React.createClass({
    render: function() {
        return (
            <div>
            <button ref={this.props.opponent} name="submit"></button>
            </div>
        );
    }
});

var NineBallBox = React.createClass({
    render: function() {
        return (
            <div>
                <input ref={"nineball"} type="checkbox" /> 9
            </div>
        );
    }
});

var EighBallBox = React.createClass({
    render: function() {
        return (
            <div>
                <input ref={"eightball"} type="checkbox" /> 9
            </div>
        );
    }
});

var DatePicker = React.createClass({
    render : function() {

    }
});

React.render(
    <RequestChallengePage url="/challenge/request" potentials="/challenge/potentials" slots="/challenge/slots" userPlayers="/userPlayers" />,
    document.getElementById('content')
);
