package javaxt.demo.express;

import java.util.*;
import javaxt.io.Directory;
import static javaxt.utils.Console.*;

//******************************************************************************
//**  Main
//******************************************************************************
/**
 *  Command line interface used to run javaxt-express demos
 *
 ******************************************************************************/

public class Main {


  //**************************************************************************
  //** main
  //**************************************************************************
  /** Command line interface
   */
    public static void main(String[] arguments) throws Exception {
        HashMap<String, String> args = parseArgs(arguments);



      //Process command line args
        if (args.containsKey("-version")){
            printVersion();
        }
        else if (args.containsKey("-start")){
            String start = args.get("-start").toLowerCase();

            if (start.equals("cms")){
                startCMS(args);
            }
            else if (start.equals("webservices")){
                startWebServices(args);
            }
            else if (start.equals("react")){
                startReact(args);
            }
            else if (start.equals("webapp") || start.equals("app")){
                startWebApp(args);
            }
            else{
                System.out.println("Unknown/unsupported -start value. Given \"" + start + "\"");
            }
        }
        else{
            System.out.println("Please provide a valid -start command.");
        }
    }


  //**************************************************************************
  //** printVersion
  //**************************************************************************
    private static void printVersion(){
        //System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Java Version: " + javaxt.utils.Java.getVersion());

        javaxt.io.Jar jar = new javaxt.io.Jar(javaxt.io.Jar.class);
        System.out.println("JavaXT Core: " + jar.getVersion());

        jar = new javaxt.io.Jar(javaxt.http.Server.class);
        System.out.println("JavaXT Server: " + jar.getVersion());

        jar = new javaxt.io.Jar(javaxt.express.Server.class);
        System.out.println("JavaXT Express: " + jar.getVersion());
    }


  //**************************************************************************
  //** startCMS
  //**************************************************************************
    private static void startCMS(HashMap<String, String> args) throws Exception {


      //Get path to the CMS directory
        var cmsDemo = args.get("-demo");
        var demoDir = new Directory(getDemoDir() + "cms");
        var cmsDir = new Directory(demoDir + cmsDemo);
        if (!cmsDir.exists()) cmsDir = new Directory(cmsDemo);
        if (!cmsDir.exists()){
            System.out.println("Invalid -demo option. Valid options include:");
            for (Object obj : demoDir.getChildren()){
                if (obj instanceof Directory){
                    Directory d = (Directory) obj;
                    System.out.println("   -demo " + d.getName());
                }
            }
            System.out.println("Alternatively, you can provide a fully " +
            "qualified path to a CMS directory using the -demo argument.");
            return;
        }


      //Start the CMS server
        CMS.start(cmsDir, args);

    }


  //**************************************************************************
  //** startWebServices
  //**************************************************************************
    private static void startWebServices(HashMap<String, String> args) throws Exception {

        var persistanceDemo = args.get("-demo");
        var demoDir = new Directory(getDemoDir() + "webservices");
        var dir = new Directory(demoDir + persistanceDemo);
        if (!dir.exists()){
            System.out.println("Invalid -demo option. Valid options include:");
            for (Object obj : demoDir.getChildren()){
                if (obj instanceof Directory){
                    Directory d = (Directory) obj;
                    System.out.println("   -demo " + d.getName());
                }
            }
            System.out.println("Alternatively, you can provide a fully " +
            "qualified path to a persistance directory using the -demo argument.");
            return;
        }


      //Start the Persistence server
        WebServices.start(dir, args);
    }


  //**************************************************************************
  //** startReact
  //**************************************************************************
    private static void startReact(HashMap<String, String> args) throws Exception {

        Directory demoDir = null;
        
        var demo = args.get("-demo");
        if (demo!=null){
            for (Directory d : new Directory(getDemoDir() + "react").getSubDirectories()){
                if (d.getName().equalsIgnoreCase(demo)){
                    demoDir = d;
                    break;
                }
            }
            if (demoDir==null){
                System.out.println("Unknown/unsupported -demo value. Given \"" + demo + "\"");
                return;
            }
        }

        if (demoDir==null && args.containsKey("-dir")){
            try{
                demoDir = new Directory(args.get("-dir"));
                if (!demoDir.exists()) throw new Exception();
            }
            catch(Exception e){
                System.out.println("Invalid -dir value. Given \"" + args.get("-dir") + "\"");
                return;
            }
        }

        if (demoDir==null){
            System.out.println("Please provide a valid -demo or -dir");
            return;
        }

        React.start(demoDir, args);
    }


  //**************************************************************************
  //** startWebApp
  //**************************************************************************
    private static void startWebApp(HashMap<String, String> args) throws Exception {
        var demo = args.get("-demo");
        if (Auth.hasDemo(demo)){
            Auth.start(getDemoDir(), args);
        }
        else{
            System.out.println("Unknown/unsupported -demo value. Given \"" + demo + "\"");
        }
    }


  //**************************************************************************
  //** getDemoDir
  //**************************************************************************
    private static Directory getDemoDir(){
        javaxt.io.Jar jar = new javaxt.io.Jar(Main.class);
        Directory currDir = new Directory(jar.getFile().getParentFile());
        return new Directory(currDir.getParentDirectory() + "demos");
    }

}