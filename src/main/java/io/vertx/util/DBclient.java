package io.vertx.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import io.vertx.todo.Tasks;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database client class which is used to manipulate data within database.
 */
public class DBclient {
    private static final Log log = LogFactory.getLog(DBclient.class);

    private MongoClient mongoClient;
   // private final String URI = "mongodb://0.0.0.0:27017";     //Default mongodb port number is 27017
    private final String URI = "mongodb://admin:admin@ds011880.mlab.com:11880/task_db";     //Cloud database
    private final String DB = "task_db";                        //database name
    private final String COLLECTION_NAME = "tasks";             //Tasks will be saved in this collection
    private final String REMOVED_COLLECTION = "rmvd_task";      //Removed tasks from the 'tasks' collection will be saved here

    /**
     * It will initiate the mongo database client.
     * @param vertx vertx from main verticle thread
     * @return initiated mongoclient
     */
    public MongoClient init(Vertx vertx){
        JsonObject config = Vertx.currentContext().config();
        String uri = config.getString("mongo_uri");
        if (uri == null) {
            uri = URI;
        }
        String db = config.getString(DB);
        if (db == null) {
            db = DB;
        }

        JsonObject mongoconfig = new JsonObject()
                .put("connection_string", uri)
                .put("db_name", db);
        mongoClient = MongoClient.createShared(vertx, mongoconfig);

        return mongoClient;
    }

    /**
     * It will add data into a collection of mongo db
     * @param mongoClient initiated mongoclient object from main verticle
     * @param task Object that wants to be inserted into collection
     */
    public void addData(MongoClient mongoClient, Tasks task, RoutingContext routingContext){
        JsonObject taskJson = new JsonObject()
                .put("task", task.getTitle())
                .put("completed",task.getCompleted());

        mongoClient.insert(COLLECTION_NAME, taskJson, res -> {
            if (res.succeeded()) {
                log.info("Successfully inserted: " + res.result());
                routingContext.response()
                        .setStatusCode(201)
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(taskJson.encodePrettily());
            }
            else{
                routingContext.response().setStatusCode(500).end();
            }
        });
    }

    /**
     * It can retrieve all data in a collection of database
     * @param mongoClient initiated mongoclient object from main verticle
     * @return Map of Objects with the key values of _ids
     */
    public Map<String, Tasks> viewAllData(MongoClient mongoClient){
        Map<String,Tasks> tasksMap = new HashMap<>();

        JsonObject jsonObject = new JsonObject();
        JsonObject query = new JsonObject();
        mongoClient.find(COLLECTION_NAME,query,res -> {
            if (res.succeeded()) {
                log.info("Getting all data");
                for (JsonObject json : res.result()) {
                    Tasks tasks = new Tasks();
                    tasks.setCompleted(json.getBoolean("completed"));
                    tasks.setTitle(json.getString("task"));
                    tasksMap.put(json.getString("_id"), tasks);
                    jsonObject.mergeIn(json);
                    log.info(json.encodePrettily());
                }
               /* routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(jsonObject.encodePrettily());*/
            } else {
                res.cause().printStackTrace();
                log.error("Error while retrieving all data");
                log.error(res.cause());
            }
        } );
        return tasksMap;
    }

    /**
     * get a specific task from collection with given id
     * @param mongoClient initiated mongoclient object from main verticle
     * @param id _id of the document that has to be get
     * @param routingContext routing context from server
     */
    public void getTask (MongoClient mongoClient,String id,RoutingContext routingContext){
        JsonObject query = new JsonObject().put("_id",id);
        mongoClient.findOne(COLLECTION_NAME, query, new JsonObject(), result -> {
            if (result.succeeded()) {
                if (result.result() == null) {
                    routingContext.response().setStatusCode(500).end();
                } else
                    routingContext.response()
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .end(result.result().encodePrettily());
            }
            if (result.failed()) {
                routingContext.response().setStatusCode(500).end();
            }
        });
    }


    /**
     * It will remove a data with specified id from the database and it will insert the deleted document into
     * another collection for data safety
     * @param mongoClient initiated mongoclient object from main verticle
     * @param id _id of the document that has to be deleted.
     */
    public void removeData (MongoClient mongoClient, String id, RoutingContext routingContext){
        JsonObject query = new JsonObject().put("_id",id);
        mongoClient.find(COLLECTION_NAME,query,resultFind -> {
            if(resultFind.succeeded()){
                log.info("Adding id "+ id +" to deleted Collection");
                List<JsonObject> jsonObject = resultFind.result();
                mongoClient.insert(REMOVED_COLLECTION,jsonObject.get(0),resultInsert -> {
                    if(resultInsert.succeeded()){
                        log.info("Successfully inserted into deleted collection: "+id);
                    }
                });

            }
        });
        mongoClient.remove(COLLECTION_NAME,query,res -> {
           if(res.succeeded()){
               log.info("Successfully removed id "+id);
               routingContext.response().end();
           }
            else{
               routingContext.response().setStatusCode(500).end();
               log.error(res.cause());
           }
        });
    }

    /**
     * It will toggle the completed boolean for given _id
     * @param mongoClient initiated mongoclient object from main verticle
     * @param id _id of the document whose @param completed has to be toggled.
     */
    public void modifyData (MongoClient mongoClient,String id, RoutingContext routingContext){
        JsonObject query = new JsonObject().put("_id",id);
        mongoClient.find(COLLECTION_NAME,query,res -> {
            if(res.succeeded()){
                List<JsonObject> jsonObjects = res.result();
                JsonObject taskJson = jsonObjects.get(0);
                boolean completed = taskJson.getBoolean("completed");
                String title = taskJson.getString("title");
                JsonObject modifiedJson = new JsonObject()
                        .put("_id",id)
                        .put("completed",!completed)
                        .put("title",title);
                mongoClient.save(COLLECTION_NAME,modifiedJson,saveResult -> {
                    if(saveResult.succeeded()){
                        log.info("Successfully modified "+ id);
                        getTask(mongoClient,id,routingContext);
                    }
                    else{
                        routingContext.response().setStatusCode(500).end();
                    }
                });
            }
        });
    }
}
