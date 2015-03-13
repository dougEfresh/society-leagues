var Challenges = React.createClass({

    getInitialState: function() {
        return {data: []};
    },
    getData: function() {
        console.log("Getting data...");
        $.ajax({
            async: false,
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
    accept: function(c) {
        $.ajax({
            async: false,
            url: '/challenge/accept/' + c.challenge.id,
            dataType: 'json',
            method: 'GET',
            success: function(data) {
                console.log("Accept challenge " + c.challenge.id);
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    cancel: function(c) {
        $.ajax({
            async: false,
            url: '/challenge/cancel/' + c.challenge.id,
            dataType: 'json',
            method: 'GET',
            success: function(data) {
                console.log("Cancel challenge " + c.challenge.id);
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },

    onUserInput: function(action,c) {
        if (action === 'a'){
            this.accept(c);
        }

        if (action === 'c') {
            this.cancel(c);
        }

        this.getData();
    },

    render: function() {
        return (
            <div className="challengList">
                <ChallengeTable ref="challengeForm" challenges = {this.state.data} onUserInput={this.onUserInput}/>
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
        var date = dt(this.props.challenge.challenge.challengeDate);
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
                <td>{date}</td>
            </tr>
        );
    }
});

var ChallengeAction = React.createClass({
    render: function () {
        return (
            <div>
                <ChallengeAccept challenge={this.props.challenge} onUserInput={this.props.onUserInput}  />
                <ChallengModify challenge={this.props.challenge} onUserInput={this.props.onUserInput}   />
                <ChallengCancel challenge={this.props.challenge} onUserInput={this.props.onUserInput}   />
            </div>
        );
    }
});

var ChallengeAccept = React.createClass({

    handleAccept : function(c) {
        this.props.onUserInput('a',c);
        this.forceUpdate();
    },

    render: function() {
        var s = {
            "display" : this.props.challenge.challenge.status == 'PENDING' ? 'on' : 'on'
        };
        return (
            <div style = {s} >
                <button  onClick={this.handleAccept.bind(this,this.props.challenge)} ref="accept" name="accept">Accept</button>
            </div>
        );
    }
});

var ChallengCancel = React.createClass({
    handleCancel: function(c) {
        this.props.onUserInput('c',c);
        this.forceUpdate();
    },
    render: function() {
        return (
            <button onClick={this.handleCancel.bind(this,this.props.challenge)} ref="cancel" name="cancel">Cancel</button>
        );
    }
});

var ChallengModify = React.createClass({
    handleModify: function(c) {

    },

    render: function() {
        return (
            <button onClick={this.handleModify.bind(this,this.props.challenge)} ref="modify" name="modify">Modify</button>
        );
    }
});


React.render(
    <Challenges url="/challenges" />,
    document.getElementById('content')
);


