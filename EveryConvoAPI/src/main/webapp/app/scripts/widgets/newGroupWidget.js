define(["../lib/guda", "../lib/values"], function(g, values) {

    var newGroupForm = new g.Form({
        id: "register",
        className: "box",
        method: "post",
        action: values.API.createGroup,
        afterSubmit: function(data) { 
            data = JSON.parse( data );
            if( data.status && data.status == values.API.status.error ) {
                var message = new g.Widget({ className: "error" }).setText( values.TEXT.registerError );
                newGroupForm.append( message );
                setTimeout( function() {
                    message.element.remove();
                }, 3000 );
            }
            else location.reload(); 
        }
    });
    
    newGroupForm.append( new g.Widget({}, "h3").setText( "Add new group" ) )
        .append(
            new g.Input({ name: "fullname", placeholder: "full name" })
        ).append(
            new g.Input({ name: "username", placeholder: "group short name" })
        ).append(
            new g.Input({ name: "description", placeholder: "description" })
        ).append(
            new g.Input({ name: "websiteurl", placeholder: "website url" })
        ).append(
            new g.Input({ name: "location", placeholder: "location" })
        ).append( 
            new g.Input({ name: "password", type: "password", placeholder: "**********" })
        ).append( 
            new g.Button({ name: "submit" }).setText( "Create" )
        );
    
    
    var newGroup = new g.Widget({ 
        id: "new-group",
        className: "bottom-pull"
    });
    newGroup.append( newGroupForm );
    newGroup.hide();
    
    
    return {
        newGroup: newGroup
    };
    
});