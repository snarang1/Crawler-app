'use strict'
angular.module('demo.services', []).factory('MyService', ["$http", function($http) {
    var service={};
    
    service.getInfo = function(url) {
        return $http.post("/getInfo", url);
    }
    return service;
}]);