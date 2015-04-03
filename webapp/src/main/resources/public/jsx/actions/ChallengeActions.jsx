var AppDispatcher = require('../dispatcher/AppDispatcher.jsx');
var RequestConstants = require('../constants/ChallengeConstants.jsx');
var moment = require('moment');
var ChallengeActions = {
    /**
     * @param  {object} challenge
     */
        /*
    create: function(challenge) {
        AppDispatcher.dispatch({
            actionType: RequestConstants.CHALLENGE_CREATE,
            challenge: challenge
        });
    },
    */

    /**
     * @param  {date} string
     */
    dateChange: function(date) {
        AppDispatcher.dispatch({
            actionType: RequestConstants.CHALLENGE_DATE_CHANGE,
            date: date
        });
    }
};

module.exports = ChallengeActions;
