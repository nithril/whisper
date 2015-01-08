whisperApp.factory('stompService', ['$rootScope', function ($rootScope) {


    var StompService = function () {
        this.init();
    };


    StompService.prototype.init = function () {
        this.sock = new SockJS('http://localhost:8080/portfolio/');
        this.stompClient = Stomp.over(this.sock);

        var that = this;

        this.stompClient.connect({}, function (frame) {
            that.connected = true;
            $rootScope.$emit("stomp.connect" , "");
        }, function (error) {
            that.errorCb(error);
        });
    };

    StompService.prototype.errorCb = function (error) {
        alert("error " + error.headers.message);
    };


    return new StompService();

}]);
