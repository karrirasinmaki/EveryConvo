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
        this.actionBar = new g.Widget({ className: "action-bar" });
        this.createActionBar();
        
        this.content.insert( this.username );
        this.append( this.time ).append( this.picture ).append( this.content ).append( this.actionBar );
    };
    PostWidget.prototype.like = function() {
        var _this = this;
        g.postAjax(values.API.story + "/" + this.__id + "?like=true").done(function() {
            _this.likeButton.setText( values.TEXT.liked );
        });
    };
    PostWidget.prototype.createActionBar = function() {
        var _this = this;
        this.likeButton = 
            new g.Button({
                className: "like",
                onclick: function() {
                    _this.like();
                }
            }).setText( values.TEXT.like );
        this.likeCount = new g.Widget({ className: "like-count" });
        
        this.actionBar.append( this.likeButton ).append( this.likeCount );
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
    PostWidget.prototype.setLikes = function(likes, meLikes) {
        var likesLen = likes.length;
        this.likeCount.setText( likesLen + " " + (likesLen > 1 ? values.TEXT.likes : values.TEXT.like) );
        if( meLikes ) {
            this.likeButton.setText( values.TEXT.liked );
        }
        return this;
    };
    PostWidget.prototype.setEditable = function() {
        var _this = this;
        this.actionBar.append( 
            new g.Button({ 
                className: "delete",
                onclick: function() {
                    _this.delete();
                }
            }).setText( values.TEXT.del )
        );
    };
    PostWidget.prototype.delete = function() {
        if( !confirm( values.TEXT.wannaDelete ) ) return;
        var _this = this;
        g.log( values.API.deleteStory + _this.__id );
        g.postAjax( values.API.deleteStory + _this.__id ).done(function() {
            _this.element.remove();
        });
    };
    
    var newPost = function(data) {
        console.log(data);
        var user = data.user;
        var time = new Date(data.timestamp);
        var post = new PostWidget({
                className: "post"
            })
            .setPictureUrl( data.imageurl )
            .setUsername( user.username )
            .setContent( data.content )
            .setTime( time.getHours() + ":" + time.getMinutes() )
            .setMedia( data.mediaurl )
            .setLikes( data.likes, data.melikes );
        post.__id = data.storyid;
        
        if( user.me ) {
            post.setEditable();
        }
        
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