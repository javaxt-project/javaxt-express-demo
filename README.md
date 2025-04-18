# Introduction
Command line app and source code used to demonstrate some of the core
capabilities of the javaxt-express library.

## Maven Quickstart
```
git clone https://github.com/javaxt-project/javaxt-express-demo.git
cd javaxt-express-demo
mvn install
java -jar dist/express-demo.jar -version
```

Note that during the build, maven will download JavaScript files to support the demos.
JavaScript files are downloaded into the `downloads` folder and then copied into individual
demo folders.


## Demos
The javaxt-express demos are launched via command line. Each demo will start a
local webserver. By default the server will run on port 9080. You can add an
optional `-p` or `-port` argument to any demo to specify a different port.

### CMS Demo

```
java -jar dist/express-demo.jar -start cms -demo Basic
```

### WebServices Demo
The following command is used to start a REST service used to demonstrate
CRUD operations and persistance.
```
 java -jar dist/express-demo.jar -start webservices -demo CarSales
```

### React Demos
The following command is used to start a web server with a ReactJS app. This
demo illustrates how javaxt-express supports ReactJS "routing" with virtual
URLs and push/pop state listeners.
```
java -jar dist/express-demo.jar -start React -demo Routing
```
You can host any ReactJS app by simply providing a path to your ReactJS build
like this:
```
java -jar dist/express-demo.jar -start React -dir /path/to/build
```

### Auth Demos
The following command starts up a simple login demo. You will be prompted to
provide a username and password at start-up. This information will be used to
authenticate a user. You can provide whatever you username/password you want.
Once the server is up and running, it will host a simple login page where you
can test the login.
```
java -jar dist/express-demo.jar -start webapp -demo BasicAuth
```

The following command starts up a login demo that sends a temporary login code
to your email account that you can use to login. You will be prompted to
provide information to your email server including the STMP host, email, and
password. As you can see in the code, this information is not stored anywhere.
```
java -jar dist/express-demo.jar -start webapp -demo EmailLogin
```


## Dev Notes
Update the .gitignore to exclude JavaScript folders copied during the maven build.