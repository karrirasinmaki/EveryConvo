define(["../lib/guda"], function(g) {    
    
    var ContactWidget = function(params) {
        this.init( params );
        this.create();
    };
    ContactWidget.prototype = new g.Widget;
    ContactWidget.prototype.create = function() {
        this.picture = new g.Widget({ className: "picture" });
        this.fullName = new g.Widget({ className: "full-name" });
        this.userName = new g.Widget({ className: "user-name" });
        this.info = new g.Widget({ className: "info" });
        this.info.append( this.fullName ).append( this.userName );
        
        this.append( this.picture ).append( this.info );
    };
    ContactWidget.prototype.setFullName = function(fullName) {
        this.fullName.element.textContent = fullName;
        return this;
    };
    ContactWidget.prototype.setUserName = function(userName) {
        this.userName.element.textContent = userName;
        return this;
    };
    
    return {
        ContactWidget: ContactWidget
    }
    
});