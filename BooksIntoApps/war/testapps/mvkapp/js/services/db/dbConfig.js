/**
 * Created by michael on 20.10.14.
 */

angular.module('dbConfig', [])
    .constant('DB_CONFIG', {
        name: 'BoInAp',
        tables: {
            contentitems: {
                name: 'contentitems',
                columns: [
                    {name: 'id', type: 'integer primary key'},
                    {name: 'title', type: 'text'},
                    {name: 'type', type: 'text'},
                    {name: 'sortorder', type: 'integer'},
                    {name: 'category', type: 'text'},
                    {name: 'icon', type: 'integer'},
                    {name: 'content', type: 'text'},
                    {name: 'quiz', type: 'text'}
                ]
            },

            settings: {
                name: "settings",
                columns: [
                    {name: 'key', type: 'text primary key'},
                    {name: 'type', type: 'text'},
                    {name: 'value_string', type: 'text'},
                    {name: 'value_int', type: 'integer'},
                    {name: 'value_float', type: 'real'},
                ]
            }
        }
    });