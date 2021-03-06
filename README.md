EveryConvo
==========

This was a university web application development -course training project.

Messaging platform/app with open API  

Build
-----
Maven is required to build EveryConvo API.  
**Note:** MySQL server required, running in localhost (default username:password, "root":"", can be changed in SQLUtils->Values class)
```
mvn install
mvn jetty:run
```
API server running at `localhost:8080/EveryConvoAPI`  
App running at `localhost:8080/EveryConvoAPI/app`  

Install
-------
When server is up and running, you have to install application's database structure and set database's username and password. Those steps are done by so:  
```
localhost:8080/EveryConvoAPI/install?username=YOUR_DB_USER&password=YOUR_DB_PASS
```
If everything went well, app created database called `everyconvo` to your MySQL and EveryConvo is ready to rock n roll.

API calls
---------
**Server admins only:**
- /install
  - // installs 'everyconvo' database structure to server
  - // requires db username & password
  - username
  - password
- /destroy
  - // destroys 'everyconvo' database
  - // requires db username & password
  - username
  - password

**Public API:**
Asterix (*) means action needs authenticated user
- /create-user POST
  - // creates new user
  - username
  - password
- /logout GET/POST *
  - // logs out current user
- /login POST
  - // logs user in
  - username
  - password
- /login GET *
  - // shows currently logged in user's status
- /user GET
  - // shows user info, if no parameter given, shows currently logged in user's status
  - /{username || userid}
- /users GET
  - // shows list of users
- /message POST *
  - // sends message
  - to (id)
  - content
- /messages GET *
  - // list user's messages
- /story POST *
  - // posts new story to current user's profile
  - title
  - content
  - mediaurl
- /stories GET
  - // shows stories posted by given users, or of all users if no parameters given
  - ?user={username || userid}(comma seperated list)
- /upload POST *
  - // upload file to server and returns uploaded file url
  - accepts "multipart/form-data"-form with single file
- /delete 
  - // delete entity
  - type // type of entity. eq. story
  - id

Tech talking
------------
API
- Java Servelets
- MySQL database

Client
- HTML, CSS, JavaScript
- Ajax calls to API
