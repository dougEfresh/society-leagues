angular.module('frontendServices', [])
    .service('UserService', ['$http','$q', function($http, $q) {
        return {
            getUserInfo: function() {
                var deferred = $q.defer();

                $http.get('/user')
                    .then(function (response) {
                        if (response.status == 200) {
                            deferred.resolve(response.data);
                        }
                        else {
                            deferred.reject('Error retrieving user info');
                        }
                });

                return deferred.promise;
            },

            getPlayerInfo: function() {
                var deferred = $q.defer();

                $http.get('/userPlayers')
                    .then(function (response) {
                        if (response.status == 200) {
                            deferred.resolve(response.data);
                        }
                        else {
                            deferred.reject('Error retrieving user info');
                        }
                    });

                return deferred.promise;
            },

            getPlayers: function() {
                var deferred = $q.defer();

                $http.get('/players')
                    .then(function (response) {
                        if (response.status == 200) {
                            deferred.resolve(response.data);
                        }
                        else {
                            deferred.reject('Error retrieving user info');
                        }
                    });

                return deferred.promise;
            },

            getPlayerResults: function() {
                var deferred = $q.defer();

                $http.get('/results')
                    .then(function (response) {
                        if (response.status == 200) {
                            deferred.resolve(response.data);
                        }
                        else {
                            deferred.reject('Error retrieving user info');
                        }
                    });

                return deferred.promise;
            },

            getChallenges: function() {
                var deferred = $q.defer();

                $http.get('/challenges')
                    .then(function (response) {
                        if (response.status == 200) {
                            deferred.resolve(response.data);
                        }
                        else {
                            deferred.reject('Error retrieving user info');
                        }
                    });

                return deferred.promise;
            },

            getPotentials: function() {
                var deferred = $q.defer();

                $http.get('/potentials')
                    .then(function (response) {
                        if (response.status == 200) {
                            deferred.resolve(response.data);
                        }
                        else {
                            deferred.reject('Error retrieving user info');
                        }
                    });

                return deferred.promise;
            },

            sendRequestChallenge: function(challenger,opponent) {
                var challenge = {};


            },


            getLeaderBoard: function() {
                 var deferred = $q.defer();

                $http.get('/leaderboard')
                    .then(function (response) {
                        if (response.status == 200) {
                            deferred.resolve(response.data);
                        }
                        else {
                            deferred.reject('Error retrieving user info');
                        }
                });

                return deferred.promise;
        },

            logout: function () {
                $http({
                    method: 'POST',
                    url: '/logout'
                })
                    .then(function (response) {
                        if (response.status == 200) {
                            window.location.reload();
                        }
                        else {
                            console.log("Logout failed!");
                        }
                    });
            }
        };
    }]);