define(["lib/guda", "lib/values", "feed"], function(g, values, feed) {
    
    var currentUser = {};
    var MessageWidget = function(data) {
        this.init({ className: "message" });
        this.content = new g.Widget({ className: "content" }).setText( data.content );
        
        this.append( this.content );
        
        if( data.fromid == currentUser.userid ) this.addClass( "from-me" );
    };
    MessageWidget.prototype = new g.Widget;
    
    var MessagesView = function() {
        
        this.init({ className: "inner" });
        this.create();
        
    };
    MessagesView.prototype = new feed.FeedView;
    MessagesView.prototype.create = function() {
        var _this = this;
        this._create();
        
        this.messages = new g.Widget({ id: "messages" });
        
        this.newMessageForm = new g.Form({
            id: "new-message",
            className: "group",
            action: values.API.message,
            method: "post",
            afterSubmit: function() {
                this.reset();
                _this.messages.empty();
                _this.loadMessages();
            }
        });
        this.toidHiddenInput = 
            new g.Input({
                type: "hidden",
                name: "to"
            });
        this.newMessageForm.append(
            new g.Input({ name: "content", className: "w-all", placeholder: "Type here..." }, "span")
        ).append( this.toidHiddenInput );
        
        
        this.feed.append( this.messages ).append( this.newMessageForm );
    };
    MessagesView.prototype.openConversation = function(userId) {
        currentUser = window.EveryConvo.user;
        this.toid = userId;
        this.toidHiddenInput.element.value = userId;
        
        this.title.setHTML( "Conversation with: " + "<small>" + userId + "</small>" );
        this.loadMessages();
    };
    MessagesView.prototype.loadMessages = function() {
        var _this = this;
        g.getAjax( values.API.messages + "/" + this.userId ).done( function(data) {
            _this.drawMessages( JSON.parse(data).data );
        });
    };
    MessagesView.prototype.drawMessages = function(data) {
        g.log( data );
        var d = document.createDocumentFragment();
        for( var i=0, l=data.length; i<l; ++i ) {
            d.appendChild( new MessageWidget(data[i]).element );
        }
        this.messages.append( d );
    };
    
    return {
        MessagesView: MessagesView
    }
    
});