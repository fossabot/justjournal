angular.module('wwwApp').controller('MainCtrl', ['$scope', '$window', '$http', function ($scope, $window, $http) {
    'use strict';

    $scope.username = '';
    $scope.password = '';

    $scope.AccountLogin = function() {
        var data = { username: $scope.username, password: $scope.password };
        $http.post('api/login', data).success(function(data, status) {
              if (status == 200 && data.status == 'JJ.LOGIN.OK') {
                  window.location.href = '/users/' + data.username;
                  return;
              }

              alert(data.error);
        });
    };
}]);
