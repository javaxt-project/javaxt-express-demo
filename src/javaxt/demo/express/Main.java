package javaxt.demo.express;

import java.util.*;
import javaxt.io.Directory;
import static javaxt.utils.Console.console;

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
    public static void main(String[] arguments) {
        HashMap<String, String> args = console.parseArgs(arguments);



      //Process command line args
        if (args.containsKey("-version")){
            printVersion();
        }
        else if (args.containsKey("-cms")){
            startCMS(args);
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
    private static void startCMS(HashMap<String, String> args){


      //Get path to the CMS directory
        var cmsDemo = args.get("-cms");
        var demoDir = new Directory(getDemoDir() + "cms");
        var cmsDir = new Directory(demoDir + cmsDemo);
        if (!cmsDir.exists()) cmsDir = new Directory(cmsDemo);
        if (!cmsDir.exists()){
            System.out.println("Invalid -cms option. Valid options include:");
            for (Object obj : demoDir.getChildren()){
                if (obj instanceof Directory){
                    Directory d = (Directory) obj;
                    System.out.println("   -cms " + d.getName());
                }
            }
            System.out.println("Alternatively, you can provide a fully " +
            "qualified path to a CMS directory using the -cms argument.");
            return;
        }


      //Start the CMS server
        CMS.start(cmsDir, args);

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