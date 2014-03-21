define(["../lib/guda", "./contactWidget"], function(g, contactWidget) {    
    
    var MenuElement = function(params) {
        this.init( params );
        this.addClass( "menu-item" );
    };
    MenuElement.prototype = new g.Widget;
    
    var menu = new g.SidebarWidget({
        id: "menu"
    });
    menu.user = 
        new contactWidget.ContactWidget({
            id: "the-user",
            className: "contact"
        });
    menu.profile = new MenuElement().setText( "Profile" );
    menu.messages = new MenuElement().setText( "Messages" );
    menu.people = new MenuElement().setText( "People" );
    menu.feed = new MenuElement().setText( "Feed" );
    menu.post = new MenuElement().setText( "Post" );
    menu.settings = new MenuElement().setText( "Settings" );
    menu.logout = new MenuElement().setText( "Log out" );
    
    var menuFirstHalf = new g.Widget({ className: "first-half" });
    var menuSecondHalf = new g.Widget({ className: "second-half" });
    menuFirstHalf.append( menu.profile ).append( menu.feed ).append( menu.messages ).append( menu.people );
    menuSecondHalf.append( menu.post ).append( menu.settings ).append( menu.logout );

    menu.append( menu.user ).append( menuFirstHalf ).append( menuSecondHalf );
    
    
    return {
        MenuElement: MenuElement,
        menu: menu
    }
    
});