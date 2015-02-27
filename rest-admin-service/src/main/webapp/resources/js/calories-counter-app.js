angular.module('caloriesCounterApp', ['editableTableWidgets', 'frontendServices', 'spring-security-csrf-token-interceptor'])
    .filter('excludeDeleted', function () {
        return function (input) {
            return _.filter(input, function (item) {
                return item.deleted == undefined || !item.deleted;
            });
        }
    })
    .controller('CaloriesTrackerCtrl',
    ['$scope' , 'MealService', 'UserService', '$timeout',
        function ($scope, MealService, UserService, $timeout) {

            $scope.vm = {
                currentPage: 1,
                totalPages: 0,
                isSelectionEmpty: true,
                errorMessages: [],
                infoMessages: [],
                matches: [],
                pendingChallenges: [],
                acceptedChallenges: [],
                players: []
            };

            function filterPlayers(players) {
                return _.filter(players,function (p) {
                    return p.division.challenge
                });
            }

            function getMatches(players) {
                var m = [];
                _.forEach(players, function (p) {m = m.concat(p.teamMatches)});
                return m;
            }

            function getPendingChallenges(players) {
                return _.chain(players).map(function(p) {
                    return _.chain(p.challenges).filter(function (c) {c.status == 'PENDING'} )
                });
            }

            function getAcceptedChallenges(players) {
                return _.chain(players).map(function(p) {
                    return _.chain(p.challenges).filter(function (c) {c.status == 'ACCEPTED'} )
                });
            }

            updateUserInfo();

            function showErrorMessage(errorMessage) {
                clearMessages();
                $scope.vm.errorMessages.push({description: errorMessage});
            }

            function updateUserInfo() {
                UserService.getUserInfo()
                    .then(function (userInfo) {
                        //console.log(JSON.stringify(userInfo));
                        window.userInfo = userInfo;
                        $scope.vm.userName = userInfo.firstName;
                        $scope.vm.players = filterPlayers(userInfo.players);
                        $scope.vm.matches = getMatches($scope.vm.players);
                        console.log(JSON.stringify($scope.vm.matches));
                        $scope.vm.pendingChallenges = getPendingChallenges($scope.vm.players);
                        $scope.vm.acceptedChallenges = getAcceptedChallenges($scope.vm.players);
                    },
                    function (errorMessage) {
                        showErrorMessage(errorMessage);
                        markAppAsInitialized();
                    });
            }

            function markAppAsInitialized() {
                if ($scope.vm.appReady == undefined) {
                    $scope.vm.appReady = true;
                }
            }

            function loadMealData(fromDate, fromTime, toDate, toTime, pageNumber) {
                MealService.searchMeals(fromDate, fromTime, toDate, toTime, pageNumber)
                    .then(function (data) {

                        $scope.vm.errorMessages = [];
                        $scope.vm.currentPage = data.currentPage;
                        $scope.vm.totalPages = data.totalPages;

                        $scope.vm.originalMeals = _.map(data.meals, function (meal) {
                            meal.datetime = meal.date + ' ' + meal.time;
                            return meal;
                        });

                        $scope.vm.meals = _.cloneDeep($scope.vm.originalMeals);

                        _.each($scope.vm.meals, function (meal) {
                            meal.selected = false;
                        });

                        markAppAsInitialized();

                        if ($scope.vm.meals && $scope.vm.meals.length == 0) {
                            showInfoMessage("No results found.");
                        }
                    },
                    function (errorMessage) {
                        showErrorMessage(errorMessage);
                        markAppAsInitialized();
                    });
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

            $scope.updateMaxCaloriesPerDay = function () {
                $timeout(function () {

                    if ($scope.vm.maxCaloriesPerDay < 0) {
                        return;
                    }

                    UserService.updateMaxCaloriesPerDay($scope.vm.maxCaloriesPerDay)
                        .then(function () {
                        },
                        function (errorMessage) {
                            showErrorMessage(errorMessage);
                        });
                    updateCaloriesCounterStatus();
                });
            };

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

            $scope.reset = function () {
                $scope.vm.meals = $scope.vm.originalMeals;
            };

            $scope.logout = function () {
                UserService.logout();
            }

            markAppAsInitialized();
        }]);

