package io.vertx.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.todo.Tasks;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBclient {
    private static final Log log = LogFactory.getLog(DBclient.class);

    private MongoClient mongoClient;
    private final String URI = "mongodb://localhost:27017";
    private final String DB = "task_db";
    private final String COLLECTION_NAME = "tasks";
    private final String REMOVED_COLLECTION = "rmvd_task";

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

    public void addData(MongoClient mongoClient, Tasks task){
        JsonObject taskJson = new JsonObject()
                .put("task", task.getTask())
                .put("completed",task.getCompleted());

        mongoClient.insert(COLLECTION_NAME, taskJson, res -> {
            if (res.succeeded()) {
                log.info("Successfully inserted: " + res.result());
            }
        });
    }

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
                    tasks.setTask(json.getString("task"));
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

    public void removeData (MongoClient mongoClient, String id){
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
           }
            else{
               log.error(res.cause());
           }
        });
    }

    public void modifyData (MongoClient mongoClient,String id){
        JsonObject query = new JsonObject().put("_id",id);
        mongoClient.find(COLLECTION_NAME,query,res -> {
            if(res.succeeded()){
                List<JsonObject> jsonObjects = res.result();
                JsonObject taskJson = jsonObjects.get(0);
                boolean completed = taskJson.getBoolean("completed");
                String task = taskJson.getString("task");
                JsonObject modifiedJson = new JsonObject()
                        .put("_id",id)
                        .put("completed",!completed)
                        .put("task",task);
                mongoClient.save(COLLECTION_NAME,modifiedJson,saveResult -> {
                    if(saveResult.succeeded()){
                        log.info("Successfully modified "+ id);
                    }
                });
            }
        });
    }
}
