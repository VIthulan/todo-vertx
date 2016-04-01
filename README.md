# Todo-vertx
This project builds todo apis using Vert.x with the following specifications of [ToDo backend](http://todobackend.com/).

## Installation

1. First install Mongo db
2. Start Mongo db service
3. Make sure Mongodb service is running in port 27017
3. Clone the project
4. Run `mvn clean package`
5. Direct your terminal to `/todo-vertx/target`
6. Run `java -jar todo.vertx-1.0-SNAPSHOT-fat.jar`

Now your server will be started in `http://localhost:8080`

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
