/**
 * Created by michael on 19.10.14.
 */

angular.module('app.contentController', ['ionic', 'contentService'])

    .run(function(){
    })

    .controller('contentController', function($scope, $rootScope, $ionicSideMenuDelegate, contentService) {

        function refreshContent(){
            $scope.activeContent = contentService.getActiveContent();
        }

        $rootScope.$on('app.contentInitialized', function(){
            refreshContent();
        });

        $rootScope.$on('app.contentSelected', function(){
            refreshContent();
        });




    });