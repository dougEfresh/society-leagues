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