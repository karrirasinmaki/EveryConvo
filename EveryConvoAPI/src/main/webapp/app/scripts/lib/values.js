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
        person: baseUrl + slash + "person",
        people: baseUrl + slash + "people",
        group: baseUrl + slash + "group",
        groups: baseUrl + slash + "groups",
        message: baseUrl + slash + "message",
        messages: baseUrl + slash + "messages",
        login: baseUrl + slash + "login",
        logout: baseUrl + slash + "logout",
        createUser: baseUrl + slash + "create-user",
        createPerson: baseUrl + slash + "create-person",
        createGroup: baseUrl + slash + "create-group",
        del: baseUrl + slash + "delete",
        status: {
            error: "error",
            ok: "ok"
        }
    };
    API.deleteStory = API.del + "?type=story&id=";
    API.follow = function(userName) { return API.user + "/" + userName + "?follow"; }
    API.unfollow = function(userName) { return API.user + "/" + userName + "?unfollow"; }
    
    var VIEW = {
        feed: "feed",
        messages: "messages"
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
        follow: "follow",
        unfollow: "unfollow",
        message: "message",
        loadMore: "load more",
        wannaDelete: "Do you really want to delete that item?",
        loginError: "Error with login. Make sure you used correct pair of username and password",
        registerError: "Error with registration. Make sure you fill every field or try with another username"
    };
    
    var STATUS = {
        error: "error"
    };
    
    return {
        API: API,
        VIEW: VIEW,
        CLASS: CLASS,
        TEXT: TEXT,
        STATUS: STATUS
    };
});