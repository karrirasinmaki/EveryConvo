define(["lib/guda", "lib/values"], function(g, values) {
    
    var init = function() {
        var view = new g.View();
        
        var loginForm = new g.Form({
            method: "post",
            action: values.API.login
        })
        .append( new g.Widget().setText( "Log in" ) )
        .append(
            new g.Input({ name: "username", placeholder: "username" })
        ).append( 
            new g.Input({ name: "password", type: "password", placeholder: "**********" })
        ).append( 
            new g.Button({ name: "submit" }).setText( "Log in" )
        );
        
        var registerForm = new g.Form({
            method: "post",
            action: values.API.createUser
        })
        .append( new g.Widget().setText( "Register" ) )
        .append(
            new g.Input({ name: "username", placeholder: "username" })
        ).append( 
            new g.Input({ name: "password", type: "password", placeholder: "**********" })
        ).append( 
            new g.Button({ name: "submit" }).setText( "Register" )
        );
        
        view.append( loginForm );
        view.append( registerForm );
    };
    
    return {
        init: init
    };

});