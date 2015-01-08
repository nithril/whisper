'use strict';
/* Controllers */


whisperApp.controller('ChannelCtrl', function ($rootScope, $scope, $http, $location, userService, stompService) {

    $scope.messages = [];
    $scope.user = userService.user;


    $rootScope.$on("message.new", function (event, message) {

        if (message.channel.publicId == $scope.channel.publicId) {
            $scope.$apply(function () {
                $scope.messages.push(message);
            });
        }
    });

    $rootScope.$on("channel", function (event, channel) {

        if (channel.publicId == $scope.channel.publicId) {
            $scope.channel = channel;
        }
    });


    //Send to the channel queue
    $scope.send = function () {

        _.forEach($scope.channel.users , function(user){
            stompService.stompClient.send('/app/queue/channel/' + $scope.channel.privateId, {}, JSON.stringify({
                message: $scope.user.key.publicKey.encrypt($scope.newMessage),
                toPublicId: user.publicId
            }));
        });

        $scope.newMessage = "";


    };


});