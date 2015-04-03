var AppDispatcher = require('../dispatcher/AppDispatcher.jsx');
var EventEmitter = require('events').EventEmitter;
var ChallengeConstants = require('../constants/ChallengeConstants.jsx');
var assign = require('object-assign');
var Util = require('../util.jsx');

var CHANGE_EVENT = 'change';

var _challenge = {
    date: Util.nextChallengeDate(),
    opponent: null,
    slots: [],
    game: {nine: false, eight: false}
};

var ChallengeStore =  assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    /**
     * @param {function} callback
     */
    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    /**
     * @param {function} callback
     */
    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    changeDate : function(date) {
        _challenge.date = date;
    },

    get: function() {
        return _challenge;
    }

});

AppDispatcher.register(function(action) {
     switch(action.actionType) {
         case ChallengeConstants.CHALLENGE_DATE_CHANGE:
             ChallengeStore.changeDate(action.date);
             ChallengeStore.emitChange();
             break;

         default:
     }
});

module.exports = ChallengeStore;