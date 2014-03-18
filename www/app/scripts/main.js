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
    
    for(var i=0; i<20; ++i) {
        var data = {
            content: "Once you are finished doing development and want to deploy your code for your end users, you can use the optimizer to combine the JavaScript files together and minify it. In the example above, it can combine main.js and helper/util.js into one file and minify the result.",
            timestamp: "16:" + (i<10?"0"+i:i)
        };
        feed.append( postWidget.newPost(data) );
    }
    postWidget.loadPosts();
    
    var NewStoryWidget = function() {
        this.init({
            method: "post",
            action: API.story
        });
        
        this.title = new g.Widget({ className: "title", textContent: "Share your thoughts" });
        this.textarea = new g.TextArea({ className: "textarea", name: "content" });
        this.submit = new g.Button({ className: "submit", textContent: "Send" });
        
        this.append( this.title ).append( this.textarea ).append( this.submit );
    };
    NewStoryWidget.prototype = new g.Form;
    
    var newStory = new NewStoryWidget();
    
    page.append( contacts );
    page.append( newStory );
    page.append( feed );
    
});