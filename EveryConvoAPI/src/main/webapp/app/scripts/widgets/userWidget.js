define(["../lib/values", "../lib/guda"], function(values, g) {
    
    var user = {};
    
    var login = function() {
        var successFn = undefined;
        var errorFn = undefined;
        this.success = function(fn) { successFn = fn; return this; }
        this.error = function(fn) { errorFn = fn; return this; }
            
        g.getAjax(values.API.login).done(function(data) {
            data = JSON.parse( data );
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
    
    return {
        user: user,
        login: login
    }
    
});