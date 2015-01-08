'use strict';
/* Controllers */

whisperApp.controller('LoginCtrl', function ($scope, $http, $location, userService) {

    var sessionUser = userService.getSessionStoredUser();

    if (sessionUser) {
        $scope.name = sessionUser.name;
        $scope.key = sessionUser.key;
        $scope.publicKey = forge.pki.publicKeyToPem(sessionUser.key.privateKey);
        $scope.privateKey = forge.pki.privateKeyToPem(sessionUser.key.privateKey);

    }


    $scope.generate = function () {
        $scope.key = forge.pki.rsa.generateKeyPair({bits: 2048});
        $scope.publicKey = forge.pki.publicKeyToPem($scope.key.privateKey);
        $scope.privateKey = forge.pki.privateKeyToPem($scope.key.privateKey);
    };

    $scope.login = function () {

        $http.post('/login', {name: $scope.name, publicKey: $scope.publicKey})
            .success(function (data, status, headers, config) {
                data.key = $scope.key;
                userService.login(data);
                $location.url("/channels");
            });
    }

});
