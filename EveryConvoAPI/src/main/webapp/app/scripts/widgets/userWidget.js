define(["../lib/values", "../lib/guda"], function(values, g) {
    
    var user = {};
    
    var login = function() {
        var successFn = undefined;
        var errorFn = undefined;
        this.success = function(fn) { successFn = fn; return this; }
        this.error = function(fn) { errorFn = fn; return this; }
            
        g.getAjax(values.API.user).done(function(data) {
            data = JSON.parse( data ).data;
            if( data.userid ) {
                user = data;
                if( successFn ) successFn( data );
            }
            else {
                if( errorFn ) errorFn( data );
            }
        });
        
        return this;
    };
    
    var loadAllUsers = function(callback, query) {
        var q = values.API.people;
        if( query ) q += "/%25" + query + "%25";
        g.getAjax(q).done(function(data) {
            if( callback ) callback( JSON.parse(data).data );
        });
    };
    
    var loadAllGroups = function(callback, query) {
        var q = values.API.groups;
        if( query ) q += "/%25" + query + "%25";
        g.getAjax(q).done(function(data) {
            if( callback ) callback( JSON.parse(data).data );
        });
    };
    
    return {
        user: user,
        login: login,
        loadAllUsers: loadAllUsers,
        loadAllGroups: loadAllGroups
    }
    
});