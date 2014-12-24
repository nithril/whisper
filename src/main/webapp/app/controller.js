'use strict';
/* Controllers */
var phonecatApp = angular.module('phonecatApp', ['ngRoute', 'ui.bootstrap']);


phonecatApp.factory('userService', ['$rootScope', function ($rootScope) {

    var UserService = function () {
        this.user = null;
    };

    UserService.prototype.setUser = function (user) {
        this.user = user;
        this.channels = [];
        this.subscribe();
    };

    UserService.prototype.subscribe = function () {
        var that = this;

        //Subscribe to private user queue
        stompClient.subscribe('/queue/' + that.user.privateId + "/message", function (message) {
            var body = JSON.parse(message.body);

            body.message = cryptico.decrypt(body.message, that.user.key).plaintext;

            $rootScope.$emit("message.new", body);
        });

        //Subscribe to private user queue channel
        stompClient.subscribe('/queue/' + that.user.privateId + "/channel", function (message) {
            var body = JSON.parse(message.body);
            $rootScope.$emit("channel", body);
        });

    };

    return new UserService();

}]);


phonecatApp.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/channels', {
                templateUrl: 'partials/channels.html',
                controller: 'ChannelsCtrl'
            }).
            when('/phones/:phoneId', {
                templateUrl: 'partials/phone-detail.html',
                controller: 'PhoneDetailCtrl'
            }).
            otherwise({
                templateUrl: 'partials/login.html',
                controller: 'LoginCtrl'
            });
    }]);



phonecatApp.controller('ChannelsCtrl', function ($scope, $http, $location, userService) {

    $scope.messages = [];
    $scope.user = userService.user;

    $scope.channels = [];

    $scope.tabs = [];

    $scope.channelName = "";


    /*    //Subscribe to private user queue
     stompClient.subscribe('/queue/' + userService.user.privateId, function (message) {
     var body = JSON.parse(message.body);

     body.message = cryptico.decrypt(body.message, $scope.user.key).plaintext;

     $scope.$apply(function () {
     $scope.messages.push(body);
     });
     });

     //Send to the channel queue
     $scope.send = function () {
     stompClient.send('/app/queue/channel/', {}, JSON.stringify({
     message: cryptico.encrypt($scope.message, $scope.user.publicKey).cipher,
     privateId: $scope.user.privateId
     }));
     };*/

    $scope.newChannel = function () {

        $http.post('/ui/channel', {name: $scope.channelName})
            .success(function (data, status, headers, config) {
                $scope.channels.push(data);
                $scope.tabs.push({title: data.name, channel: data});
            });
    }

});