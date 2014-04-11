define(["lib/guda", "lib/values", "feed"], function(g, values, feed) {
    
    var EditableWidget = function(params, tagName) {
        this.init( params, tagName );
    };
    EditableWidget.prototype = new g.Widget;
    EditableWidget.prototype.setEditMode = function(editMode, returnOriginalValue) {
        this.element.contentEditable = editMode;
        if( !editMode && returnOriginalValue ) {
            this.element.textContent = this._originalValue;
        }
        else if( editMode ) this._originalValue = this.element.textContent;
    };
    
    var ProfileView = function() {
        this.init({ className: "inner" });
        this.create();
        this.sideView.addClass( "user-info" );
    };
    ProfileView.prototype = new feed.FeedView;
    ProfileView.prototype.toggleEditMode = function() {
        var editMode = this.sideView.hasClass( values.CLASS.editing );
        
        if( editMode ) {
            this.editButton.setText( values.TEXT.edit );
            this.saveButton.hide();
            this.pictureInput.hide();
        }
        else {
            this.editButton.setText( values.TEXT.cancel );
            this.saveButton.show();
            this.pictureInput.show();
        }
        
        this.websiteurl.setEditMode( !editMode, true );
        this.location.setEditMode( !editMode, true );
        this.description.setEditMode( !editMode, true );
        
        this.sideView.toggleClass( values.CLASS.editing );
    };
    ProfileView.prototype.save = function() {
        var serialized = g.serialize( this.sideView.element );
        g.postAjax(values.API.user +"?"+ serialized).done(function() {
            location.reload();
        });
    };
    ProfileView.prototype.fillInfo = function(user) {
        this.user = user;
        var _this = this;
        
        var fullName = user.fullname || user.firstname + " " + user.lastname;
        this.title.setHTML( fullName + "<small>" + user.username + "</small>" );
        
        var imageUrl = user.imageurl;
        if( !imageUrl ) imageUrl = "/u/default-profile-pic.png";
        this.picture = new g.MediaWidget({
            className: "picture",
            mediaURL: values.API.baseUrl + imageUrl
        });
        
        this.location = new EditableWidget({ className: "location", name: "location" }).setText( user.location );
        this.websiteurl = new EditableWidget({ className: "websiteurl", name: "websiteurl", href: user.websiteurl }, "a").setText( user.websiteurl );
        this.websiteurl.element.style.display = "block";
        this.description = new EditableWidget({ className: "description", name: "description" }).setText( user.description );
        
        this.followButton = new g.Button({
            className: "follow",
            onclick: function() {
                g.postAjax( _this.followButton.followAction ).done(function(data) {
                    _this.followButton.setFollow( !user.follows );
                });
            }
        });
        this.followButton.followAction = values.API.follow(_this.user.username);
        this.followButton.setFollow = function(follow) {
            var text = follow ? values.TEXT.unfollow : values.TEXT.follow;
            this.setText( text );
            this.followAction = values.API[text](_this.user.username);
        };
        this.followButton.setFollow( user.follows );
        
        this.sideView.append( this.picture );
        
        if( user.me ) {
            this.pictureForm = 
                new g.Form({
                    method: "post",
                    action: values.API.upload,
                    enctype: "multipart/form-data"
                });
            this.pictureInput = 
                new g.Input({
                    type: "file",
                    name: "file",
                    accept: "image/*",
                    onchange: function() {
                        var reader = new FileReader();
                        var file = this.files[0];
                        reader.onload = function(evt) {
                            _this.picture.setMediaURL( evt.target.result );
                        };
                        reader.readAsDataURL( file );
                        g.AJAXSubmit( _this.pictureForm.element ).done(function(data) {
                            var data = JSON.parse( data );
                            _this.picture.setMediaURL( values.API.baseUrl + data.fileurl );
                            _this.sideView.append( 
                                new g.Input({
                                    type: "hidden",
                                    name: "imageurl",
                                    value: data.fileurl
                                })
                            );
                        });
                    }
                }).hide();
            this.pictureForm.append( this.pictureInput );
            this.editButton = 
                new g.Widget({ 
                    className: "edit-button",
                    onclick: function() {
                        _this.toggleEditMode();
                    }
                }, "button")
                .setText( values.TEXT.edit );
            this.saveButton = 
                new g.Widget({ 
                    className: "edit-button",
                    onclick: function() {
                        _this.save();
                    }
                }, "button")
                .setText( values.TEXT.save )
                .hide();
            
            this.sideView.append( this.pictureForm ).append( this.editButton ).append( this.saveButton );
        }
        
        this.sideView.append( this.location ).append( this.websiteurl )
            .append( this.description ).append( this.followButton );
    };
    ProfileView.prototype.load = function(userName, userType) {
        this._load( userName, true );
        var _this = this;
        g.getAjax( values.API[userType] +"/"+ userName ).done(function(data) {
            _this.user = JSON.parse(data).data;
            _this.fillInfo( _this.user );
        });
    };
    ProfileView.prototype.loadPerson = function(userName) {
        this.load( userName, "person" );
    };
    ProfileView.prototype.loadGroup = function(userName) {
        this.load( userName, "group" );
    };
    
    return {
        ProfileView: ProfileView
    };
    
});