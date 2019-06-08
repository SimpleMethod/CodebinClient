'use strict';
var app = angular.module('mainApp', []);
angular.module("mainApp").requires.push('ngCookies');
angular.module("mainApp").requires.push('ngRoute');


var moduleA = angular.module("dashboard", []);
angular.module("dashboard").requires.push('ngCookies');

moduleA.config(function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'dashboard/home.html',
            controller: 'HomeController'
        })

        .when('/projects', {
            templateUrl: 'dashboard/projects.html',
            controller: 'ProjectsController'
        })

        .when('/containers/:id', {
            templateUrl: 'dashboard/container.html',
            controller: 'ContainersController'
        })

        .when('/create/:id', {
            templateUrl: 'dashboard/containercreate.html',
            controller: 'ContainersCreateController'
        })

        .when('/about', {
            templateUrl: 'dashboard/about.html',
            controller: 'AboutController'
        })

        .when('/profile', {
            templateUrl: 'dashboard/profile.html'
        })

        .when('/payment-accept', {
            templateUrl: 'dashboard/payment-accept.html'
        })

        .when('/payment-error', {
            templateUrl: 'dashboard/payment-error.html'
        })

        .otherwise({
            redirectTo: '/'
        });
});

angular.module("CombineModule", ["mainApp", "dashboard"]);


app.controller('ContainersCreateController', ['$filter', '$routeParams', '$scope', '$http', function ($filter, $routeParams, $scope, $http) {
    $scope.supportedPlatforms = ["Java", "C", "C++", "Python", "HTML", "JavaScript", "CSS"];
    $scope.referId = $routeParams.id;
    $scope.progressBar = 0;


    $http({
        url: 'http://127.0.0.1/github/user/repos/' + $routeParams.id,
        method: 'GET'
    }).then(
        function (response) {
            $scope.name = response.data.name;
            $scope.description = response.data.description;
            $scope.created_at = response.data.created_at;
            $scope.language = response.data.language;
            $scope.id = response.data.id;
            $scope.git_url = response.data.git_url;
            $scope.rammemory = null;
            $scope.diskquota = null;
            if (!response.data.container_create) {
                if ($scope.supportedPlatforms.indexOf(response.data.language) !== -1) {
                    $scope.progressBar = 40;
                    $http({
                        url: 'http://127.0.0.1/github/local',
                        method: 'GET'
                    }).then(
                        function (local) {
                            $scope.progressBar = 70;
                            $scope.userId = local.data.user_id;
                            $scope.subscriptionStatus = local.data.subscription_status;
                            $scope.github_id = local.data.github_id;
                            $scope.premium = null;
                            $scope.dockerImages = null;

                            if ($scope.subscriptionStatus === "") {
                                $scope.premium = 0;
                                console.log("Free user");
                                if ($scope.supportedPlatforms.indexOf(response.data.language) === 0) {
                                    $scope.dockerImages = 3;
                                } else if ($scope.supportedPlatforms.indexOf(response.data.language) === 1 || $scope.supportedPlatforms.indexOf(response.data.language) === 2) {
                                    $scope.dockerImages = 3;
                                } else if ($scope.supportedPlatforms.indexOf(response.data.language) === 3) {
                                    $scope.dockerImages = 3;
                                } else {
                                    $scope.dockerImages = 4;
                                }
                                $scope.rammemory = $scope.diskquota = 1000000000;
                                $scope.premiumtext = "Free";
                            } else {
                                console.log("Pro user");
                                if ($scope.supportedPlatforms.indexOf(response.data.language) === 0) {
                                    $scope.dockerImages = 1;
                                } else if ($scope.supportedPlatforms.indexOf(response.data.language) === 1 || $scope.supportedPlatforms.indexOf(response.data.language) === 2) {
                                    $scope.dockerImages = 1;
                                } else if ($scope.supportedPlatforms.indexOf(response.data.language) === 3) {
                                    $scope.dockerImages = 1;
                                } else {
                                    $scope.dockerImages = 2;
                                }
                                $scope.premium = 1;
                                $scope.premiumtext = "Premium";
                                $scope.rammemory = $scope.diskquota = 2000000000;
                            }

                            var str = "" + $scope.id;
                            var res = str.substring(0, 3);
                            $scope.exposedports = 8080;
                            $scope.hostports = (1 + res);
                            $scope.dockerName = $scope.id;
                            $scope.progressBar = 90;
                            console.log("Status konta:" + $scope.premium);
                            console.log("Obraz:" + $scope.dockerImages);
                            console.log("Port wewnętrzny:" + $scope.exposedports);
                            console.log("Nazwa dokeru:" + $scope.id);
                            console.log("Ilość pamięci ram: " + $scope.rammemory + " i dysku: " + $scope.diskquota);
                            $http({
                                url: 'http://127.0.0.1/srv/container/create',
                                method: 'POST',
                                params: {
                                    dockerimage: $scope.dockerImages,
                                    exposedports: $scope.exposedports,
                                    hostports: $scope.hostports,
                                    name: $scope.id,
                                    rammemory: $scope.rammemory,
                                    diskquota: $scope.diskquota,
                                    giturl: $scope.git_url,
                                    premiumstatus: $scope.premium
                                }
                            }).then(
                                function (create) {
                                    console.log(create.data);
                                    $scope.progressBar = 100;
                                    $scope.finish = 1;
                                },
                                function (create) {
                                    $scope.errors = "Problem with creating a container";
                                    $scope.erorType = 0;
                                    console.error(create.data);
                                }
                            );

                        },
                        function () {
                            $scope.errors = "Problem with loading user data";
                            $scope.erorType = 1;
                        }
                    );
                } else {
                    $scope.errors = "No support for the specified repository";
                    $scope.erorType = 2;

                }
            } else {
                $scope.errors = "The repository does exist";
                $scope.erorType = 3;
                console.error("The repository does exist");
            }

        },
        function () {
            $scope.errors = "Fatal Error";
            $scope.erorType = 4;
            window.location = "/";
        }
    );
}]);


app.controller('ContainersController', ['$filter', '$routeParams', '$scope', '$http', function ($filter, $routeParams, $scope, $http, $window) {
    $http({
        url: 'http://127.0.0.1/srv/user/container/info',
        method: 'GET',
        params: {dockergithubid: $routeParams.id}
    }).then(
        function (response) {
            $scope.status = response.data.status;
            $scope.creationTime = response.data.createTime;
            $scope.containersId = response.data.idDocker;
            $scope.hostPorts = response.data.hostPorts;
            if ($scope.status === 1) {
                $scope.status = "Premium";
                $scope.shareStatus = response.data.shareUrl;
            } else {
                $scope.status = "Free";
                $scope.shareStatus = null;

            }
            $scope.dockerRestart = function () {
                $http({
                    url: 'http://127.0.0.1/srv/container/' + $scope.containersId + '/restart',
                    method: 'POST'
                }).then(
                    function (response) {
                    },
                    function () {
                        $scope.errors = "Problem with restarting a container";
                    }
                );
            };
            $scope.dockerRemove = function () {
                $http({
                    url: 'http://127.0.0.1/srv/container/' + $scope.containersId + '/delete',
                    method: 'DELETE'
                }).then(
                    function () {
                        window.location = "/";
                    },
                    function () {
                        $scope.errors = "Problem with deleteing a container";
                    }
                );
            };


            $scope.execCommands = function (path, args) {
                $http({
                    url: 'http://127.0.0.1/srv/container/' + $scope.containersId + '/exec',
                    method: 'POST',
                    params: {
                        path: path,
                        argument: args
                    }
                }).then(
                    function () {
                    },
                    function () {
                    }
                );
            };

            $http({
                url: 'http://127.0.0.1/srv/container/' + $scope.containersId + '/logs',
                method: 'GET'
            }).then(
                function (logGet) {
                    $scope.logMachine = logGet.data.logs;
                },
                function () {
                    $scope.errors = "Problem with downloading logs from containers";
                }
            );
            $http({
                url: 'http://127.0.0.1/srv/container/' + $scope.containersId + '/top',
                method: 'GET'
            }).then(
                function (response) {
                    $scope.tops = response.data.Processes;
                },
                function () {
                    $scope.errors = "Problem with getting processes from a container";
                }
            );
        },
        function () {
            $scope.errors = "Fatal error";
            window.location = "/";
        }
    );
}]);


app.controller('HomeController', function ($scope, $http, $cookies) {
    $scope.lastVal = $cookies.get('token');
    $http({
        url: 'http://127.0.0.1/github/user',
        method: 'GET'
    }).then(
        function (response) {
            $scope.passCheck = response;
            $scope.privateRepo = response.data.total_private_repos;
            $scope.publicRepo = response.data.public_repos;
            $scope.numbersColl = response.data.collaborators;
            $scope.followers = response.data.followers;
            $scope.following = response.data.following;
        },
        function () {
            $scope.passCheck = false;
        }
    );
});

app.controller('ProjectsController', function ($scope, $http) {
    $scope.supportedPlatforms = ["Java", "HTML", "C", "C++", "JavaScript", "CSS"];

    $http({
        url: 'http://127.0.0.1/github/user/repos/public',
        method: 'GET'
    }).then(
        function (response) {
            $scope.jsondata = response.data;
            $scope.repoid = response.data.id;
            $scope.progressBar = 100;
        },
        function () {
            $scope.errors = "Fatal error";
        }
    );
});

app.controller('VersionController', function ($scope) {
    $scope.years = "2019";
    $scope.mm = "Michał Młodawski";
    $scope.pp = "Piotr Piasecki";
    $scope.tb = "Tobiasz Nartowski";
    $scope.k0 = "Kacper Obara";
});

app.controller('CheckLoginStatus', function ($scope, $http, $cookies) {
    $scope.lastVal = $cookies.get('token');
    $http({
        url: 'http://127.0.0.1/github/user/checktoken',
        method: 'GET',
        params: {token: $scope.lastVal}
    }).then(
        function () {
            $scope.passCheck = true;
        },
        function () {
            $scope.passCheck = false;
        }
    );
});


app.controller('dashboardGithub', function ($scope, $http, $cookies) {
    $scope.lastVal = $cookies.get('token');
    $http({
        url: 'http://127.0.0.1/github/user',
        method: 'GET'
    }).then(
        function (response) {
            $scope.passCheck = response;
            $scope.avatar = response.data.avatar_url;
            $scope.name = response.data.name;
        },
        function () {
            window.location = "/";
        }
    );

    $scope.logout = function () {
        $cookies.remove('token');
        $cookies.remove('id');
        window.location = "/";
    };
});

$('ul.nav > li > a.nav-link').click(function (e) {
    $('ul.nav > li  > a.nav-link').removeClass('active');
    $(this).addClass('active');
});
