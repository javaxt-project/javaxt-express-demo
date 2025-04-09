package javaxt.demo.express;

import java.util.*;

import javaxt.http.servlet.*;

import javaxt.io.Directory;
import static javaxt.utils.Console.*;

import javaxt.express.*;
import static javaxt.demo.express.Utils.*;


//******************************************************************************
//**  Auth
//******************************************************************************
/**
 *  Used to run authentication-related demos
 *
 ******************************************************************************/
public class Auth {

    private static String[] demos = new String[]{"BasicAuth","EmailLogin"};


  //**************************************************************************
  //** hasDemo
  //**************************************************************************
    public static boolean hasDemo(String demo){
        for (String d : demos){
            if (d.equalsIgnoreCase(demo)) return true;
        }
        return false;
    }


  //**************************************************************************
  //** start
  //**************************************************************************
    public static void start(Directory demoDir, HashMap<String, String> args){
        String demo = args.get("-demo");
        if (demo.equalsIgnoreCase("BasicAuth")){
            var webDir = new javaxt.io.Directory(demoDir + "auth/basic");
            basicAuth(webDir, args);
        }
        else if (demo.equalsIgnoreCase("EmailLogin")){
            emailAuth(demoDir, args);
        }
        else{
            System.out.println("Unknown/unsupported -demo value. Given \"" + demo + "\"");
        }
    }


  //**************************************************************************
  //** basicAuth
  //**************************************************************************
  /** Used to demonstrate basic authentication using a simple username and
   *  password.
   */
    private static void basicAuth(Directory web, HashMap<String, String> args){

      //Prompt user to define a valid username and password
        String username = getInput("Username: ");
        String password = getPassword("Password: ");


      //Create an approved/authorized User
        var authorizedUser = new User(username, password);
        System.out.println(username + " user created!");


      //Create a simple Authenticator used to validate credentials associated
      //with an HTTP Request. In this demo we only have one approved User.
      //If the credentials match the username/password of the approved
      //user, then the user is returned via the getPrinciple(). If the
      //credentials are null or invalid, a null user is returned.
        var basicAuthenticator = new javaxt.express.Authenticator(){

            public java.security.Principal getPrinciple(){

                User user = null;
                try{
                    String[] credentials = getCredentials();
                    String username = credentials[0];
                    String password = credentials[1];

                    if (username!=null && password!=null){

                        if (username.equalsIgnoreCase(authorizedUser.username) &&
                            password.equals(authorizedUser.password)){
                            user = authorizedUser;
                        }
                    }
                }
                catch(Exception e){}

                setUser(user);
                return user;
            }
        };



      //Create a custom HttpServlet
        var servlet = new HttpServlet() {

          //Instantiate FileManager
            private FileManager fileManager = new FileManager(web);

          //Assign authenticator to this servlet
            {this.setAuthenticator(basicAuthenticator);}

          //
            public void processRequest(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, java.io.IOException {

                if (request.isWebSocket()) return;


              //Get path from url, excluding servlet path and leading "/" character
                String path = request.getPathInfo();
                if (path!=null) path = path.substring(1);


              //Get first "directory" in the path
                String service = path==null ? "" : path.toLowerCase();
                if (service.contains("/")) service = service.substring(0, service.indexOf("/"));



              //Pass the request to the authenticator. Return early if the
              //authenticator handled the request.
                javaxt.express.Authenticator authenticator = (javaxt.express.Authenticator) getAuthenticator(request);
                if (authenticator.handleRequest(service, response)) return;



              //Send static file if we can
                if (service.length()==0){

                  //If the service is empty, send welcome file (e.g. index.html)
                    fileManager.sendFile(request, response);
                }
                else{

                  //Check if the service matches a file or folder in the web
                  //directory. If so, send the static file as requested.
                    for (Object obj : web.getChildren()){
                        String name;
                        if (obj instanceof javaxt.io.File){
                            name = ((javaxt.io.File) obj).getName();
                        }
                        else{
                            name = ((javaxt.io.Directory) obj).getName();
                        }
                        if (service.equalsIgnoreCase(name)){
                            fileManager.sendFile(path, request, response);
                            return;
                        }
                    }

                }
            }

        };


      //Start web server
        startServer(args, servlet);
    }


  //**************************************************************************
  //** emailAuth
  //**************************************************************************
    private static void emailAuth(Directory demoDir, HashMap<String, String> args){



      //Create a simple Authenticator used to validate credentials associated
      //with an HTTP Request. In this demo we only have one approved User.
      //If the credentials match the username/password of the approved
      //user, then a
        var authenticator = new javaxt.express.Authenticator(){

            public java.security.Principal getPrinciple(){

              //Get user from cache
                User user = (User) getUser();
                if (user!=null) return user;

                try{

                    String[] credentials = getCredentials();
                    String email = credentials[0];
                    String accessCode = credentials[1];

                    if (email!=null && accessCode!=null){


                    }
                }
                catch(Exception e){
                }

                setUser(user);
                return user;
            }

        };

        System.out.println("Not implemented...");
    }


  //**************************************************************************
  //** User Class
  //**************************************************************************
  /** Simple implementation of a User class
   */
    private static class User
        implements java.security.Principal, javaxt.express.User {

        private String username;
        private String password;
        public User(String username, String password){
            this.username = username;
            this.password = password;
        }

      /** Returns a user ID - required for implementations of the
       * javaxt.express.User interface
       */
        public Long getID(){
            return 1L;
        }

      /** Returns a username/email - required for implementations of the
       *  java.security.Principal interface.
       */
        public String getName(){
            return username;
        }
    }

}