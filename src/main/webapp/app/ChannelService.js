
whisperApp.factory('channelService', ['$rootScope', '$http', function ($rootScope, $http) {

    var ChannelService = function () {
        this.channels = [];
    };

    ChannelService.prototype.findAllChannel = function () {
        var that = this;

        return $http.get('/ui/channels')
            .success(function (data, status, headers, config) {
                that.channels = data;
            });
    };

    return new ChannelService();

}]);
