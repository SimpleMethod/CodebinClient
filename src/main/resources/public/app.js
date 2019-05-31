'use strict';
var app = angular.module('mainApp', []);
angular.module("mainApp").requires.push('ngCookies');


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
        url: 'http://127.0.0.1/github/checktoken',
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