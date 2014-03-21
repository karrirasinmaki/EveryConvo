define(["lib/guda", "widgets/postWidget"], function(g, postWidget) {
    
    var FeedView = function() {
        
        this.init({ className: "inner" });
        this.create();
        
    };
    FeedView.prototype = new g.Widget;
    FeedView.prototype.create = function() {
        this.title = new g.Widget({ id: "title" }, "h1");
        this.sideView = new g.Widget({ id: "side" });
        this.feed = new g.Widget({
            id: "feed"
        });

        this.append( this.title ).append( this.sideView ).append( this.feed );
    };
    FeedView.prototype._load = FeedView.prototype.load = function(userName, empty) {
        var filter = "";
        if( empty ) this.feed.element.innerHTML = "";
        if( userName ) filter += "user=" + userName
        g.log(filter);
        postWidget.loadPosts( this.feed, filter );
    };
    
    
    return {
        FeedView: FeedView,
    };
    
});