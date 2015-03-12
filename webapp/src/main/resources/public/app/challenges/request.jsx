var RequestChallengePage = React.createClass({
    render: function() {
        return (<div>Hello</div>)
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
