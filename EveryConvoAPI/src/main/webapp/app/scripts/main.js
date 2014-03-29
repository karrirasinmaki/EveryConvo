require(
["widgets/userWidget", "app", "login"], 
function(userWidget, app, login) {
    
    userWidget.login()
        .success(function(user) {
            app.init( user );
        })
        .error(function(data) {
            login.init();
        });
    
});