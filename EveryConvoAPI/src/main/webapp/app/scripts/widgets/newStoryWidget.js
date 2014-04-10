define(["../lib/guda", "../lib/values"], function(g, values) {

    var NewStoryForm = function() {
        var _this = this;
        this.init({
            method: "post",
            action: values.API.story,
            afterSubmit: function(data) { location.reload(); }
        });

        this.title = new g.Widget({ className: "title", textContent: "Share your thoughts" });
        this.content = new g.TextArea({ className: "content", name: "content" });
        this.mediaurl = new g.Input({ className: "mediaurl", name: "mediaurl", placeholder: "media url" });
        this.mediaUploadForm = new g.Form({ action: values.API.upload, method: "post", enctype: "multipart/form-data" });
        this.mediaUploadForm.append( 
            new g.Input({ 
                name: "file", 
                type: "file",
                onchange: function() {
                    g.AJAXSubmit( _this.mediaUploadForm.element ).done(function(data) {
                        var data = JSON.parse( data );
                        _this.setMediaUrl( data.fileurl );
                    });
                }
            })
        );
        this.submit = new g.Button({ className: "submit", textContent: "Send" });
        this.append( this.title ).append( this.content ).append( this.mediaurl )
            .append( this.mediaUploadForm ).append( this.submit );
    };
    NewStoryForm.prototype = new g.Form;
    NewStoryForm.prototype.setMediaUrl = function(mediaUrl) {
        mediaUrl = values.API.baseUrl + mediaUrl;
        this.mediaurl.element.value = mediaUrl;
        this.mediaPreview = new g.MediaWidget({ className: "media-preview", mediaURL: mediaUrl });
        this.append( this.mediaPreview );
    };
    

    var newStory = new g.Widget({ 
        id: "new-story",
        className: "bottom-pull"
    });
    newStory.append( new NewStoryForm() );
    newStory.hide();
    
    
    return {
        NewStoryForm: NewStoryForm,
        newStory: newStory
    };
    
});