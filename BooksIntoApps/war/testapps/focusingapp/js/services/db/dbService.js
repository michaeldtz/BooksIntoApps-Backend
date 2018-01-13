/**
 * Created by michael on 20.10.14.
 */

angular.module('dbService', ['dbConfig'])
// DB wrapper
    .factory('DB', function($q, $log, DB_CONFIG) {
        var self = this;
        self.db = null;

        self.init = function() {



            try {

                if(window.sqlitePlugin !== undefined){
                    self.db = window.sqlitePlugin.openDatabase({name: DB_CONFIG.name});
                } else {
                    self.db = window.openDatabase(DB_CONFIG.name, '1.0', 'database', -1);
                }

            angular.forEach(DB_CONFIG.tables, function (table) {
                var columns = [];

                angular.forEach(table.columns, function (column) {
                    columns.push(column.name + ' ' + column.type);
                });

                var query = 'CREATE TABLE IF NOT EXISTS ' + table.name + ' (' + columns.join(',') + ')';
                self.query(query);
                console.log('Table ' + table.name + ' initialized');
            });

            } catch(e){
                console.log("Error init db " + e);
            }
        };

        self.checkInit = function(){
            if(self.db == null){
                self.init();
            }
        };

        self.query = function(query, bindings) {

            self.checkInit();

            bindings = typeof bindings !== 'undefined' ? bindings : [];
            var deferred = $q.defer();

            self.db.transaction(function(transaction) {
                transaction.executeSql(query, bindings, function(transaction, result) {
                    deferred.resolve(result);
                }, function(transaction, error) {
                    deferred.reject(error);
                });
            });

            return deferred.promise;
        };

        self.fetchAll = function(result) {

            var output = [];

            for (var i = 0; i < result.rows.length; i++) {
                output.push(result.rows.item(i));
            }

            return output;
        };

        self.fetch = function(result) {
            return result.rows.item(0);
        };

        self.updateStmt = function(updateStmt, bindings){

            self.checkInit();

            var deferred = $q.defer();
             self.db.transaction(function (tx) {
                var res = tx.executeSql(updateStmt, bindings, function(transaction, result) {
                    deferred.resolve(result);
                }, function(transaction, error) {
                    console.error(error);
                    deferred.reject(error);
                });
            });

            return deferred.promise;
        };

        return self;
    })
// Resource service example
    .factory('DBContentItems', function(DB, DB_CONFIG) {
        var self = this;

        self.all = function() {
            return DB.query('SELECT * FROM contentitems ORDER BY sortorder ASC')
                .then(function(result){
                    return DB.fetchAll(result);
                });
        };

        self.getById = function(id) {
            return DB.query('SELECT * FROM contentitems WHERE id = ?', [id])
                .then(function(result){
                    return DB.fetch(result);
                });
        };

        self.insertOrUpdate = function(data){

            var columns = DB_CONFIG.tables.contentitems.columns;
            var qmString = "";
            var dataArray = [];

            for(var i = 0; i < columns.length; i++){


                if(i > 0)
                    qmString += ",";
                qmString += "?";


                if(data[columns[i].name] != undefined){
                    dataArray.push(data[columns[i].name]);
                } else {
                    dataArray.push("");
                }
            }

            var sql = "INSERT OR REPLACE INTO contentitems VALUES (" + qmString + ")";
            return DB.updateStmt(sql, dataArray)
                .then(function(result){
                    return "OK"
                });
        };

        self.truncate = function(){
            return DB.updateStmt("DELETE FROM contentitems", [])
                .then(function(result){
                    return "OK"
                });
        };

        return self;
    })

    .factory('DBSettings', function(DB) {
        var self = this;

        self.all = function() {
            return DB.query('SELECT * FROM settings')
                .then(function(result){
                    return DB.fetchAll(result);
                });
        };

        self.getById = function(id, defValue) {
            return DB.query('SELECT * FROM settings WHERE key = ?', [id])
                .then(function(result){
                    if(result.rows.length > 0){
                        var value = DB.fetch(result);
                        if(value.type == "STRING")
                             return value.value_string;
                        else if(value.type == "INTEGER")
                            return value.value_int;
                        else if(value.type == "FLOAT")
                            return value.value_float;
                    } else
                        return defValue ;
                });
        };

        self.set = function(key,type,value){

            var dataArray = [];
            if(type == "FLOAT") {
                dataArray = [key,type,"",0,parseFloat(value)];
            } else if(type == "INTEGER"){
                dataArray = [key,type,"",parseInt(value),0];
            } else {
                dataArray = [key, type, value,0,0];
            }

            return DB.updateStmt("INSERT OR REPLACE INTO settings VALUES (?,?,?,?,?)", dataArray)
                .then(function(result){
                    return "OK"
                });

        };

        return self;
    });