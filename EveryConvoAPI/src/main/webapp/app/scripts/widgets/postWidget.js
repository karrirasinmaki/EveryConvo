define(["../guda"], function(g) {
    
    var API = {
        stories: "http://localhost:8080/EveryConvoAPI/stories"
    };
    
    var PostWidget = function(params) {
        this.init( params );
        this.create();
    };
    PostWidget.prototype = new g.Widget;
    PostWidget.prototype.create = function() {
        this.time = new g.Widget({ className: "time" });
        this.picture = new g.Widget({ className: "picture" });
        this.content = new g.Widget({ className: "content" });
        
        this.append( this.time ).append( this.picture ).append( this.content );
    };
    PostWidget.prototype.setContent = function(content) {
        this.content.element.textContent = content;
        return this;
    };
    PostWidget.prototype.setMedia = function(mediaURL) {
        this.mediaURL = mediaURL;
        this.media = new g.MediaWidget({ className: "media", mediaURL: this.mediaURL });
        this.content.append( this.media );
        return this;
    };
    PostWidget.prototype.setTime = function(time) {
        this.time.element.textContent = time;
        return this;
    };
    
    var newPost = function(data) {
        var time = new Date(data.timestamp);
        var post = new PostWidget({
                className: "post"
            })
            .setContent( data.content )
            .setTime( time.getHours() + ":" + time.getMinutes() )
            .setMedia( data.mediaurl );
        
        return post;
    };
    
    var loadPosts = function(element) {
        g.getJSON( API.stories ).done(function(data) {
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