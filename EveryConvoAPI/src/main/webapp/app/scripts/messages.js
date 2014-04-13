define(["lib/guda", "lib/values", "feed"], function(g, values, feed) {
    
    var MessagesView = function() {
        
        this.init({ className: "inner" });
        this.create();
        
    };
    MessagesView.prototype = new feed.FeedView;
    MessagesView.prototype.openConversation = function(userId) {
        g.getAjax( values.API.messages + "/" + userId ).done( function(data) {
            g.log(data);
        });
    };
    
    return {
        MessagesView: MessagesView
    }
    
});