/**
 * Created by michael on 19.10.14.
 */

angular.module('settingsService', [])
    .factory("settingsService", function($http, $q, DB, DBContentItems, DBSettings){

        var self = this;
        self.settings = {};

        var configurations =  {
            closeMenuOnSelection : true
        };

        function getConfiguration(name){
            return configurations[name];
        }

        function getSetting(name, defaultValue){
            var defer = $q.defer();

            DBSettings.getById(name,defaultValue)
                .then(function(result){
                    defer.resolve(result);
                });

            return defer.promise;
        }

        function provideSettings(){

            var defer = $q.defer();

            DBSettings.all()
                .then(function(data){
                    self.settings = {};
                    for(var i = 0; i < data.length; i++){
                        var key = data[i].key;
                        var val = data[i].value_string;
                        self.settings[key] = val;
                    }
                });

            return defer.promise;
        }

        function setSetting(key, type, value){

            var defer = $q.defer();

            DBSettings.set(key, type, value)
                .then(function(){
                   defer.resolve();
                });

            return defer.promise;

        }

        return {
            getConfiguration  : getConfiguration,
            provideSettings : provideSettings,
            getSetting : getSetting,
            setSetting : setSetting,
            settings : self.settings
        };
    });


