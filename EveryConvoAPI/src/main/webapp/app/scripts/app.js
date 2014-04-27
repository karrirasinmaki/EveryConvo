define(
["lib/values", "lib/guda", "widgets/userWidget", "widgets/contactWidget", "widgets/menuWidget", "widgets/newStoryWidget", "widgets/newGroupWidget", "feed", "profile", "messages"], 
function(values, g, userWidget, contactWidget, menuWidget, newStoryWidget, newGroupWidget, feed, profile, messages) {
    
    window.EveryConvo = {};
    
    var user = {};
    
    var main = new g.Widget({ id: "main" });
    var views = {
        feedView: new feed.FeedView(),
        profileView: new profile.ProfileView(),
        messagesView: new messages.MessagesView()
    };
    views.profileView.currentProfilePath = undefined;
    
    var showView = function(view) {
        for( var k in views ) {
            if( !views.hasOwnProperty(k) ) continue;
            var v = views[k];
            if( k !== view ) v.hide( g.Widget.ANIM.fadeOut );
            else v.show( g.Widget.ANIM.fadeIn );
        }
    };
    var changeView = function() {
        var originalPath = location.hash.substring( 2 );
        var pathParts = originalPath.split("/");
        var path = pathParts[0];
        switch(path) {
            case "":
            case values.VIEW.feed:
                showView( "feedView" );
                break;
            default:
                showView( "profileView" );
                if( views.profileView.currentProfilePath != originalPath ) {
                    if( path == "g" )
                        views.profileView.loadGroup( pathParts[1] );
                    else 
                        views.profileView.loadPerson( path );
                    views.profileView.currentProfilePath = originalPath;
                }
                break;
        }
    };
    
    var setView = function(view) {
        location.hash = "/" + view;
        changeView();
    };
    window.EveryConvo.setView = setView;
    
    var init = function(userData) {
        
        user = userData;
        window.EveryConvo.user = user;
        
        var page = new g.View({
            id: "app"
        });

        var contacts = new g.SidebarWidget({
            id: "contacts",
            className: "right-pull"
        });
        var contactsArea = new g.Widget({ className: "list" });
        var updateUsersList = function(query) {
            userWidget.loadAllUsers(function(users) {
                for(var i=0, l=users.length; i<l; ++i) {
                    var u = users[i];
                    contactsArea.append(
                        new contactWidget.ContactWidget({
                            className: "contact",
                            onclick: function() {
                                setView( this._g.getUserName() );
                            }
                        })
                        .setPictureUrl( u.imageurl )
                        .setFullName( u.firstname + " " + u.lastname )
                        .setUserName( u.username )
                    );
                }
            }, query );
        };
        
        var contactsSearchInput = new g.Input({ 
            className: "search w-all",
            placeholder: "Search people"
        });
        contacts.append( 
            new g.Form({
                afterSubmit: function() {
                    contactsArea.empty();
                    updateUsersList( contactsSearchInput.element.value );
                }
            }).append( 
                new g.Group().append( contactsSearchInput, 100 ).append( new g.Button({ className: "search-button", type: "submit" }).setText( ">>" ) )
            )
        ).append( contactsArea );

        
        var groups = new g.SidebarWidget({
            id: "groups",
            className: "right-pull"
        });
        var groupsArea = new g.Widget({ className: "list" });
        var updateGroupsList = function(query) {
            userWidget.loadAllGroups(function(users) {
                for(var i=0, l=users.length; i<l; ++i) {
                    var u = users[i];
                    groupsArea.append(
                        new contactWidget.ContactWidget({
                            className: "contact",
                            onclick: function() {
                                setView( "g/" + this._g.getUserName() );
                            }
                        })
                        .setPictureUrl( u.imageurl )
                        .setFullName( u.fullname )
                        .setUserName( u.username )
                    );
                }
            }, query );
        };
        
        var groupsSearchInput = new g.Input({ 
            className: "search w-all",
            placeholder: "Search groups"
        });
        groups.append( 
            new g.Form({
                afterSubmit: function() {
                    groupsArea.empty();
                    updateGroupsList( groupsSearchInput.element.value );
                }
            }).append( 
                new g.Group().append( groupsSearchInput, 100 ).append( new g.Button({ className: "search-button", type: "submit" }).setText( ">>" ) )
            )
        ).append( groupsArea );
        
            
        
        menuWidget.menu.user.setPictureUrl( user.imageurl ).setFullName( user.username ).setUserName( user.username ).element.onclick = function() {
            setView( user.username );
        };
        menuWidget.menu.profile.element.onclick = function() {
            setView( user.username );
        };
        menuWidget.menu.people.element.onclick = function() {
            contacts.toggleClass( "open" );
            groups.removeClass( "open" );
        };
        menuWidget.menu.groups.element.onclick = function() {
            groups.toggleClass( "open" );
            contacts.removeClass( "open" );
        };
        menuWidget.menu.feed.element.onclick = function() {
            setView( values.VIEW.feed );
        };
        menuWidget.menu.newGroup.element.onclick = function() {
            newGroupWidget.newGroup.toggle();
        };
        menuWidget.menu.post.element.onclick = function() {
            newStoryWidget.newStory.toggle();
        };
        menuWidget.menu.logout.element.onclick = function() {
            g.postAjax(values.API.logout).done(function() {
                location.reload();
            });
        };
        
        views.feedView.load( null, true );
        views.feedView.title.setText( "Feed" );
        
        main.append( views.feedView ).append( views.profileView ).append( views.messagesView );

        page.append( contacts );
        page.append( groups );
        page.append( newGroupWidget.newGroup );
        page.append( newStoryWidget.newStory );
        page.append( main );
        page.append( menuWidget.menu );
        
        changeView();
        updateUsersList();
        updateGroupsList();
    };
    
    return {
        init: init,
        setView: setView
    };
    
});