JAVA client for ooyala V2 HTTP API
==================================

The approach is very simple. It allows you to do GET, POST, PUT, PATCH and DELETE requests to our API by simply
specifying the path to the API you want to hit and depending of the call, a two more 
hashes which depending on the request, they contain the query parameters or the request's body.

Build
-----
  
    mvn package
    
Example
-------

First you need to create an OoyalaAPI object by passing your V2 API keys like this:

    OoyalaAPI api = new OoyalaAPI("<api key>", "<secret key>");
  
Now lets get all the assets under the "Funny dogs" label:

    HashMap<String, String> parameters = new HashMap<String, String>();
    parameters.put("where", "labels INCLUDES 'Funny dogs'");

    LinkedList<HashMap<String,Object>> assets = (LinkedList<HashMap<String,Object>>)api.getRequest("assets", parameters).get("items");
  
Now that we have our results on the assets ArrayList, lets print them out to the console.

    System.out.println("Printing out assets...");
    ListIterator<HashMap<String, Object>> iterator = assets.listIterator();
    while(iterator.hasNext()){
      HashMap<String, Object> asset = iterator.next();
    	System.out.println(asset.get("embed_code").toString() + " - " + asset.get("name").toString());
    }

It's that easy to work with this SDK!

We use json-simple to parse and encode JSON. Please visit http://code.google.com/p/json-simple/ for more information on supported classes when creating your body HashMap.

[More examples](src/test/java/com/ooyala/api/OoyalaApiTest.java)
