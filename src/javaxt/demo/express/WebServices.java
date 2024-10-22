package javaxt.demo.express;

//Java imports
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

//JavaXT imports
import javaxt.sql.*;
import javaxt.io.Directory;
import javaxt.http.servlet.*;
import javaxt.http.websocket.*;


import javaxt.express.*;
import javaxt.express.utils.DbUtils;
import static javaxt.demo.express.Utils.*;


public class WebServices {


  //**************************************************************************
  //** start
  //**************************************************************************
  /** Used to start a webservices demo
   */
    public static void start(Directory dir, HashMap<String, String> args) throws Exception {
        basicDemo(dir, args);
    }


  //**************************************************************************
  //** basicDemo
  //**************************************************************************
    private static void basicDemo(Directory dir, HashMap<String, String> args) throws Exception {

      //Get models
        javaxt.io.File inputFile = new javaxt.io.File(dir, dir.getName() + ".js");
        javaxt.orm.Model[] models = new javaxt.orm.Parser(inputFile.getText()).getModels();



      //Init database
        Database database = new Database();
        database.setDriver("H2");
        database.setHost(dir.toString().replace("\\", "/") + "database");
        initDatabase(models, database);



      //Websocket stuff
        var listeners = new ConcurrentHashMap<Long, WebSocketListener>();
        var webSocketID = new AtomicLong(0);



      //Instantiate custom WebService. This implementation broadcasts CRUD
      //events to websocket listeners via the notify() method.
        WebService ws = new WebService(){

            public void onCreate(Object obj, ServiceRequest request){
                notify("create", (Model) obj);
            };

            public void onUpdate(Object obj, ServiceRequest request){
                notify("update", (Model) obj);
            };

            public void onDelete(Object obj, ServiceRequest request){
                notify("delete", (Model) obj);
            };

            protected void notify(String action, Model model){
                String msg = action+","+model.getClass().getSimpleName()+","+model.getID();
                synchronized(listeners){
                    Iterator<Long> it = listeners.keySet().iterator();
                    while(it.hasNext()){
                        WebSocketListener ws = listeners.get(it.next());
                        ws.send(msg);
                    }
                }
            }
        };



      //Compile models into classes and register them with the WebService
        Class[] classes = new javaxt.orm.Compiler(models).getClasses();
        for (Class c : classes){
            Model.init(c, database.getConnectionPool());
            ws.addModel(c);
        }



      //Instantiate HttpServlet
        var servlet = new HttpServlet() {

            public void processRequest(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, java.io.IOException {


              //Add CORS support
                response.addHeader("Access-Control-Allow-Origin", "*");
                response.addHeader("Access-Control-Allow-Headers","*");
                response.addHeader("Access-Control-Allow-Methods", "*");


              //Send response
                if (request.isWebSocket()){
                    createWebSocket(request, response);
                }
                else{
                    ServiceRequest req = new ServiceRequest(request);
                    ServiceResponse rsp = ws.getServiceResponse(req, database);
                    rsp.send(response);
                }
            }


          /* Custom websocket interface
           */
            private void createWebSocket(
                HttpServletRequest request, HttpServletResponse response)
                throws java.io.IOException {

                new WebSocketListener(request, response){
                    private Long id;
                    public void onConnect(){
                        id = webSocketID.incrementAndGet();
                        synchronized(listeners){
                            listeners.put(id, this);
                        }
                    }
                    public void onDisconnect(int statusCode, String reason){
                        synchronized(listeners){
                            listeners.remove(id);
                        }
                    }
                };
            }
        };



      //Start web server
        startServer(args, servlet);
    }


  //**************************************************************************
  //** initDatabase
  //**************************************************************************
    private static void initDatabase(javaxt.orm.Model[] models, Database database) throws Exception {


      //Initialize schema (create tables, indexes, etc)
        String schema = new javaxt.orm.Schema(models).getSQLScript();
        if (database.getDriver().equals("H2")){

          //Set H2 to PostgreSQL mode
            Properties properties = database.getProperties();
            if (properties==null){
                properties = new java.util.Properties();
                database.setProperties(properties);
            }
            properties.setProperty("MODE", "PostgreSQL");
            properties.setProperty("DATABASE_TO_LOWER", "TRUE");
            properties.setProperty("DEFAULT_NULL_ORDERING", "HIGH");


          //Update list of non-reserved keywords (e.g. "KEY", "VALUE", "YEAR")
            HashSet<String> nonKeyWords = new HashSet<>();
            Set<String> resevedKeyWords = new HashSet<>(
                Arrays.asList(database.getReservedKeywords())
            );

            for (javaxt.orm.Model model : models){

                String str = model.getTableName().toUpperCase();
                if (resevedKeyWords.contains(str)) nonKeyWords.add(str);

                for (javaxt.orm.Field field : model.getFields()){
                    str = field.getColumnName().toUpperCase();
                    if (resevedKeyWords.contains(str)) nonKeyWords.add(str);
                }
            }

            if (!nonKeyWords.isEmpty()){
                properties.setProperty("NON_KEYWORDS", String.join(",", nonKeyWords));
            }
        }
        DbUtils.initSchema(database, schema, null);



      //Enable metadata caching
        database.enableMetadataCache(true);


      //Inititalize connection pool
        database.initConnectionPool();


    }
}