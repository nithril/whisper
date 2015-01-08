'use strict';
/* Controllers */
var whisperApp = angular.module('whisperApp', ['ngRoute', 'ui.bootstrap', 'ui.layout', 'luegg.directives']);

whisperApp.directive('enterSubmit', function () {
    return {
        restrict: 'A',
        link: function (scope, elem, attrs) {

            elem.bind('keydown', function(event) {
                var code = event.keyCode || event.which;

                if (code === 13) {
                    if (!event.shiftKey) {
                        event.preventDefault();
                        scope.$apply(attrs.enterSubmit);
                    }
                }
            });
        }
    }
});





whisperApp.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/channels', {
                templateUrl: 'partials/channels.html',
                controller: 'ChannelsCtrl'
            }).
            when('/login', {
                templateUrl: 'partials/login.html',
                controller: 'LoginCtrl'
            }).
            otherwise({
                templateUrl: 'partials/login.html',
                controller: 'LoginCtrl'
            });
    }]);

whisperApp.config(['$httpProvider',
    function ($httpProvider) {


        $httpProvider.interceptors.push(function($q, $location, userService) {
            return {
                // optional method
                'responseError': function(rejection) {
                    if (rejection.status === 401) {
                        userService.clearUser();
                        $location.path('/login');
                        return $q.reject(rejection);
                    }
                    else {
                        return $q.reject(rejection);
                    }
                }
            };
        });


    }]);



whisperApp.config(['$httpProvider', function ($httpProvider) {

    var logsOutUserOn401 = ['$q', '$location', function ($q, $location) {
        var success = function (response) {
            return response;
        };

        var error = function (response) {
            if (response.status === 401) {
                //redirect them back to login page
                $location.path('/login');

                return $q.reject(response);
            }
            else {
                return $q.reject(response);
            }
        };
        return function (promise) {
            return promise.then(success, error);
        };
    }];

    $httpProvider.interceptors.push(logsOutUserOn401);
}]);



whisperApp.run( function($rootScope, $location, userService) {

    // register listener to watch route changes
    $rootScope.$on( "$routeChangeStart", function(event, next, current) {
        if ( userService.user == null ) {
            // no logged user, we should be going to #login
            if ( next.templateUrl == "partials/login.html" ) {
                // already going to #login, no redirect needed
            } else {
                // not going to #login, we should redirect now
                $location.path( "/login" );
            }
        }
    });
});


whisperApp.controller('ChannelsCtrl', function ($scope, $http, $location, userService, channelService) {

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
        $http.post('/ui/channels', {name: $scope.channelName})
            .success(function (data, status, headers, config) {
                //$scope.channels.push(data);
                $scope.refreshChannels();
                $scope.tabs.push({title: data.name, channel: data , publicId:data.publicId});
            });
    };

    $scope.joinChannel = function (channelName) {
        $http.post('/ui/channels', {name: channelName})
            .success(function (data, status, headers, config) {
                //$scope.channels.push(data);
                $scope.refreshChannels();
                $scope.tabs.push({title: data.name, channel: data , publicId:data.publicId});
            });
    };


    $scope.refreshChannels = function(){
        channelService.findAllChannel().success(function(){
            $scope.channels = channelService.channels;
        });
    };


    $scope.refreshChannels();
});