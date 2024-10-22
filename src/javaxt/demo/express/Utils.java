package javaxt.demo.express;

import java.util.HashMap;
import javaxt.http.servlet.HttpServlet;

public class Utils {


  //**************************************************************************
  //** startServer
  //**************************************************************************
  /** Used to start a web server
   */
    public static void startServer(HashMap<String, String> args, HttpServlet servlet){

        int port = 9080;
        try{ port = Integer.parseInt(args.get("-port")); }catch(Exception e){}
        int numThreads = 250;
        new javaxt.http.Server(port, numThreads, servlet).start();

        System.out.println("Started server!");
    }

}