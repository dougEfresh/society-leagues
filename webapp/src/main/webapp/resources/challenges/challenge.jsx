var Challenges = React.createClass({
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
            <div className="challengList">
                <ChallengeTable challenges = {this.state.data}/>
            </div>
        );
    }
});

var ChallengeTable = React.createClass({

    render: function() {
        var rows = [];
        this.props.challenges.forEach(function (s) {
            rows.push(<ChallengeRow challenge={s} key={s.challenger.id} />);
        }.bind(this));
        return (
            <table className="table">
                <thead>
                <tr>
                    <th>Action</th>
                    <th>Status</th>
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

var ChallengeRow = React.createClass({
    render: function() {
        return (
            <tr>
                <td>
                    <ChallengeAction challenge={this.props.challenge} />
                </td>
                <td>{this.props.challenge.challenger.user.name}</td>
                <td>{this.props.challenge.opponent.user.name}</td>
                <td>{this.props.challenge.challenger.division.type}</td>
                <td>{this.props.challenge.challenge.challengeDate}</td>
            </tr>
        );
    }
});

var ChallengeAction = React.createClass({
    handleAccept : function(e) {
        console.log(e);
    },
    handle : function(e) {
        console.log(e);
    },
    render: function () {
        return (
            <div className="pending-action">
                <button onClick={this.handleAccept} ref="acceptButton" value={this.props.challenge.challenger.id} name="accept">Accept</button>
                <button onClick={this.handle} ref="modifyButton" value={this.props.challenge.challenger.id} name="modify">Modify</button>
            </div>
        );
    }
});

React.render(
    <Challenges url="/challenges" />,
    document.getElementById('content')
);


