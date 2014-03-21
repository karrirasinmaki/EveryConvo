define(function() {
    var baseUrl = "http://localhost:8080/EveryConvoAPI/";
        
    var API = {
        baseUrl: baseUrl,
        story: baseUrl + "story",
        user: baseUrl + "user",
        users: baseUrl + "users",
        login: baseUrl + "login",
        logout: baseUrl + "logout",
        createUser: baseUrl + "create-user"
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