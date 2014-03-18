require(["guda", "widgets/contactWidget", "widgets/postWidget"], function(g, contactWidget, postWidget) {
    
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
    
    
    var feed = new g.Widget({
        id: "feed"
    });
    postWidget.loadPosts( feed );
    
    var NewStoryWidget = function() {
        this.init({
            method: "post",
            action: API.story
        });
        
        this.title = new g.Widget({ className: "title", textContent: "Share your thoughts" });
        this.content = new g.TextArea({ className: "content", name: "content" });
        this.mediaurl = new g.Input({ className: "mediaurl", name: "mediaurl" });
        this.submit = new g.Button({ className: "submit", textContent: "Send" });
        
        this.append( this.title ).append( this.content ).append( this.mediaurl ).append( this.submit );
    };
    NewStoryWidget.prototype = new g.Form;
    
    var newStory = new NewStoryWidget();
    
    page.append( contacts );
    page.append( newStory );
    page.append( feed );
    
});