define(function() {
    var baseUrl = "http://localhost:8080/EveryConvoAPI/";
        
    var API = {
        baseUrl: baseUrl,
        story: baseUrl + "story",
        user: baseUrl + "user",
        login: baseUrl + "login",
        logout: baseUrl + "logout",
        createUser: baseUrl + "create-user"
    };
    
    return {
        API: API
    }
});