/**
 * Created by michael on 20.10.14.
 */

angular.module('app.contentListController', ['ionic','contentService', 'settingsService'])

    .run(function(contentService){

    })

    .controller('contentListController', function($scope, $rootScope, $ionicSideMenuDelegate, contentService, settingsService) {

        $scope.itemSelected = function(item, noMenuToggle){

            if(typeof analytics !== "undefined") {
                analytics.trackEvent('PageSelect', 'Select', 'SiteID', item.id);
            }

            contentService.setSelectedItem(item.id);
            $rootScope.$broadcast("app.contentSelected");

            if(noMenuToggle != true && settingsService.getConfiguration("closeMenuOnSelection") == true)
                $rootScope.toggleLeftMenu();

            var elements = document.getElementsByClassName("scroll");
            var style = elements[0].getAttribute("style");
            elements[0].setAttribute("style", "-webkit-transform: translate3d(0px, 0px, 0px) scale(1);");

            settingsService.setSetting("LAST_LOADED_PAGE", "INTEGER", item.id);
        };

        $rootScope.$on('app.selectItem', function(event, itemIdToSelect){
            $scope.itemSelected({ id : itemIdToSelect }, true);
        });


    });