# Introduction


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

- CMS
- CRUD
- React

### CMS Demo
```
java -jar dist/express-demo.jar -start cms -demo Basic
```

### React Demo
```
java -jar dist/express-demo.jar -start React -dir /path/to/build
```


## Dev Notes
Update the .gitignore to exclude JavaScript folders copied during the maven build.