define(["lib/guda", "lib/values"], function(g, values) {
    
    var init = function() {
        var view = new g.View({ id: "login-page" });
        var wrapper = new g.Widget({ className: "inner" });
        
        var title = new g.Widget({}, "h1").setText( "EveryConvo" );
        
        var loginForm = undefined;
        loginForm = new g.Form({
            id: "login",
            className: "box",
            method: "post",
            action: values.API.login,
            afterSubmit: function(data) { 
                data = JSON.parse( data );
                if( data.status && data.status == values.API.status.error ) {
                    var message = new g.Widget({ className: "error" }).setText( values.TEXT.loginError );
                    loginForm.append( message );
                    setTimeout( function() {
                        message.element.remove();
                    }, 3000 );
                }
                else location.reload(); 
            }
        });
        var registerForm = new g.Form({
            id: "register",
            className: "box",
            method: "post",
            action: values.API.createUser,
            afterSubmit: function(data) { 
                data = JSON.parse( data );
                if( data.status && data.status == values.API.status.error ) {
                    var message = new g.Widget({ className: "error" }).setText( values.TEXT.registerError );
                    registerForm.append( message );
                    setTimeout( function() {
                        message.element.remove();
                    }, 3000 );
                }
                else location.reload(); 
            }
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
                type: "button",
                onclick: function() {
                    registerForm.show( g.Widget.ANIM.zoomIn );
                    loginForm.hide( g.Widget.ANIM.zoomOut );
                }
            }).setText( "or register" )
        )
        .hide();
        

        registerForm.append( new g.Widget({}, "h3").setText( "Register" ) )
        .append(
            new g.Input({ name: "username", placeholder: "username" })
        ).append(
            new g.Input({ name: "firstname", placeholder: "firstname" })
        ).append(
            new g.Input({ name: "lastname", placeholder: "lastname" })
        ).append( 
            new g.Input({ name: "password", type: "password", placeholder: "**********" })
        ).append( 
            new g.Button({ name: "submit" }).setText( "Register" )
        ).append( 
            new g.Button({
                className: "second-button",
                type: "button",
                onclick: function() {
                    loginForm.show( g.Widget.ANIM.zoomIn );
                    registerForm.hide( g.Widget.ANIM.zoomOut );
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