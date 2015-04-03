var React = require('react/addons');
var ChallengeStore = require('../stores/ChallengeStore.jsx');
var ChallengeRequestDate = require('./ChallengeRequestDate.jsx');

var ChallengeRequestApp = React.createClass({

    getInitialState: function() {
        return {
            challenge: ChallengeStore.get()
        }
    },
    componentDidMount: function() {
        ChallengeStore.addChangeListener(this._onChange);
    },

    componentWillUnmount: function() {
      ChallengeStore.removeChangeListener(this._onChange);
    },

    _onChange: function() {
        this.setState(ChallengeStore.get());
    },

    render: function(){
        return (
            <div>
                <ChallengeRequestDate date={this.state.challenge.date} />
            </div>
        )
    }
});

module.exports = ChallengeRequestApp;