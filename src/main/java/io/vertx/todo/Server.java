package io.vertx.todo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.util.DBclient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main verticle that will create the server and handles REST calls.
 */
public class Server extends AbstractVerticle {

    private static final Log log = LogFactory.getLog(Server.class);

    private DBclient dBclient;
    private MongoClient mongoClient;
    private final String COLLECTION_NAME = "tasks";             //Tasks will be saved in this collection
    private final String REMOVED_COLLECTION = "rmvd_task";      //Removed tasks from the 'tasks' collection will be saved here

    @Override
    public void start(Future<Void> fut) throws Exception {
        dBclient = new DBclient();
        mongoClient = dBclient.init(vertx);                         //Initiating mongo database

        Router router = Router.router(vertx);                       //Create the router object
        /*router.route("/").handler(routingContext -> {               //Binding the message to / directory
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Welcome to my Todo API from Vertx</h1><br>" +
                            "<p>Please refer to : https://github.com/VIthulan/todo-vertx </p><br>" +
                            "<p>-Vithulan MV</p>");
        });*/

        /**
         * Handling rest calls with corresponding methods
         */
        router.get("/api/tasks").handler(this::getAllTasks);
        router.get("/api/tasks/:id").handler(this::getTask);
        router.route("/api/tasks*").handler(BodyHandler.create());
        router.route().handler((routingContext) -> {
            routingContext.response()
                    .putHeader("Access-Control-Allow-Origin", "*")
                    .putHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PATCH")
                    .putHeader("Access-Control-Allow-Headers", "x-requested-with, origin, content-type, accept")
                    .putHeader("Access-Control-Max-Age", "3600");
            routingContext.next();
        });
        router.post("/api/tasks").handler(this::addTask);
        router.delete("/api/tasks/:id").handler(this::deleteTask);
        router.patch("/api/tasks/:id").handler(this::completed);
        router.put("/api/tasks/:id").handler(this::completed);
        router.options("/api/tasks").handler((handler) -> handler.response().end());
        router.options("/api/tasks/:id").handler((handler) -> handler.response().end());

        /**
         * Creating the HTTP server at port 8080
         */
        /*vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );*/
        vertx.createHttpServer().requestHandler(router::accept)
                .listen(
                        Integer.getInteger("http.port"), System.getProperty("http.address", "0.0.0.0"));
    }

    /**
     * It will retrieve all the data from the database to the server
     * @param routingContext context of request from the server
     */
    private void getAllTasks(RoutingContext routingContext) {
        JsonObject query = new JsonObject();
        mongoClient.find(COLLECTION_NAME, query, res -> {
            if (res.succeeded()) {
                JsonArray jsonArray = new JsonArray();
                log.info("Getting all data");
                for (JsonObject json : res.result()) {
                    jsonArray.add(json);
                    log.info(jsonArray.encodePrettily());
                }
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(jsonArray));

            } else {
                routingContext.response().setStatusCode(500).end();
                res.cause().printStackTrace();
                log.error("Error while retrieving all data");
                log.error(res.cause());
            }
        });

    }

    private void getTask (RoutingContext routingContext){
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(500).end();
        } else {
            dBclient.getTask(mongoClient,id,routingContext);

        }
    }

    /**
     * It will add data into the database by calling DBclient function
     * @param routingContext context of request from the server
     */
    private void addTask(RoutingContext routingContext) {
        Tasks task = Json.decodeValue(routingContext.getBodyAsString(),
                Tasks.class);
        dBclient.addData(mongoClient, task, routingContext);
    }

    /**
     * It will delete a particular document for a given id by calling DBclient method
     * @param routingContext context of request from the server
     */
    private void deleteTask(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            dBclient.removeData(mongoClient, id,routingContext);
        }
    }

    /**
     * It will toggle the @param completed for a given _id by calling DBclient method
     * @param routingContext context of request from the server
     */
    private void completed(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null ) {
            routingContext.response().setStatusCode(400).end();
        } else {
            dBclient.modifyData(mongoClient, id, routingContext);
        }
    }
}
