var RequestChallengePage = React.createClass({
    render: function() {
        return (<div>Hello</div>)
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
    <RequestChallengePage url="/challenge/request" potentials="/potentials" />,
    document.getElementById('content')
);
