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
    PostWidget.prototype.setTime = function(time) {
        this.time.element.textContent = time;
        return this;
    };
    
    var newPost = function(data) {
        var post = new PostWidget({
                className: "post"
            })
            .setContent( data.content )
            .setTime( data.timestamp );
        
        return post;
    };
    
    var loadPosts = function() {
        g.getAjax( API.stories ).done(function(data) {
            g.log(data);
        });
    };
    
    return {
        newPost: newPost,
        loadPosts: loadPosts
    };
    
});