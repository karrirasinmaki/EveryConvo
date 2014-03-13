EveryConvo
==========

Messaging platform/app with open API

Build
-----
Maven is required to build EveryConvo API.  
`mvn install`  
`mvn jetty:run`  
API server running at `localhost:8080/EveryConvoAPI`

API calls
---------
**Server admins only:**
- /install
  - // installs database structure to server
- /destroy
  - // drops tables from database

**Public API:**
- /create-user POST
  - // creates new user
  - username
  - password
- /login POST
  - // logs user in
  - username
  - password
- /message POST
  - // sends message
  - to (id)
  - content
- /messages GET
  - // list user's messages

Tech talking
------------
API
- Java Servelets
- MySQL database

Client
- HTML, CSS, JavaScript
- Ajax calls to API
