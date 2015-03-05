angular.module('leagueApp',
    ['editableTableWidgets', 'frontendServices', 'spring-security-csrf-token-interceptor'])
    .controller('HomeCtrl',['$scope' , 'UserService', '$timeout', '$filter',
    function ($scope, UserService, $timeout, $filter) {
        var orderBy = $filter('orderBy');

        $scope.orderLeaders = function(predicate, reverse) {
             $scope.vm.leaderBoard = orderBy($scope.vm.leaderBoard, predicate, reverse);
        };

        $scope.orderPlayerResults = function(predicate, reverse) {
            $scope.vm.results = orderBy($scope.vm.results, predicate, reverse);
        };

        $scope.orderAllResults = function(predicate, reverse) {
            $scope.vm.allResults = orderBy($scope.vm.allResults, predicate, reverse);
        };

        function _getMatchDate(match) {
            return match.teamMatch.matchDate;
        }

        function _getChallengeDate(c) {
            return c.challengeDate;
        }

        function _getName(user) {
            return user.firstName ;
        }

        function _getPlayer(player) {
            if (player === null) {
                return undefined;
            }
            if (typeof(player) === "object") {
                return player;
            }
            return $scope.vm.playerMap[player];
        }

        function _getOpponent(match) {
            var p = _getPlayer(match.playerHome);
            if ($scope.vm.players[p.id] === undefined)
                return p;

            return _getPlayer(match.playerAway);

        }

        function _isWin(match) {
            if (match === null) {
                return false;
            }
            var p  = _getPlayer(match.playerHome);
            //Logged in user is away for this match
            if ($scope.vm.players[p.id] === undefined) {
                return match.homeRacks < match.awayRacks;
            }
            return match.homeRacks > match.awayRacks;
        }

        function _getRacks(match) {
            if (match === null) {
                return false;
            }
            var p  = _getPlayer(match.playerHome);
            //Logged in user is away for this match
            if ($scope.vm.players[p.id] === undefined) {
                return  match.awayRacks;
            }
            return match.homeRacks ;
        }

        function _getOpponentRacks(match) {
            if (match === null) {
                return false;
            }
            var p  = _getPlayer(match.playerHome);
            //Logged in user is away for this match
            if ($scope.vm.players[p.id] === undefined) {
                return  match.homeRacks;
            }
            return match.awayRacks;
        }

        function _getDivision(teamMatch) {
            return teamMatch.division.type.replace("_CHALLENGE","").replace("NINE","9").replace("EIGHT","8").replace("_"," ");
        }

        function _isSessionPlayer(p) {
            if (p === null || typeof(p) !== "object")
                return false;

              return  $scope.vm.players[p.id] !== undefined;
        }

        function _getWinner(match) {
            var p  = _getPlayer(match.playerHome);
            if (match.homeRacks > match.awayRacks) {
                return p;
            }

            return _getPlayer(match.playerAway);

        }

        $scope.vm = {
            currentPage: 1,
            totalPages: 0,
            isSelectionEmpty: true,
            errorMessages: [],
            infoMessages: [],
            results: [],
            allResults: [],
            pendingChallenges: [],
            acceptedChallenges: [],
            players: {},
            playerMap: {},
            stats: {},
            leaderBoard: [],
            getOpponent:  _getOpponent,
            isWin:  _isWin,
            getRacks:  _getRacks,
            getOpponentRacks:  _getOpponentRacks,
            getDivision: _getDivision,
            getWinner: _getWinner,
            getName: _getName,
            getChallengeDate: _getChallengeDate,
            getMatchDate: _getMatchDate
        };

        function updateChallengeInfo() {
            UserService.getChallenges().then (function (challenges) {
                $scope.vm.pendingChallenges = _.filter(challenges, function(c) {return c.status === 'PENDING'});
                $scope.vm.acceptedChallenges = _.filter(challenges, function(c) {return c.status === 'ACCEPTED'});

            }, function (errorMessage) {
                showErrorMessage(errorMessage);
                markAppAsInitialized();
            });
        }

        function updatePlayerInfo() {
            UserService.getPlayerInfo().then (function (playerInfo) {
                $scope.vm.players = filterPlayers(playerInfo);

            }, function (errorMessage) {
                showErrorMessage(errorMessage);
                markAppAsInitialized();
            });

        }

        function updatePlayerResults() {
            UserService.getPlayerResults().then (function (playerResults) {

                $scope.vm.allResults = playerResults;
                window.allResults = playerResults;
                _.forEach(playerResults, function(p) {
                    addToMap(p.playerHome);
                    addToMap(p.playerAway);
                });
                $scope.orderAllResults('teamMatch.matchDate',true);
                $scope.vm.results = _.filter(playerResults, function(r) {
                    return _isSessionPlayer(r.playerHome) || _isSessionPlayer(r.playerAway);
                });
                $scope.orderPlayerResults('teamMatch.matchDate',true);
                $scope.vm.stats = updateStats($scope.vm.results);
                $scope.vm.leaderBoard =  updateLeaderBoard($scope.vm.allResults);
                $scope.orderLeaders('points',true);
                window.leaderBoard =  $scope.vm.leaderBoard;

            }, function (errorMessage) {
                showErrorMessage(errorMessage);
                markAppAsInitialized();
            });
        }

        function updateUserInfo() {
            UserService.getUserInfo()
                .then(function (userInfo) {
                    $scope.vm.userName = userInfo.firstName;
                },
                function (errorMessage) {
                    showErrorMessage(errorMessage);
                    markAppAsInitialized();
                });
        }

        function updateStats(matches) {
            var stats = {};
            stats.wins = 0;
            stats.matches = 0;
            stats.loses = 0;
            stats.racks = 0;
            stats.percentage = 0;

            _.map(matches,function(m){
                if (m.result === null)
                    return;
                stats.matches++;

                if (_isWin(m))
                    stats.wins++;
                else
                    stats.loses++;

                stats.racks += _getRacks(m);

            });

            if (stats.matches >0) {
                stats.percentage = stats.loses == 0 ? 1 : Math.round((stats.loses/ stats.matches)*100) ;
            }

            return stats;
        }

        function updateLeaderBoard(matches) {
            var stats = {};
            _.map(matches,function(m) {
                if (stats[m.playerHome.user.id] === undefined) {
                    stats[m.playerHome.user.id] = m.playerHome.user;
                    stats[m.playerHome.user.id].points = 0;
                }

                if (stats[m.playerAway.user.id] === undefined) {
                    stats[m.playerAway.user.id] = m.playerAway.user;
                    stats[m.playerAway.user.id].points = 0;
                }

                if (m.homeRacks > m.awayRacks) {
                    stats[m.playerHome.user.id].points += 2;
                    stats[m.playerAway.user.id].points += 1;
                } else {
                    stats[m.playerAway.user.id].points += 2;
                    stats[m.playerHome.user.id].points += 1;
                }
            });
            var statsArray = [];
            _.map(stats , function(s) {statsArray = statsArray.concat(s)});
            return statsArray;
        }

        function addToMap(p) {
            if (p !== null && typeof(p) === "object" && $scope.vm.playerMap[p.id] === undefined ) {
                //console.log("Adding player " + p.id + " to map");
                $scope.vm.playerMap[p.id] = p;
            }
        }

        function filterPlayers(players) {
            var challengePlayers =  _.filter(players,function (p) {
                return p.division.challenge
            });

            var players  = {};
            _.map(challengePlayers, function(p) {
                addToMap(p);
                players[p.id] = p;
            });
            return players;
        }

        function showErrorMessage(errorMessage) {
            clearMessages();
            $scope.vm.errorMessages.push({description: errorMessage});
        }

        function markAppAsInitialized() {
            if ($scope.vm.appReady == undefined) {
                $scope.vm.appReady = true;
            }
        }

        function clearMessages() {
            $scope.vm.errorMessages = [];
            $scope.vm.infoMessages = [];
        }

        function showInfoMessage(infoMessage) {
            $scope.vm.infoMessages = [];
            $scope.vm.infoMessages.push({description: infoMessage});
            $timeout(function () {
                $scope.vm.infoMessages = [];
            }, 1000);
        }

        $scope.selectionChanged = function () {
            $scope.vm.isSelectionEmpty = !_.any($scope.vm.meals, function (meal) {
                return meal.selected && !meal.deleted;
            });
        };

        $scope.pages = function () {
            return _.range(1, $scope.vm.totalPages + 1);
        };

        $scope.search = function (page) {

            var fromDate = new Date($scope.vm.fromDate);
            var toDate = new Date($scope.vm.toDate);

            console.log('search from ' + $scope.vm.fromDate + ' ' + $scope.vm.fromTime + ' to ' + $scope.vm.toDate + ' ' + $scope.vm.toTime);

            var errorsFound = false;

            if ($scope.vm.fromDate && !$scope.vm.toDate || !$scope.vm.fromDate && $scope.vm.toDate) {
                showErrorMessage("Both from and to dates are needed");
                errorsFound = true;
                return;
            }

            if (fromDate > toDate) {
                showErrorMessage("From date cannot be larger than to date");
                errorsFound = true;
            }

            if (fromDate.getTime() == toDate.getTime() && $scope.vm.fromTime &&
                $scope.vm.toTime && $scope.vm.fromTime > $scope.vm.toTime) {
                showErrorMessage("Inside same day, from time cannot be larger than to time");
                errorsFound = true;
            }

            if (!errorsFound) {
                loadMealData($scope.vm.fromDate, $scope.vm.fromTime, $scope.vm.toDate, $scope.vm.toTime, page == undefined ? 1 : page);
            }

        };

        $scope.previous = function () {
            if ($scope.vm.currentPage > 1) {
                $scope.vm.currentPage-= 1;
                loadMealData($scope.vm.fromDate, $scope.vm.fromTime,
                    $scope.vm.toDate, $scope.vm.toTime, $scope.vm.currentPage);
            }
        };

        $scope.next = function () {
            if ($scope.vm.currentPage < $scope.vm.totalPages) {
                $scope.vm.currentPage += 1;
                loadMealData($scope.vm.fromDate, $scope.vm.fromTime,
                    $scope.vm.toDate, $scope.vm.toTime, $scope.vm.currentPage);
            }
        };

        $scope.goToPage = function (pageNumber) {
            if (pageNumber > 0 && pageNumber <= $scope.vm.totalPages) {
                $scope.vm.currentPage = pageNumber;
                loadMealData($scope.vm.fromDate, $scope.vm.fromTime, $scope.vm.toDate, $scope.vm.toTime, pageNumber);
            }
        };

        $scope.add = function () {
            $scope.vm.meals.unshift({
                id: null,
                datetime: null,
                description: null,
                calories: null,
                selected: false,
                new: true
            });
        };

        $scope.delete = function () {
            var deletedMealIds = _.chain($scope.vm.meals)
                .filter(function (meal) {
                    return meal.selected && !meal.new;
                })
                .map(function (meal) {
                    return meal.id;
                })
                .value();

            MealService.deleteMeals(deletedMealIds)
                .then(function () {
                    clearMessages();
                    showInfoMessage("deletion successful.");

                    _.remove($scope.vm.meals, function(meal) {
                        return meal.selected;
                    });

                    $scope.selectionChanged();
                    updateUserInfo();

                },
                function () {
                    clearMessages();
                    $scope.vm.errorMessages.push({description: "deletion failed."});
                });
        };

        $scope.logout = function () {
            UserService.logout();
        }

        function updateInfo() {
            updateUserInfo();
            updatePlayerInfo();
            updatePlayerResults();
            updateChallengeInfo();
        }

        updateInfo();
        markAppAsInitialized();
    }]);

