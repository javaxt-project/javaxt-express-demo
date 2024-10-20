//******************************************************************************
//**  Template Scripts
//******************************************************************************
/**
 *   This file contains functions called by the init() script in template.html
 *
 ******************************************************************************/



  //**************************************************************************
  //** initWebSite
  //**************************************************************************
  /** Used to update the website into a dynamic web application. As users
   *  click on links (e.g. tabs) the "content" node is dynamically updated
   *  without a full page refresh.
   */
    var initWebSite = function(){


      //Get content node
        var content;
        var nodes = document.getElementsByTagName("td");
        for (var i=0; i<nodes.length; i++){
            var node = nodes[i];
            if (node.className=="content"){
              //Found the content node! However, the height of the "content"
              //node will be updated by the carousel which may conflict with
              //the CSS rules for the "content" node. As a workaround, we
              //wrapped the content in a div and passing the wrapper div to
              //the constructor.
                for (var j=0; j<node.childNodes.length; j++){
                    if (node.childNodes[j].nodeType==1){
                        content = node.childNodes[j];
                        break;
                    }
                }

                break;
            }
        }


      //Get tabs and navbar
        var tabs = [];
        var navbar;
        var header = document.getElementsByClassName("header")[0];
        var nodes = header.getElementsByTagName("div");
        for (var i=0; i<nodes.length; i++){
            var node = nodes[i];
            if (node.className=="tabs"){
                tabs = node.getElementsByTagName("a");
            }
            else if (node.className=="navbar"){
                navbar = node;
            }
        }


      //Get menu button and
        var menuButton = header.getElementsByTagName("input")[0];
        var hideMenu = function(){
            menuButton.checked = false;
        };
        for (var i=0; i<tabs.length; i++){
            var tab = tabs[i];
            if (tab.addEventListener) {
                tab.addEventListener("click", hideMenu);
            }
            else if (tab.attachEvent) { // For IE 8 and earlier versions
                tab.attachEvent("onclick", hideMenu);
            }
        }


        var fx = new javaxt.dhtml.Effects();


      //Instantiate website component
        new javaxt.express.WebSite(content, {
            tabs: tabs,
            navbar: navbar,
            animationSteps: 0,
            fx: fx,
            padding: 0
        });

    };


  //**************************************************************************
  //** initAutoRefresh
  //**************************************************************************
  /** Used to automatically refresh the browser whenever a file is created,
   *  updated, or deleted on the server.
   */
    var initAutoRefresh = function(){

        var connected = false;

        new javaxt.dhtml.WebSocket({
            url: "/",
            onMessage: function(msg){

                var arr = msg.split(",");
                var model = arr[0];
                var op = arr[1];
                var info = arr[2];


                //console.log(model, op, info);

                if (model==="FILE"){
                    location.reload();
                }

            },
            onConnect: function(){
                if (!connected){
                    connected = true;
                }
            },
            onDisconnect: function(){
                if (connected){
                    connected = false;

                    //Show offline message
                }
            },
            onTimeout: function(){
                connected = false;

                //Show offline message
            }
        });


    };