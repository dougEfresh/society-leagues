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

    addSlots : function(slots) {
        //TODO Optimize
        slots.forEach(function(newSlot) {
            var found = false;
            _challenge.slots.forEach(function(s) {
                if (s.id == newSlot.id) {
                    found = true;
                }
            });
            if (!found) {
                _challenge.slots.push(newSlot);
            }
        });
    },
    removeSlot : function(slot) {
        var newSlots = [];
        _challenge.slots.forEach(function(s){
            if (s.id != slot.id) {
                newSlots.push(s);
            }
        });
        _challenge.slots = newSlots;
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

         case ChallengeConstants.CHALLENGE_SLOTS_ADD:
             ChallengeStore.addSlots(action.slots);
             ChallengeStore.emitChange();
             break;

         case ChallengeConstants.CHALLENGE_SLOTS_REMOVE:
             ChallengeStore.removeSlot(action.slot);
             ChallengeStore.emitChange();
             break;

         default:
     }
});

module.exports = ChallengeStore;