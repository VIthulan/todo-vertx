package io.vertx.todo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;


public class Server extends AbstractVerticle {

    private static final Log log = LogFactory.getLog(Server.class);

    private Map<Integer, Tasks> TasksMap = new HashMap<>();
    private MongoClient mongoClient;

    @Override
    public void start(Future<Void> fut) throws Exception {
       createData();

        JsonObject config = Vertx.currentContext().config();

        String uri = config.getString("mongo_uri");
        if (uri == null) {
            uri = "mongodb://localhost:27017";
        }
        String db = config.getString("mongo_db");
        if (db == null) {
            db = "task_db";
        }

        JsonObject mongoconfig = new JsonObject()
                .put("connection_string", uri)
                .put("db_name", db);
        mongoClient = MongoClient.createShared(vertx, mongoconfig);


        Router router = Router.router(vertx);
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Welcome to my Todo API from Vertx</h1>");
        });

//        router.route("/assets/*").handler(StaticHandler.create("assets"));

        router.get("/api/tasks").handler(this::getAllTasks);
        router.route("/api/tasks*").handler(BodyHandler.create());
        router.post("/api/tasks").handler(this::addTask);
        router.delete("/api/tasks/:id").handler(this::deleteTask);
        router.put("/api/tasks/:id").handler(this::isDone);

        /*JsonObject taskJson = new JsonObject()
                .put("_id",111)
                .put("task", "TestingMongo")
                .put("isDone",false);

        mongoClient.save("tasks", taskJson, res -> {
            if (res.succeeded()) {
                log.info("Successfully inserted: " + res.result());
            }
        });*/

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
    }

    private void getAllTasks(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(TasksMap.values()));
    }

    private void addTask(RoutingContext routingContext) {
        Tasks task = Json.decodeValue(routingContext.getBodyAsString(),
                Tasks.class);
        TasksMap.put(task.getId(), task);

        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(task));
    }

    private void deleteTask(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            Integer idAsInteger = Integer.valueOf(id);
            TasksMap.remove(idAsInteger);
        }
        routingContext.response().setStatusCode(204).end();
    }

    private void isDone(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        JsonObject json = routingContext.getBodyAsJson();
        if (id == null || json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Tasks tasks = TasksMap.get(idAsInteger);
            if (tasks == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                tasks.setIsDone(json.getBoolean("isDone"));
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(tasks));
            }
        }
    }

    private void createData(){
        Tasks tasks = new Tasks("GSoC at VertX",true);
        /*JsonObject taskJson = new JsonObject()
                .put("_id",tasks.getId())
                .put("task", tasks.getTask())
                .put("isDone",tasks.getIsDone());*/

        /*JsonObject taskJson = new JsonObject()
                .put("_id",111)
                .put("task", "TestingMongo")
                .put("isDone",false);

        mongoClient.save("tasks", taskJson, res -> {
                    if (res.succeeded()) {
                        log.info("Successfully inserted: " + res.result());
                    }
                });*/

                TasksMap.put(tasks.getId(), tasks);
    }
}
