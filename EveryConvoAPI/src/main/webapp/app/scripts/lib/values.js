define(function() {
    var baseUrl = location.href.substring(0, location.href.indexOf("EveryConvoAPI") + 13);
    var slash = "/";
        
    var API = {
        baseUrl: baseUrl,
        upload: baseUrl + slash + "upload",
        story: baseUrl + slash + "story",
        stories: baseUrl + slash + "stories",
        user: baseUrl + slash + "user",
        users: baseUrl + slash + "users",
        login: baseUrl + slash + "login",
        logout: baseUrl + slash + "logout",
        createUser: baseUrl + slash + "create-user"
    };
    
    var VIEW = {
        feed: "feed"
    };
    
    var CLASS = {
        editing: "editing"
    };
    
    var TEXT = {
        edit: "Edit",
        save: "Save",
        cancel: "Cancel"
    };
    
    return {
        API: API,
        VIEW: VIEW,
        CLASS: CLASS,
        TEXT: TEXT
    };
});