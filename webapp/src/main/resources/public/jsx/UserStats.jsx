var React = require('react/addons');

var UserStats = React.createClass({
    getInitialState: function() {
        return {data: []};
    },
    componentDidMount: function() {
    $.ajax({
      url: this.props.url,
      dataType: 'json',
      success: function(stats) {
        this.setState({data: stats});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  render: function() {
    return (
        <div className="userStats">
            <UserStatsTable stats = {this.state.data}/>
        </div>
    );
  }
});

var UserStatsTable = React.createClass({
    render: function() {
        var rows = [];
        this.props.stats.forEach(function (s) {
            rows.push(<UserStatsRow stat={s} key={s.user.id} />);
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

module.exports = {UserStats: UserStats, UserStatsTable: UserStatsTable};
