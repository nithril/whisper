'use strict';
/* Controllers */

phonecatApp.controller('LoginCtrl', function ($scope, $http, $location, userService) {

    $scope.generate = function () {
        $scope.key = forge.pki.rsa.generateKeyPair({bits: 2048});
        $scope.publicKey = forge.pki.publicKeyToPem($scope.key.privateKey);
        $scope.privateKey = forge.pki.privateKeyToPem($scope.key.privateKey);
    }

    $scope.login = function () {

        $http.post('/login', {name: $scope.name, publicKey: $scope.publicKey})
            .success(function (data, status, headers, config) {
                data.key = $scope.key;
                userService.setUser(data);
                $location.url("/channels");
            });
    }

});
