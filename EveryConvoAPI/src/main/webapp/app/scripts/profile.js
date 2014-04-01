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
        var _this = this;
        this.title.setText( user.username );
        
        this.picture = new g.MediaWidget({
            className: "picture",
            mediaURL: values.API.baseUrl + user.imageurl || "/default-profile-pic.png"
        });
        this.location = new EditableWidget({ className: "location", name: "location" }).setText( user.location );
        this.websiteurl = new EditableWidget({ className: "websiteurl", name: "websiteurl", href: user.websiteurl }, "a").setText( user.websiteurl );
        this.websiteurl.element.style.display = "block";
        this.description = new EditableWidget({ className: "description", name: "description" }).setText( user.description );
        
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
                            // TODO: File upload to server
                        };
                        reader.readAsDataURL( file );
                        g.AJAXSubmit( _this.pictureForm.element ).done(function(data) {
                            g.log( data );
                            var data = JSON.parse( data );
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
            .append( this.description );
    };
    ProfileView.prototype.load = function(userName) {
        this._load( userName, true );
        var _this = this;
        g.getAjax( values.API.user +"/"+ userName ).done(function(data) {
            _this.user = JSON.parse(data).data;
            _this.fillInfo( _this.user );
        });
    };
    
    return {
        ProfileView: ProfileView
    };
    
});