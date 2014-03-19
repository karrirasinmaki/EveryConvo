define(["../guda"], function(g) {    
    
    var MenuElement = function(params) {
        this.init( params );
        this.addClass( "menu-item" );
    };
    MenuElement.prototype = new g.Widget;
    
    return {
        MenuElement: MenuElement
    }
    
});