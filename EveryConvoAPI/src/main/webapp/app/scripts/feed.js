define(["lib/guda", "lib/values", "widgets/postWidget"], function(g, values, postWidget) {
    
    var FeedView = function() {
        
        this.init({ className: "inner" });
        this.create();
        
    };
    FeedView.prototype = new g.Widget;
    FeedView.prototype._create = FeedView.prototype.create = function() {
        var _this = this;
        
        this.title = new g.Widget({ className: "title" });
        this.sideView = new g.Widget({ id: "side" });
        this.feed = new g.Widget({
            id: "feed"
        });
        this.posts = new g.Widget({ className: "posts" });
        this.loadMoreButton = new g.Button({ 
            className: "load-more",
            onclick: function() {
                _this.loadPosts( _this.nextCursor );
            }
        }).setText( values.TEXT.loadMore );
        this.setNextCursor( null );

        this.feed.append( this.posts ).append( this.loadMoreButton );
        this.append( this.title ).append( this.sideView ).append( this.feed );
    };
    FeedView.prototype.setNextCursor = function(nextCursor) {
        if( nextCursor ) {
            this.loadMoreButton.show();
        }
        else {
            this.loadMoreButton.hide();
        }
        this.nextCursor = nextCursor;
    };
    FeedView.prototype._load = FeedView.prototype.load = function(userName, empty) {
        if( empty ) {
            this.element.innerHTML = "";
            this.create();
        }
        var filter = "";
        if( userName ) filter += "user=" + userName
        this.loadPosts( values.API.stories + "?" + filter );
    };
    FeedView.prototype.loadPosts = function(url) {
        var _this = this;
        g.getAjax( url ).done(function(data) {
            var data = JSON.parse( data );
            var posts = data.data;
            
            for(var i=0, l=posts.length; i<l; ++i) {
                _this.posts.append( postWidget.newPost(posts[i]) );
            }
            _this.setNextCursor( data.next );
        });
    };
    
    
    return {
        FeedView: FeedView,
    };
    
});