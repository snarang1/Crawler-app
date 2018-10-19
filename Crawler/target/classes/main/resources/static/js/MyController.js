
'use strict'
var module = angular.module('demo.controllers', []);
module.controller("MyController", ["$scope", "MyService",
    function($scope, MyService) {
        $scope.crawlerDto = {
            url: null,
            depth: null
        };
        $scope.getResults = function() {
        MyService.getInfo($scope.url).then(function(value) {
                console.log("works");           
                $scope.result=value.data;
            }, function(reason) {
                console.log("error occured");
            }, function(value) {
                console.log("no callback");
            });
        }
    }
]);