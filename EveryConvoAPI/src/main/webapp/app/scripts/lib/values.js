define(function() {
    var baseUrl = "http://localhost:8080/EveryConvoAPI";
    var slash = "/";
        
    var API = {
        baseUrl: baseUrl,
        upload: baseUrl + slash + "upload",
        story: baseUrl + slash + "story",
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