package javaxt.demo.express;

import java.util.*;
import javaxt.io.Directory;
import javaxt.http.servlet.*;
import static javaxt.demo.express.Utils.*;


//******************************************************************************
//**  React Demo
//******************************************************************************
/**
 *   Used to run React-related demos and scripts
 *
 ******************************************************************************/

public class React {


  //**************************************************************************
  //** start
  //**************************************************************************
  /** Used to start a React demo
   */
    public static void start(Directory demoDir, HashMap<String, String> args){
        routerDemo(demoDir, args);
    }


  //**************************************************************************
  //** routerDemo
  //**************************************************************************
  /** Used to host a ReactJS app with routing.
   */
    private static void routerDemo(Directory demoDir, HashMap<String, String> args){


      //Instantiate FileManager
        var fileManager = new javaxt.express.FileManager(demoDir);


      //Instantiate HttpServlet
        var servlet = new HttpServlet() {

            public void processRequest(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, java.io.IOException {

              //Get url
                java.net.URL url = request.getURL();

              //Get path
                String path = url.getPath();
                String servletPath = getServletPath();
                if (!servletPath.endsWith("/")) servletPath += "/";
                path = path.substring(path.indexOf(servletPath)).substring(servletPath.length());
                if (path.startsWith("/")) path = path.substring(1);


              //Get file
                java.io.File file = fileManager.getFile(path);
                if (file==null || !file.exists()){
                    file = fileManager.getFile("index.html");
                }


              //Send response
                if (file==null || !file.exists()){
                    response.sendError(404);
                }
                else{
                    fileManager.sendFile(file, request, response);
                }
            }
        };



      //Start web server
        startServer(args, servlet);
    }
}