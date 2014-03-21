define(["../lib/guda", "../lib/values"], function(g, values) {

    var NewStoryForm = function() {
        this.init({
            method: "post",
            action: values.API.story,
            afterSubmit: function(data) { location.reload(); }
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
    
    
    return {
        NewStoryForm: NewStoryForm,
        newStory: newStory
    };
    
});