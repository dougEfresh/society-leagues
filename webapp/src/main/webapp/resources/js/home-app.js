var app = angular.module('leagueApp',
    ['editableTableWidgets', 'frontendServices', 'spring-security-csrf-token-interceptor'])
    .controller('HomeCtrl',['$scope' , 'UserService', '$timeout', '$filter',
    function ($scope, UserService, $timeout, $filter) {

        $scope.leaderCurrentPage = 0;
        $scope.allResultsCurrentPage = 0;
        $scope.resultsCurrentPage = 0;
        $scope.pageSize = 10;

        $scope.numberOfPages=function(){
            return Math.ceil($scope.vm.leaderBoard.length/$scope.pageSize);
        };

        $scope.allResultsNumberOfPages=function(){
            return Math.ceil($scope.vm.allResults.length/$scope.pageSize);
        };

        $scope.resultsNumberOfPages=function(){
            return Math.ceil($scope.vm.results.length/$scope.pageSize);
        };

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
            if ( match === null || match === undefined || match.teamMatch === undefined)
                return "Unknown";

            return match.teamMatch.matchDate;
        }

        function _getChallengeDate(c) {
            return c.challengeDate;
        }

        function _getName(user) {
            if (user === null || user === undefined || user.firstName == undefined)
                return "Unknown";

            return user.firstName ;
        }

        function _getPlayer(player) {
            if (player === null) {
                return undefined;
            }
            if (typeof(player) === "object") {
                return  $scope.vm.playerMap[player.id];
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
            if (p === null || p === undefined)
                return false;

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
            if (teamMatch === null || teamMatch === undefined ||  teamMatch.division === undefined) {
                return "Unknown";
            }
            return teamMatch.division.type.replace("_CHALLENGE","").replace("NINE","9").replace("EIGHT","8").replace("_"," ");
        }

        function _isCurrentLoginPlayer(p) {
            var player = _getPlayer(p);
            if (player === null || player === undefined)
                return false;

            return _.filter($scope.vm.players, function(pl) {return pl.id == p.id;}).length > 0;
        }

        function _getWinner(match) {
            var p  = _getPlayer(match.playerHome);
            if (match.homeRacks > match.awayRacks) {
                return p;
            }

            return _getPlayer(match.playerAway);

        }

        function _getNameByChallenge(c,home) {
            if (c === null || c === undefined) {
                return "Unknown";
            }
            var u;
            if (home) {
                u=_getPlayer(c.teamMatch.playersHome[0]).user;
            } else {
                u=_getPlayer(c.teamMatch.playersAway[0]).user;
            }
            return _getName(u);
        }

        function _getNameByMatch(m,home) {
            if (m === null || m === undefined) {
                return "Unknown";
            }
            var u,p;

            if (home) {
                p = _getPlayer(m.playerHome);
                if (p === undefined || p === null){
                    return "Unknown";
                }
                return  _getName(p.user);
            } else {
                p = _getPlayer(m.playerAway);
                if (p === undefined || p === null)
                    return "Unknown";

                return _getName(_getPlayer(m.playerAway).user);
            }
        }


        $scope.vm = {
            request: {},
            potentials: [ {id: 1 , name: "player1"}, { id: 2, name: "player2"} ],
            potentialUsers: [],
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
            user: {},
            getOpponent:  _getOpponent,
            isWin:  _isWin,
            getRacks:  _getRacks,
            getOpponentRacks:  _getOpponentRacks,
            getDivision: _getDivision,
            getWinner: _getWinner,
            getName: _getName,
            getChallengeDate: _getChallengeDate,
            getMatchDate: _getMatchDate,
            getNameByChallenge: _getNameByChallenge,
            getNameByMatch: _getNameByMatch
        };

        function updatePotentials() {
            UserService.getPotentials().then (function (users) {
                console.log("Processing " + users.length);
                $scope.vm.potentialUsers = users;
            }, function (errorMessage) {
                showErrorMessage(errorMessage);
            });
        }

        function updatePlayers() {
            UserService.getPlayers().then (function (players) {
                console.log("Processing " + players.length);
                var filtered = filterPlayers(players);
                _.map(filtered,function(p){ addToMap(p);})
                window.players  =  $scope.vm.playerMap;
            }, function (errorMessage) {
                showErrorMessage(errorMessage);
            });
        }

        function updateChallengeInfo() {
            UserService.getChallenges().then (function (challenges) {
                $scope.vm.pendingChallenges = _.filter(challenges, function(c) {return c.status === 'PENDING'});
                $scope.vm.acceptedChallenges = _.filter(challenges, function(c) {return c.status === 'ACCEPTED'});
                window.challenges =     $scope.vm.pendingChallenges;
            }, function (errorMessage) {
                showErrorMessage(errorMessage);
            });
        }

        function updatePlayerInfo() {
            UserService.getPlayerInfo().then (function (playerInfo) {
                $scope.vm.players = filterPlayers(playerInfo);
            }, function (errorMessage) {
                showErrorMessage(errorMessage);
            });
        }

        function updatePlayerResults() {
            UserService.getPlayerResults().then (function (playerResults) {
                console.log("Processing Results: "  + playerResults.length);
                window.results = playerResults;
                $scope.vm.allResults = playerResults;
                $scope.orderAllResults('teamMatch.matchDate',true);
                $scope.vm.results = _.filter(playerResults, function(r) {
                    return _isCurrentLoginPlayer(r.playerHome) || _isCurrentLoginPlayer(r.playerAway);
                });
                console.log("User has " +  $scope.vm.results.length);
                $scope.orderPlayerResults('teamMatch.matchDate',true);
                $scope.vm.stats = updateStats($scope.vm.results);
                $scope.vm.leaderBoard =  updateLeaderBoard($scope.vm.allResults);
                $scope.orderLeaders('points',true);

            }, function (errorMessage) {
                showErrorMessage(errorMessage);
                markAppAsInitialized();
            });
        }

        function updateUserInfo() {
            UserService.getUserInfo()
                .then(function (userInfo) {
                    $scope.vm.user = userInfo;
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
                var h = _getPlayer(m.playerHome);
                var a = _getPlayer(m.playerAway);
                if (h === null || h === undefined || a === null || a === undefined) {
                    return;
                }

                if (stats[h.user.id] === undefined) {
                    stats[h.user.id] = h.user;
                    stats[h.user.id].points = 0;
                }

                if (stats[a.user.id] === undefined) {
                    stats[a.user.id] = a.user;
                    stats[a.user.id].points = 0;
                }

                if (m.homeRacks > m.awayRacks) {
                    stats[h.user.id].points += 2;
                    stats[a.user.id].points += 1;
                } else {
                    stats[a.user.id].points += 2;
                    stats[h.user.id].points += 1;
                }
            });
            var statsArray = [];
            _.map(stats , function(s) {statsArray.push(s)});
            return statsArray;
        }

        function addToMap(p) {
            if (p !== null && typeof(p) === "object" && $scope.vm.playerMap[p.id] === undefined ) {
                //console.log("Adding player " + p.id + " to map");
                $scope.vm.playerMap[p.id] = p;
            }
        }

        function filterPlayers(players) {
            return _.filter(players,function (p) {
                return p.division.challenge
            });
        }

        $scope.sendRequest = function() {
            console.log($scope.vm.victum);
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
            updatePlayers();
            updateUserInfo();
            updatePlayerInfo();
            updatePlayerResults();
            updateChallengeInfo();
            updatePotentials();
        }

        updateInfo();
        markAppAsInitialized();
    }]);


//We already have a limitTo filter built-in to angular,
//let's make a startFrom filter
app.filter('startFrom', function() {
    return function(input, start) {
        start = +start; //parse to int
        return input.slice(start);
    }
});