define(["../lib/guda", "../lib/values"], function(g, values) {
    
    var CounterWidget = function(params, tagName) {
        tagName = tagName || "span";
        this.counterTextSingular = " " + params.counterTextSingular || "";
        this.counterTextPlural = " " + params.counterTextPlural || "";
        this.init( params, tagName );
    };
    CounterWidget.prototype = new g.Widget;
    CounterWidget.prototype.setCount = function(count) {
        this.setText( count + (Math.abs(count) == 1 ? this.counterTextSingular : this.counterTextPlural) );
        return this;
    };
    CounterWidget.prototype.addCount = function(count) {
        this.setCount( parseInt(this.getText()) + count );
        return this;
    };
    
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
    PostWidget.prototype.createActionBar = function() {
        var _this = this;
        this.likeButton = 
            new g.Button({
                className: "like",
                onclick: function() {
                    _this.like();
                }
            }).setText( values.TEXT.like );
        this.likeCount = new CounterWidget({ 
            className: "like-count", 
            counterTextSingular: values.TEXT.like,
            counterTextPlural: values.TEXT.likes
        });
        
        this.actionBar.append( this.likeButton ).append( this.likeCount );
    };
    PostWidget.prototype.like = function() {
        var _this = this;
        this.likeCount.addCount( this.userLikes ? -1 : 1 );
        this.setUserLikes( !this.userLikes );
        g.postAjax(values.API.story + "/" + this.__id + "?like=" + this.userLikes ).done(function() {
        });
    };
    PostWidget.prototype.setUserLikes = function(likeOrNot) {
        this.userLikes = likeOrNot;
        this.likeButton.setText( this.userLikes ? values.TEXT.liked : values.TEXT.like );
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
        this.likeCount.setCount( likes.length );
        this.setUserLikes( meLikes );
        return this;
    };
    PostWidget.prototype.setEditable = function() {
        var _this = this;
        this.append( 
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
    
    return {
        newPost: newPost
    };
    
});