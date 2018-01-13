/**
 * Created by michael on 19.10.14.
 */

angular.module('contentService', ['dbService'])
    .factory("contentService", function($http, $q, DB, DBContentItems, DBSettings){


        var appkey = "APcca448400165436a86da7afbc75fcdae";
        var secret = "SCa1f3422e6aee4bcab19507e09838d2a5";

     //   var appkey = "APa9733e2c622a40129a8005c9848c7243";
     //   var secret = "SC5b20e2b62f9540b88e0e04b06d4f58cb";

//        var appkey = "AP4a89e2048a634b86803f081bdbd5cbc1";
//        var secret = "SCbe8406c1db964a84b9040d9e85113fd1";


        var updateInterval = 864000000;



        var self = this;

        self.content = [];
        self.activeContentID = undefined;

        //Initialize the DB
       /* document.addEventListener("deviceready", function(){
            DB.init();
        }, false);

        window.onload = function () {
            if(!window.device)
                DB.init();
        }*/


        //Load the content
        function provideContent(forceRefresh){

            //create a defer
            var defer = $q.defer();

            //clear current content
            self.content.splice(0,self.content.length);

            /*//Add start page
            self.content.push({
                id : "FE323AB212",
                title: "Willkommen zum Buch",
                category : "Kapitel 1",
                icon : "asterisk",
                type : "article",
                content:"<p>Hallo 2. Dieser Text soll auf der Start<strong>seite erscheinen&#8230;.<\/strong><\/p>\n<p><b><i>Lass uns noch ein wenig ausprobieren<\/i><\/b><i> was es alles gibt<\/i>.<\/p>\n<p><span style=\"color: #767676;\"><span style=\"text-decoration: line-through;\">Das will ich nicht mehr<\/span><\/span><\/p>\n<ul>\n<li>Listenpunkt 1<\/li>\n<li>Listenpunkt 2<\/li>\n<\/ul>\n","parent":0,"link":"http:\/\/booksintoapps.bitnamiapp.com\/wordpress\/2014\/10\/17\/hallo-2\/","date":"2014-10-17T19:54:40+00:00","modified":"2014-10-19T13:04:45+00:00","format":"standard","slug":"hallo-2","guid":"http:\/\/booksintoapps.bitnamiapp.com\/wordpress\/?p=4","excerpt":"<p>Hallo 2. Dieser Text soll auf der Startseite erscheinen&#8230;. Lass uns noch ein wenig ausprobieren was es alles gibt. Das will ich nicht mehr Listenpunkt 1 Listenpunkt 2<\/p>\n"
            });*/

            //Set first page as active
            //self.activeContent = self.content[0];

            //Check if db is filled and up to date
            if(forceRefresh == true){
                loadContentFromServer(defer);
            } else {
                DBSettings.getById("IS_DATABASE_FILLED", "false")
                    .then(function(result){
                        if(result != "true"){
                            loadInitialContent(defer);
                            //loadContentFromServer(defer);
                        } else {
                            loadContentFromDB(defer);
                        }
                    });
            }

            return defer.promise;


        }

        function checkForUpdatesIfActive(){

            var defer = $q.defer();

            DBSettings.getById("AUTO_UPDATES_ACTIVE", "false")
                .then(function(active){
                    if(active == "true"){
                        DBSettings.getById("LAST_SERVER_UPDATE", 0)
                            .then(function(lastUpdate){
                                var nextUpdate = new Date().getTime() - updateInterval;
                                if(nextUpdate > lastUpdate){
                                    loadContentFromDB(defer);
                                } else {
                                    defer.reject("Update interval not reached");
                                }
                            });
                    } else {
                        defer.reject("Auto updates not active");
                    }
                });

            return defer.promise;

        }

        //Load the content from a file
        function loadInitialContent(defer){
            $http.get("js/content/initialContent.json")
                .success(function(data){
                    console.log("Loading " + data.length + " entries from initial content ");
                    for(var i = 0; i < data.length; i++){
                        self.content.push(data[i]);
                        DBContentItems.insertOrUpdate(data[i]);
                    }
                    defer.resolve(self.content);
                })
                .error(function(error,status){
                    defer.reject(error);
                });
        }

        //load the content from the database
        function loadContentFromDB(defer){
            DBContentItems.all()
                .then(function(data){
                    console.log("Loading " + data.length + " entries from db ");
                    for(var i = 0; i < data.length; i++){
                        self.content.push(data[i]);
                    }
                    defer.resolve(self.content);
                },function(error){
                    defer.reject(error);
                });
        }

        //Reload content from the server
        function loadContentFromServer(defer){
            $http.get("/services/load/contentlist?appkey=" + appkey + "&secret=" + secret)
                .success(function(data){

                    var content = data.content;
                    //var media = data.media;

                    console.log("Loading " + content.length + " content entries from server ");
                    //console.log("Loading " + media.length + " media entries from server ");
                    if(content.length > 0){
                        DBContentItems.truncate();
                    }

                    for(var i = 0; i < content.length; i++){
                        self.content.push(content[i]);
                        DBContentItems.insertOrUpdate(content[i]);
                    }

                    DBSettings.set("IS_DATABASE_FILLED","STRING","true");
                    DBSettings.set("LAST_SERVER_UPDATE","INTEGER",new Date().getTime());
                    defer.resolve(self.content);
                })
                .error(function(error,status){
                    //defer.reject(error);

                    console.log("Loading data from server failed due to " + error);
                    loadContentFromDB(defer);

                });

        }

        //set selected index
        function setSelectedItem(itemID){
            self.activeContentID = itemID;
        }

        function getContentWithID(itemID){
            for(var i = 0; i < self.content.length; i++){
                if(self.content[i].id == itemID){
                    return self.content[i];
                }
            }

            return {};
        }

        //get active content
        function getActiveContent(){
            if(self.activeContentID == undefined){
                self.activeContentID = self.content[0].id;
            }
            return getContentWithID(self.activeContentID);
        }


        return {
            provideContent : provideContent,
            content : self.content,
            getActiveContent : getActiveContent,
            setSelectedItem : setSelectedItem,
            checkForUpdatesIfActive : checkForUpdatesIfActive
        };
    });


