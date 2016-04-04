# Todo-vertx
This project builds todo apis using Vert.x with the following specifications of [Todo-backend](http://todobackend.com/).

Project is merged in Todo-backend Site!! Please checkout [Todo-vertx from Vithulan](http://www.todobackend.com/client/index.html?https://todo-vertx.herokuapp.com/api/tasks).

##Deploying the application in Heroku
1. Clone the [heroku-app branch](https://github.com/VIthulan/todo-vertx/tree/heroku-app)
2. Open the terminal 
3. heroku create (Assumption : Heroku environment is alreay been set) 
4. git push heroku master

## Project is live!
Please check out [https://todo-vertx.herokuapp.com/api/tasks](https://todo-vertx.herokuapp.com/api/tasks)

###Tips
* Sometimes you may not able to push to the heroku master, in that case create a git remote heroku for git repo created in step 3
* After deployemnt successfully completed, You can open the application in the URL that shown in step 3
* If anything doesn't work please check the log files in your application page and let me know.

##Calling APIs

1. Open a Rest client in your browser
2. Send a `POST` request to `http://{$YOUR_HEROKU_APP_URL}/api/tasks` with the following JSON context
```
{
"task":"Doing GSoC 2016",
"completed":"true"
}
```
3. View the sent request in `http://{$YOUR_HEROKU_APP_URL}/api/tasks` in your browser.
4. Like vice you can Add tasks, Delete tasks, Modify Tasks and View tasks.

##Available APIS
* `GET` `http://{$YOUR_HEROKU_APP_URL}/api/tasks` : Get all the Tasks stored in DB
* `POST` `http://{$YOUR_HEROKU_APP_URL}/api/tasks` : Add a new task
* `PUT` `http://{$YOUR_HEROKU_APP_URL}/api/tasks/:id` : Update a task whether it is done or not
* `DELETE` `http://{$YOUR_HEROKU_APP_URL}/api/tasks/:id` : Delete a task

##Simple Architecture of todo-vertx
<img src = "http://i68.tinypic.com/303er5x.png">

##Database schema
<img src = "http://i67.tinypic.com/1yoen5.png">

##Contact 
The best way for potential contributors to contact is [posting a message in my issue tracker](https://github.com/VIthulan/todo-vertx/issues/new) 

Feel free to contribute for any bugs or for improvemet of a new feature

-Vithulan MV
