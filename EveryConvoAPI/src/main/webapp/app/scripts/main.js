require(
["widgets/userWidget", "app", "login"], 
function(userWidget, app, login) {
    
    userWidget.login().success(function(data) {
        console.log(data);
        app.init();
    })
    .error(function(data) {
        console.log(data);
        login.init();
    });
    
});