/**
 * Created by michael on 19.10.14.
 */

angular.module('app.mainController',
		[ 'ngAnimate', 'ngSanitize', 'textAngular' ])

.config(function() {

})

.run(function() {

})

.controller(
		'mainController',
		function($scope, $http) {

			$scope.projectlist = [];
			$scope.projectitem = undefined;
			$scope.busy = false;

			$scope.loadProjectlist = function() {
				$http.get("../servicesadmin/project/list").success(
						function(data, status, headers, config) {
							$scope.projectlist = data;
						});
			}

			$scope.projectitemSelected = function(selectedItem){
	    		$scope.projectitem = selectedItem;
	    	}
			
			$scope.createProject = function(){
	    	
				var title = prompt("Enter the Title");
	    		if(title == undefined)
	    			return;
	    		
	    		var password = prompt("Enter a Password");
	    		if(password == undefined)
	    			return;
	    		
	    		$http.post("../servicesadmin/project/create", "title=" + title + "&password="+password)
	    			.success(function(data){
	    				$scope.projectlist.push({
	    					title 		: title
	    				});
	    				$scope.loadProjectlist();
	    			});
	    	}
			
			$scope.loginTo = function(){
				console.log("LOGINS");
				$http.post("../servicesadmin/project/loginto", "id=" + $scope.projectitem.id)
					.success(function(data){
						if(data.status == "S")
							location.href = "/app";
					});
				
			}
			
			
			$scope.loadProjectlist();
			
		});