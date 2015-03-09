var UserStats = React.createClass({
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
            rows.push(<UserStatsRow challenge={s} key={s.user.id} />);
        }.bind(this));
        return (
            <table className="table">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Matches</th>
                    <th>Wins</th>
                    <th>Loses</th>
                    <th>Racks</th>
                    <th>Percentage</th>
                </tr>
                </thead>
                <tbody>
                {rows}
                </tbody>
            </table>
        );
    }
});

var UserStatsRow = React.createClass({
    render: function() {
        var p =  Math.round((this.props.stat.loses/ this.props.stat.matches)*100);
        return (
            <tr>
                <td>{this.props.stat.user.name}</td>
                <td>{this.props.stat.matches}</td>
                <td>{this.props.stat.wins}</td>
                <td>{this.props.stat.loses}</td>
                <td>{this.props.stat.racks}</td>
                <td>{p}%</td>
            </tr>
        );
    }
});

React.render(
    <UserStats url="/challenges" />,
    document.getElementById('content')
);


