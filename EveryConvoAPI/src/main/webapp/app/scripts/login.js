define(["lib/guda", "lib/values"], function(g, values) {
    
    var init = function() {
        var view = new g.View({ id: "login-page" });
        var wrapper = new g.Widget({ className: "inner" });
        
        var title = new g.Widget({}, "h1").setText( "EveryConvo" );
        
        var loginForm = new g.Form({
            id: "login",
            className: "box",
            method: "post",
            action: values.API.login,
            afterSubmit: function(data) { location.reload(); }
        });
        var registerForm = new g.Form({
            id: "register",
            className: "box",
            method: "post",
            action: values.API.createUser
        });
        
        loginForm.append( new g.Widget({}, "h3").setText( "Log in" ) )
        .append(
            new g.Input({ name: "username", placeholder: "username" })
        ).append( 
            new g.Input({ name: "password", type: "password", placeholder: "**********" })
        ).append( 
            new g.Button({ name: "submit" }).setText( "Log in" )
        ).append( 
            new g.Button({
                className: "second-button",
                onclick: function() {
                    registerForm.show();
                    loginForm.hide();
                }
            }).setText( "or register" )
        )
        .hide();
        

        registerForm.append( new g.Widget({}, "h3").setText( "Register" ) )
        .append(
            new g.Input({ name: "username", placeholder: "username" })
        ).append( 
            new g.Input({ name: "password", type: "password", placeholder: "**********" })
        ).append( 
            new g.Button({ name: "submit" }).setText( "Register" )
        ).append( 
            new g.Button({
                className: "second-button",
                onclick: function() {
                    loginForm.show();
                    registerForm.hide();
                }
            }).setText( "or login" )
        );
        
        wrapper.append( title ).append( loginForm ).append( registerForm );
        view.append( wrapper );
    };
    
    return {
        init: init
    };

});