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
        this.intervalId = null;
        
    };
    MessagesView.UPDATE_INTERVAL = 3000;
    MessagesView.prototype = new feed.FeedView;
    MessagesView.prototype.create = function() {
        var _this = this;
        this._create();
        
        this.from = "&limit=20";
        
        this.messagesArea = new g.Widget({ id: "messages" });
        this.messages = new g.Widget({ className: "messages-inner" });
        this.messagesArea.append( this.messages );
        
        this.newMessageForm = new g.Form({
            id: "new-message",
            className: "group",
            action: values.API.message,
            method: "post",
            afterSubmit: function() {
                this.reset();
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
        
        this.feed.append( this.messagesArea ).append( this.newMessageForm );
    };
    MessagesView.prototype.openConversation = function(userId) {
        currentUser = window.EveryConvo.user;
        this.toid = userId;
        this.toidHiddenInput.element.value = userId;
        
        this.title.setHTML( "Conversation with: " + "<small>" + userId + "</small>" );
        this.loadMessages();
    };
    MessagesView.prototype.closeConversation = function() {
        clearInterval( this.intervalId );
        this.intervalId = null;
    };
    MessagesView.prototype.hide = function(param) {
        this._hide( param );
        this.closeConversation();
    };
    MessagesView.prototype.loadMessages = function() {
        var _this = this;
        g.getAjax( values.API.messages + "/" + this.toid + "?from=" + this.from ).done( function(data) {
            _this.drawMessages( JSON.parse(data).data );
        });
        this.from = new Date().getTime();
        
        if( this.intervalId == null ) {
            this.intervalId = setInterval( function() { _this.loadMessages() }, MessagesView.UPDATE_INTERVAL );
        }
    };
    MessagesView.prototype.drawMessages = function(data) {
        if( data.length <= 0 ) return;
        var scrollToBottom = false;
        if( this.messagesArea.element.scrollTop + this.messagesArea.element.offsetHeight > this.messages.element.offsetHeight - 60 )
            scrollToBottom = true;
        
        var d = document.createDocumentFragment();
        for( var i=0, l=data.length; i<l; ++i ) {
            d.appendChild( new MessageWidget(data[i]).show( g.Widget.ANIM.zoomIn ).element );
            
        }
        this.messages.append( d );
        
        if( scrollToBottom ) this.messagesArea.element.scrollTop = this.messages.element.offsetHeight;
    };
    
    return {
        MessagesView: MessagesView
    }
    
});