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
        createUser: baseUrl + slash + "create-user",
        del: baseUrl + slash + "delete",
        status: {
            error: "error",
            ok: "ok"
        }
    };
    API.deleteStory = API.del + "?type=story&id=";
    
    var VIEW = {
        feed: "feed"
    };
    
    var CLASS = {
        editing: "editing"
    };
    
    var TEXT = {
        edit: "Edit",
        save: "Save",
        cancel: "Cancel",
        del: "Delete",
        like: "like",
        likes: "likes",
        liked: "liked",
        loadMore: "load more",
        wannaDelete: "Do you really want to delete that item?",
        loginError: "Error with login. Make sure you used correct pair of username and password",
        registerError: "Error with registration. Make sure you fill every field or try with another username"
    };
    
    return {
        API: API,
        VIEW: VIEW,
        CLASS: CLASS,
        TEXT: TEXT
    };
});