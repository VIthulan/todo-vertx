# Todo-vertx
This project builds todo apis using Vert.x with the following specifications of [Todo-backend](http://todobackend.com/).

## Installation

1. First install Mongo db
2. Start Mongo db service
3. Make sure Mongodb service is running in port 27017
3. Clone the project : master branch
4. Run `mvn clean package`
5. Direct your terminal to `/todo-vertx/target`
6. Run `java -jar todo.vertx-1.0-SNAPSHOT-fat.jar`

Now your server will be started in `http://localhost:8080`

##Deploying the application in Heroku
1. Clone the [heroku-app branch](https://github.com/VIthulan/todo-vertx/tree/heroku-app)
2. Open the terminal 
3. heroku create (Assumption : Heroku environment is alreay been set) 
4. git push heroku master

##Calling APIs

1. Open a Rest client in your browser
2. Send a `POST` request to `http://localhost:8080/api/tasks` with the following JSON context
```
{
"task":"Doing GSoC 2016",
"completed":"true"
}
```
3. View the sent request in `http://localhost:8080/api/tasks` in your browser.
4. Like vice you can Add tasks, Delete tasks, Modify Tasks and View tasks.

##Available APIS
* `GET` `http://localhost:8080/api/tasks` : Get all the Tasks stored in DB
* `POST` `http://localhost:8080/api/tasks` : Add a new task
* `PUT` `http://localhost:8080/api/tasks/:id` : Update a task whether it is done or not
* `DELETE` `http://localhost:8080/api/tasks/:id` : Delete a task

##Simple Architecture of todo-vertx
<img src = "http://i68.tinypic.com/303er5x.png">

##Database schema
<img src = "http://i67.tinypic.com/1yoen5.png">

##Contact 
The best way for potential contributors to contact is [posting a message in my issue tracker](https://github.com/VIthulan/todo-vertx/issues/new) 

Feel free to contribute for any bugs or for improvemet of a new feature

-Vithulan MV
