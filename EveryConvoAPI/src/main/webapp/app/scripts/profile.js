define(["lib/guda", "lib/values", "feed", "app", "messages"], function(g, values, feed, app, messages) {
    
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
        this.editableWidgets = [];
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
        
        for( var i=0, l=this.editableWidgets.length; i<l; ++i ) {
            this.editableWidgets[i].setEditMode( !editMode, true );
        }
        
        this.sideView.toggleClass( values.CLASS.editing );
    };
    ProfileView.prototype.save = function() {
        var serialized = g.serialize( this.sideView.element );
        g.postAjax(values.API[this.user.type] +"?"+ serialized).done(function(data) {
            data = JSON.parse(data).data;
            if( data.status == values.STATUS.error ) {
                alert( data.message );
            }
            else {
                location.reload();
            }
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
        
        this.nameWidget = new g.Widget({ className: "names" });
        this.usernameWidget = new EditableWidget({ className: "username", name: "username" }).setText( user.username );
        this.nameWidget.append( this.usernameWidget );
        this.editableWidgets.push( this.usernameWidget );
        if( user.fullname ) {
            this.fullname = new EditableWidget({ className: "fullname", name: "fullname" }).setText( user.fullname );
            this.nameWidget.append( this.fullname );
            this.editableWidgets.push( this.fullname );
        }
        else {
            this.firstname = new EditableWidget({ className: "firstname", name: "firstname" }).setText( user.firstname );
            this.lastname = new EditableWidget({ className: "lastname", name: "lastname" }).setText( user.lastname );
            this.nameWidget.append( this.firstname ).append( this.lastname );
            this.editableWidgets.push( this.firstname );
            this.editableWidgets.push( this.lastname );
        }
            
        this.location = new EditableWidget({ className: "location", name: "location" }).setText( user.location );
        this.websiteurl = new EditableWidget({ className: "websiteurl", name: "websiteurl", href: user.websiteurl }, "a").setText( user.websiteurl );
        this.websiteurl.element.style.display = "block";
        this.description = new EditableWidget({ className: "description", name: "description" }).setText( user.description );
        
        
        this.editableWidgets.push( this.location );
        this.editableWidgets.push( this.websiteurl );
        this.editableWidgets.push( this.description );
        
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
        
        this.messageButton = new g.Button({
            onclick: function() {
                EveryConvo.setView( values.VIEW.messages + "/" + _this.user.userid );
            }
        }).setText( values.TEXT.message );
        
        this.sideView.append( this.picture );
        
        this.createEdit();
        
        this.sideView.append( this.nameWidget ).append( this.location ).append( this.websiteurl )
            .append( this.description ).append( this.followButton ).append( this.messageButton );
        
        
        this.messageView = new messages.MessagesView();
        this.messageView.openConversation( this.user.userid );
        this.sideView.append( this.messageView );
    };
    ProfileView.prototype.createEdit = function() {
        var _this = this;
        if( this.user.me ) {
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
                    onclick: function(e) {
                        e.preventDefault();
                        _this.save();
                    }
                }, "button")
                .setText( values.TEXT.save )
                .hide();
            
            this.sideView.append( this.pictureForm ).append( this.editButton ).append( this.saveButton );
        }
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
        console.log("loading group");
        this.load( userName, "group" );
    };
    
    return {
        ProfileView: ProfileView
    };
    
});