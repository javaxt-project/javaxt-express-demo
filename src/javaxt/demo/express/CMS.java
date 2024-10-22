package javaxt.demo.express;

//Java imports
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

//JavaXT imports
import javaxt.io.Directory;
import javaxt.http.servlet.*;
import javaxt.http.websocket.*;
import static javaxt.demo.express.Utils.*;


//******************************************************************************
//**  CMS Demo
//******************************************************************************
/**
 *   Used to run CMS-related demos and scripts
 *
 ******************************************************************************/

public class CMS {


  //**************************************************************************
  //** start
  //**************************************************************************
  /** Used to start a CMS demo
   */
    public static void start(Directory cmsDir, HashMap<String, String> args){
        basicDemo(cmsDir, args);
    }


  //**************************************************************************
  //** basicDemo
  //**************************************************************************
  /** Used to support a basic CMS demo. Features a custom getContent() method
   *  to generate custom html for the "wiki" homepage and a custom websocket
   *  interface to notify clients whenever any files are created, updated, or
   *  deleted on the server.
   */
    public static void basicDemo(Directory cmsDir, HashMap<String, String> args){


      //Websocket stuff
        var listeners = new ConcurrentHashMap<Long, WebSocketListener>();
        var webSocketID = new AtomicLong(0);


      //Instantiate CMS servlet
        var servlet = new javaxt.express.cms.WebSite(cmsDir){


          /* Override the processRequest method and add a websocket listener
           */
            public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {

                if (request.isWebSocket()){
                    createWebSocket(request, response);
                }
                else{
                    super.processRequest(request, response);
                }
            }


          /* Override the getContent method to generate custom HTML for wiki pages.
           * Performs custom keyword substitution for the "<%=index%>" tag.
           */
            public javaxt.express.cms.Content getContent(
                javaxt.http.servlet.HttpServletRequest request, javaxt.io.File file){

              //Get path from url
                String path = request.getURL().getPath().toLowerCase();


              //Remove leading and trailing "/" characters
                if (path.startsWith("/")) path = path.substring(1);
                if (path.endsWith("/")) path = path.substring(0, path.length()-1);


              //Return content
                if (path.equals("wiki")){
                    String html = file.getText();
                    Date date = file.getDate();
                    if (file.getName(false).equals("index")){
                        javaxt.express.cms.Content content = getIndex(file);
                        javaxt.utils.Date d = new javaxt.utils.Date(content.getDate());
                        if (d.isAfter(new javaxt.utils.Date(date))){
                            date = d.getDate();
                        }
                        html = html.replace("<%=index%>", content.getHTML());
                    }

                    return new javaxt.express.cms.Content(html, date);
                }
                else{
                    return super.getContent(request, file);
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



      //Watch for changes to the web directory and broadcast changes to any
      //websocket listeners
        javaxt.express.FileManager fileManager = servlet.getFileManager();
        int len = servlet.getWebDirectory().getPath().length();
        fileManager.getFileUpdates((Directory.Event event) -> {
            var file = new java.io.File(event.getFile());
            var path = file.toString().substring(len).replace("\\", "/");
            var action = event.getAction();
            var msg = "FILE," + action + "," + path;
            synchronized(listeners){
                Iterator<Long> it = listeners.keySet().iterator();
                while(it.hasNext()){
                    WebSocketListener ws = listeners.get(it.next());
                    ws.send(msg);
                }
            }
        });



      //Start web server
        startServer(args, servlet);
    }

}