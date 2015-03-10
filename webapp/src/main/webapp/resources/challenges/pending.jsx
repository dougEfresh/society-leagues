var PendingChallenges = React.createClass({
    getInitialState: function() {
        return {data: []};
    },
    componentDidMount: function() {
        $.ajax({
            url: this.props.url,
            dataType: 'json',
            success: function(data) {
                this.setState({data: data});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
    });
  },
    render: function() {
        return (
            <div className="pendingChallengList">
                <PendingChallengeTable challenges = {this.state.data}/>
            </div>
        );
    }
});

var PendingChallengeTable = React.createClass({

    render: function() {
        var rows = [];
        this.props.challenges.forEach(function (s) {
            rows.push(<PendingChallengeRow challenge={s} key={s.challenger.id} />);
        }.bind(this));
        return (
            <table className="table">
                <thead>
                <tr>
                    <th>Accept/Modify</th>
                    <th>Challenger</th>
                    <th>Opponent</th>
                    <th>Type</th>
                    <th>Date</th>
                </tr>
                </thead>
                <tbody>
                {rows}
                </tbody>
            </table>
        );
    }
});

var PendingChallengeRow = React.createClass({
    render: function() {
        return (
            <tr>
                <td>
                    <PendingChallengeAction challenge={this.props.challenge} />
                </td>
                <td>{this.props.challenge.challenger.user.name}</td>
                <td>{this.props.challenge.opponent.user.name}</td>
                <td>{this.props.challenge.challenger.division.type}</td>
                <td>{this.props.challenge.challenge.challengeDate}</td>
            </tr>
        );
    }
});

var PendingChallengeAction = React.createClass({
    handleAccept : function(e) {
        console.log(e);
    },
    handlePending : function(e) {
        console.log(e);
    },
    render: function () {
        return (
            <div className="pending-action">
                <button onClick={this.handleAccept} ref="acceptButton" value={this.props.challenge.challenger.id} name="accept">Accept</button>
                <button onClick={this.handlePending} ref="modifyButton" value={this.props.challenge.challenger.id} name="modify">Modify</button>
            </div>
        );
    }
});

React.render(
    <PendingChallenges url="/pendingChallenges" />,
    document.getElementById('content')
);


