'use strict';
var app = angular.module('mainApp', []);
angular.module("mainApp").requires.push('ngCookies');
angular.module("mainApp").requires.push('ngRoute');


var moduleA = angular.module("dashboard", []);
angular.module("dashboard").requires.push('ngCookies');
moduleA.controller("twojstary", function($scope) {
    $scope.name = "Bob A";
});

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

        .when('/containers', {
            templateUrl: 'dashboard/container.html',
            controller: 'ContainersController'
        })

        .when('/about', {
            templateUrl: 'dashboard/about.html',
            controller: 'AboutController'
        })

        .otherwise({
            redirectTo: '/'
        });
});

angular.module("CombineModule", ["mainApp", "dashboard"]);

app.controller('HomeController', function ($scope, $http, $cookies) {

    $scope.lastVal = $cookies.get('token');
    $http({
        url: 'http://127.0.0.1/github/user',
        method: 'GET',
        params: {token:  $scope.lastVal}
    }).then(
        function (response){
            $scope.passCheck = response;
            $scope.privateRepo = response.data.total_private_repos;
            $scope.publicRepo = response.data.public_repos;
            $scope.numbersColl = response.data.collaborators;
            $scope.followers = response.data.followers;
            $scope.following= response.data.following;
            console.log($scope.passCheck);

        },
        function (response){
            $scope.passCheck = false;
        }
    );
});

app.controller('ProjectsController', function ($scope, $http, $cookies) {
    $scope.upportedPlatforms=["Java", "HTML", "C", "C++", "JavaScript", "CSS"];
    $scope.lastVal = $cookies.get('token');
    $http({
        url: 'http://127.0.0.1/github/user/repos/public',
        method: 'GET'
        //params: {token:  $scope.lastVal}
    }).then(
        function (response){
            $scope.jsondata = response.data;
            console.info("Return"+ $scope.jsondata);

        },
        function (response){
            $scope.passCheck = false;
            console.error("Error: "+ $scope.passCheck);
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
       params: {token:  $scope.lastVal}
    }).then(
        function (response){
            $scope.passCheck = true;
            console.log($scope.lastVal);

        },
        function (response){
            $scope.passCheck = false;
        }
    );
});


app.controller('dashboardGithub', function ($scope, $http, $cookies) {
    $scope.lastVal = $cookies.get('token');
    $http({
        url: 'http://127.0.0.1/github/user',
        method: 'GET'
      //  params: {token:  $scope.lastVal}
    }).then(
        function (response){
            $scope.passCheck = response;
            $scope.avatar = response.data.avatar_url;
            $scope.name = response.data.name;
            console.log($scope.passCheck);

        },
        function (response){
            $scope.passCheck = false;
        }
    );
});

$('ul.nav > li > a.nav-link').click(function (e) {
    $('ul.nav > li  > a.nav-link').removeClass('active');
    $(this).addClass('active');
});
