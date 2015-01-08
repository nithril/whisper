whisperApp.factory('userService', ['$rootScope', '$window', 'stompService', function ($rootScope, $window, stompService) {


    var UserService = function () {
        var that = this;

        $rootScope.$on("stomp.connect" , function(event, message){

        });
    };


    UserService.prototype.getSessionStoredUser = function(){
        var pki = forge.pki;

        var sessionUserAsJson = $window.sessionStorage.getItem("user");

        if (sessionUserAsJson != null) {
            var sessionUser = JSON.parse(sessionUserAsJson);
            sessionUser.key = {
                privateKey: pki.privateKeyFromPem(sessionUser.privateKey),
                publicKey: pki.publicKeyFromPem(sessionUser.publicKey)
            };
            return sessionUser;
        }
        return null;
    };

    UserService.prototype.isLoggedIn = function(){
        return this.user != null;
    };



    UserService.prototype.getUser = function () {
        return this.user;
    };



    UserService.prototype.clearUser = function () {
        this.user = null;
    };

    UserService.prototype.login = function (user) {
        this.user = user;
        this.channels = [];
        this.subscribe();

        var sessionUser = {name:user.name ,
            publicId:user.publicId,
            privateId:user.privateId,
            privateKey:forge.pki.privateKeyToPem(user.key.privateKey),
            publicKey:forge.pki.publicKeyToPem(user.key.publicKey)};
        $window.sessionStorage.setItem("user", JSON.stringify(sessionUser));
    };

    UserService.prototype.subscribe = function () {

        if (stompService.connected) {

            var that = this;

            //Subscribe to private user queue
            stompService.stompClient.subscribe('/queue/' + that.user.privateId + "/message", function (message) {
                var body = JSON.parse(message.body);

                body.message = that.user.key.privateKey.decrypt(body.message);

                $rootScope.$emit("message.new", body);
            });

            //Subscribe to private user queue channel
            stompService.stompClient.subscribe('/queue/' + that.user.privateId + "/channel", function (message) {
                var body = JSON.parse(message.body);
                $rootScope.$emit("channel", body);
            });
        }

    };

    return new UserService();

}]);
