define(["lib/guda", "lib/values", "feed"], function(g, values, feed) {
    
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
        }
        else {
            this.editButton.setText( values.TEXT.cancel );
            this.saveButton.show();
        }
        
        this.websiteurl.element.contentEditable = !editMode;
        this.location.element.contentEditable = !editMode;
        this.description.element.contentEditable = !editMode;
        
        this.sideView.toggleClass( values.CLASS.editing );
    };
    ProfileView.prototype.fillInfo = function(user) {
        var _this = this;
        this.title.setText( user.username );
        
        this.picture = new g.MediaWidget({
            className: "picture",
            mediaURL: "http://mjt.nysv.org/scratch/tissit_xxl.jpg"
        });
        this.websiteurl = new g.Widget({ className: "websiteurl"});
        this.location = new g.Widget({ className: "location" });
        this.description = new g.Widget({ className: "description"});
        
        this.sideView.append( this.picture );
        
        if( user.me ) {
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
            
            this.sideView.append( this.editButton ).append( this.saveButton );
        }
        
        this.sideView.append( this.websiteurl ).append( this.location )
            .append( this.description );
    };
    ProfileView.prototype.load = function(userName) {
        this._load( userName, true );
        var _this = this;
        g.getAjax( values.API.user +"/"+ userName ).done(function(data) {
            _this.user = JSON.parse(data);
            _this.fillInfo( _this.user );
        });
    };
    
    return {
        ProfileView: ProfileView
    };
    
});