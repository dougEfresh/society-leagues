var AppDispatcher = require('../dispatcher/AppDispatcher.jsx');
var RequestConstants = require('../constants/ChallengeConstants.jsx');

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
    changeDate: function(date) {
        AppDispatcher.dispatch({
            actionType: RequestConstants.CHALLENGE_DATE_CHANGE,
            date: date
        });
    },

    /**
     *  Dupilcate slots will be ignored
     * @param slots
     */
    addSlots: function(slots) {
        AppDispatcher.dispatch({
            actionType: RequestConstants.CHALLENGE_SLOTS_ADD,
            slots: slots
        });
    },

    /**
     * Add a time slot
     * @param slot
     */
    removeSlot: function(slot) {
        AppDispatcher.dispatch({
            actionType: RequestConstants.CHALLENGE_SLOTS_REMOVE,
            slot: slot
        });
    }
};

module.exports = ChallengeActions;
