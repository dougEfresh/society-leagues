var Challenges = React.createClass({

    getInitialState: function() {
        return {data: []};
    },
    getData: function() {
        console.log("Getting data...");
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
    componentDidMount: function() {
        this.getData();
    },
    onUserInput: function(action,c) {
        $.ajax({
            url: '/challenge/accept/' + c.challenge.id,
            dataType: 'json',
            method: 'GET',
            success: function(data) {
                console.log("Accepted challenge " + c.challenge.id);
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
        this.getData();
        this.forceUpdate();
    },
    render: function() {
        return (
            <div className="challengList">
                <ChallengeTable challenges = {this.state.data} onUserInput={this.onUserInput}/>
            </div>
        );
    }
});

var ChallengeTable = React.createClass({

    render: function() {
        var rows = [];
        this.props.challenges.forEach(function (s) {
            rows.push(<ChallengeRow challenge={s} key={s.challenge.id} onUserInput={this.props.onUserInput}/>);
        }.bind(this));
        return (
            <table className="table">
                <thead>
                <tr>
                    <th>Action</th>
                    <th>Id</th>
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
                    <ChallengeAction challenge={this.props.challenge} onUserInput={this.props.onUserInput} />
                </td>
                <td>{this.props.challenge.challenge.id}</td>
                <td>{this.props.challenge.challenge.status}</td>
                <td>{this.props.challenge.challenger.user.name}</td>
                <td>{this.props.challenge.opponent.user.name}</td>
                <td>{this.props.challenge.challenger.division.type}</td>
                <td>{this.props.challenge.challenge.challengeDate}</td>
            </tr>
        );
    }
});

var ChallengeAction = React.createClass({
    handleAccept : function(c) {
        this.props.onUserInput('a',c);
        this.forceUpdate();
    },
    handlePending : function(e) {

    },
    buttons : function() {
        if (this.props.challenge.challenge.status == 'PENDING') {
            return (
                <div className="pending-action">
                    <button onClick={this.handleAccept.bind(this,this.props.challenge)} ref="acceptButton"
                            value={this.props.challenge.challenger.id}
                            name="accept">Accept
                    </button>
                    <button onClick={this.handlePending} ref="modifyButton" value={this.props.challenge.challenger.id}
                            name="modify">Modify
                    </button>
                </div>);
        }
        return (
            <div className="accepted-action">
                <button onClick={this.handlePending} ref="modifyButton" value={this.props.challenge.challenger.id}
                        name="modify">Modify
                </button>
            </div>);
    },

    render: function () {
        return (this.buttons());
    }
});

React.render(
    <Challenges url="/challenges" />,
    document.getElementById('content')
);


