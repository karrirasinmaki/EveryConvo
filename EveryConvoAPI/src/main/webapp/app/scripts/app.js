define(
["lib/values", "lib/guda", "widgets/userWidget", "widgets/contactWidget", "widgets/menuWidget", "widgets/newStoryWidget", "feed", "profile"], 
function(values, g, userWidget, contactWidget, menuWidget, newStoryWidget, feed, profile) {
    
    var user = {};
    
    var main = new g.Widget({ id: "main" });
    
    var changeView = function() {
        var path = location.hash.substr(2);
        main.element.innerHTML = "";
        switch(path) {
            case "":
            case values.VIEW.feed:
                var feedView = new feed.FeedView();
                feedView.load( null, true );
                feedView.title.setText( "Feed" );
                main.append( feedView );
                break;
            default:
                var profileView = new profile.ProfileView();
                profileView.load( path );
                main.append( profileView );
                break;
        }
    };
    
    var setView = function(view) {
        location.hash = "/" + view;
        changeView();
    };
    
    var init = function(userData) {
        
        user = userData;
        
        var page = new g.View({
            id: "app"
        });

        var contacts = new g.SidebarWidget({
            id: "contacts"
        });

        userWidget.loadAllUsers(function(users) {
            console.log(users);
            for(var i=0, l=users.length; i<l; ++i) {
                var u = users[i];
                contacts.append(
                    new contactWidget.ContactWidget({
                        className: "contact",
                        onclick: function() {
                            setView( this._g.getUserName() );
                        }
                    })
                    .setFullName( u.username )
                    .setUserName( u.username )
                );
            }
        });
        
        menuWidget.menu.user.setFullName( user.username ).setUserName( user.username );
        menuWidget.menu.people.element.onclick = function() {
            contacts.toggleClass( "open" );
        };
        menuWidget.menu.feed.element.onclick = function() {
            setView( values.VIEW.feed );
        };
        menuWidget.menu.post.element.onclick = function() {
            newStoryWidget.newStory.toggle();
        };
        menuWidget.menu.logout.element.onclick = function() {
            g.postAjax(values.API.logout).done(function() {
                location.reload();
            });
        };

        page.append( contacts );
        page.append( newStoryWidget.newStory );
        page.append( main );
        page.append( menuWidget.menu );
        
        changeView();
    };
    
    return {
        init: init
    };
    
});