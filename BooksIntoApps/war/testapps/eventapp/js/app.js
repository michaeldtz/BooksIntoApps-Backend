// Ionic Starter App'
angular.module('app', ['ionic','ngCordova', 'app.mainController','app.contentListController','app.contentController'])

.run(function($ionicPlatform, $cordovaSplashscreen) {

      setTimeout(function() {
    	  if($cordovaSplashscreen)
    		  $cordovaSplashscreen.hide();
      }, 3000)

  $ionicPlatform.ready(function() {

    console.log("Welcome to BoInAp");

    if(window.cordova && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
    }
    if(window.StatusBar) {
      StatusBar.styleDefault();
    }

    if(typeof analytics !== "undefined") {
      analytics.startTrackerWithId("UA-19678063-9");
    } else {
      console.log("Google Analytics Unavailable");
    }

  });
})
