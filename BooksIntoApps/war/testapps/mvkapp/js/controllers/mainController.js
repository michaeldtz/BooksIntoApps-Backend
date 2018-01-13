/**
 * Created by michael on 19.10.14.
 */

angular.module('app.mainController', ['ionic', 'app.contentController', 'contentService'])

    .run(function(){
       // contentService.provideContent();
    })

    .controller('mainController', function($scope, $rootScope, $ionicSideMenuDelegate, $ionicModal, contentService, settingsService) {

        $rootScope.toggleLeftMenu = function() {
            $ionicSideMenuDelegate.toggleLeft();
        };


        $rootScope.toggleRightMenu = function() {
            $ionicSideMenuDelegate.toggleRight();
        };

        $rootScope.refreshContent = function(){
            contentService.provideContent(true)
                .then(function(){
                    $rootScope.$broadcast("app.contentInitialized");
                    $scope.closeModal();
                });
        };


        $rootScope.provideContent = function(){
            contentService.provideContent()
                .then(function(content){
                    $scope.content = contentService.content;
                    $rootScope.$broadcast("app.contentInitialized");

                    contentService.checkForUpdatesIfActive()
                        .then(function(result){
                            $scope.content = contentService.content;
                        },function(notExecutedResult){
                            console.log(notExecutedResult);
                        });

                    settingsService.getSetting("LAST_LOADED_PAGE","")
                        .then(function(lastPage){
                            if(lastPage != undefined && lastPage != "")
                                $rootScope.$broadcast("app.selectItem", lastPage);
                        });


                });
        };

        $scope.provideSettings = function(key){
            settingsService.provideSettings()
                .then(function(){
                    $scope.settings = settingsService.settings;
                });
            $rootScope.$broadcast("app.settingsLoaded");
        };

        function init(){
            $rootScope.provideContent();
            $scope.provideSettings();
        }

        //Call the first functions to initalize the data
        document.addEventListener("deviceready", init,false);

        window.onload = function () {
            if(!window.device)
                init();
        }

        //Modal View for Settings
        $ionicModal.fromTemplateUrl('templates/settings.html', {
            scope: $scope,
            animation: 'slide-in-up'
        }).then(function(modal) {
            $scope.settingsmodal = modal;
        });

        $scope.openSettingsModal = function() {
            $scope.settingsmodal.show();
        };
        $scope.closeSettingsModal = function() {
            $scope.settingsmodal.hide();
        };

        //Modal View for Contact
        $ionicModal.fromTemplateUrl('templates/contact.html', {
            scope: $scope,
            animation: 'slide-in-up'
        }).then(function(modal) {
            $scope.contactmodal = modal;
        });

        $scope.openContactModal = function() {
            $scope.contactmodal.show();
        };
        $scope.closeContactModal = function() {
            $scope.contactmodal.hide();
        };

        $scope.$on('$destroy', function() {
            $scope.settingsmodal.remove();
            $scope.contactmodal.remove();
        });

    });