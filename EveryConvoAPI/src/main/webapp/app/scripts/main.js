require(["guda", "widgets/contactWidget", "widgets/postWidget", "widgets/menuWidget"], function(g, contactWidget, postWidget, menuWidget) {
    
    var API = {
        story: "http://localhost:8080/EveryConvoAPI/story"
    };
    
    var page = new g.View({
        id: "app"
    });
    
    var contacts = new g.SidebarWidget({
        id: "contacts"
    });
    
    for(var i=0; i<20; ++i) {
        contacts.append(
            new contactWidget.ContactWidget({
                className: "contact"
            })
            .setFullName( "Karri Rasinmäki" )
            .setUserName( "karrirasinmaki" )
        );
    }
    
    
    var NewStoryForm = function() {
        this.init({
            method: "post",
            action: API.story
        });
        
        this.title = new g.Widget({ className: "title", textContent: "Share your thoughts" });
        this.content = new g.TextArea({ className: "content", name: "content" });
        this.mediaurl = new g.Input({ className: "mediaurl", name: "mediaurl", placeholder: "media url" });
        this.submit = new g.Button({ className: "submit", textContent: "Send" });
        
        this.append( this.title ).append( this.content ).append( this.mediaurl ).append( this.submit );
    };
    NewStoryForm.prototype = new g.Form;
    
    
    var newStory = new g.Widget({ 
        id: "new-story"
    });
    newStory.append( new NewStoryForm() );
    newStory.hide();       
    
            
    var menu = new g.SidebarWidget({
        id: "menu"
    });
    var menuFirstHalf = new g.Widget({ className: "first-half" });
    var menuSecondHalf = new g.Widget({ className: "second-half" });
    menuFirstHalf.append( new menuWidget.MenuElement().setText( "Profile" ) )
                .append( new menuWidget.MenuElement().setText( "Messages" ) )
                .append( new menuWidget.MenuElement().setText( "Feed" ) );
    menuSecondHalf.append( 
        new menuWidget.MenuElement({
            onclick: function() {
                newStory.toggle();
            }
        }).setText( "Post" )
    )
    .append( new menuWidget.MenuElement().setText( "Settings" ) );
            
    menu.append( menuFirstHalf ).append( menuSecondHalf );
    
    
    var feed = new g.Widget({
        id: "feed"
    });
    postWidget.loadPosts( feed );
            
    
    page.append( contacts );
    page.append( newStory );
    page.append( feed );
    page.append( menu );
    
});