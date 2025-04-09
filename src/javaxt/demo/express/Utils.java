package javaxt.demo.express;

import java.util.HashMap;
import static javaxt.utils.Console.*;
import javaxt.http.servlet.HttpServlet;

public class Utils {


  //**************************************************************************
  //** startServer
  //**************************************************************************
  /** Used to start a web server
   */
    public static void startServer(HashMap<String, String> args, HttpServlet servlet){
        Integer port = getValue(args, "-port", "-p").toInteger();
        if (port==null) port = 9080;
        int numThreads = 250;
        new javaxt.http.Server(port, numThreads, servlet).start();
    }

}