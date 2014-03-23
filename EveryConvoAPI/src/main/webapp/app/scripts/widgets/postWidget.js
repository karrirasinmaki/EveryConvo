define(["../lib/guda", "../lib/values"], function(g, values) {
    
    var PostWidget = function(params) {
        this.init( params );
        this.create();
    };
    PostWidget.prototype = new g.Widget;
    PostWidget.prototype.create = function() {
        this.time = new g.Widget({ className: "time" });
        this.picture = new g.Widget({ className: "picture" });
        this.username = new g.Widget({ className: "username" });
        this.content = new g.Widget({ className: "content" });
        
        this.content.insert( this.username );
        this.append( this.time ).append( this.picture ).append( this.content );
    };
    PostWidget.prototype.setPictureUrl = function(imageUrl) {
        this.picture.element.style.backgroundImage = "url(" + values.API.baseUrl + imageUrl + ")";
        return this;
    };
    PostWidget.prototype.setUsername = function(username) {
        this.username.setText( username );
        return this;
    };
    PostWidget.prototype.setContent = function(content) {
        this.content.setText( content );
        return this;
    };
    PostWidget.prototype.setMedia = function(mediaURL) {
        this.mediaURL = mediaURL;
        this.media = new g.MediaWidget({ className: "media", mediaURL: this.mediaURL });
        this.content.append( this.media );
        return this;
    };
    PostWidget.prototype.setTime = function(time) {
        this.time.setText( time );
        return this;
    };
    
    var newPost = function(data) {
        var time = new Date(data.timestamp);
        var post = new PostWidget({
                className: "post"
            })
            .setPictureUrl( data.imageurl )
            .setUsername( data.username )
            .setContent( data.content )
            .setTime( time.getHours() + ":" + time.getMinutes() )
            .setMedia( data.mediaurl );
        
        return post;
    };
    
    var loadPosts = function(element, filter) {
        filter = filter || "";
        g.getAjax( values.API.stories + "?" + filter ).done(function(data) {
            var data = JSON.parse( data ).data;
            for(var i=0, l=data.length; i<l; ++i) {
                element.append( newPost(data[i]) );
            }
        });
    };
    
    return {
        newPost: newPost,
        loadPosts: loadPosts
    };
    
});