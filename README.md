# Web-Calendar

## How it works


An ajax call is made from the client where a command embedded in json
requests a serverside action. If the client is sending data to the server,
it is appended to the json request as nested json.

The json request is structured as follows:

```javascript
    {
        'action': 'login',
        'data'  : {
            'login-email'   : email,
            'login-password': password
        }
    }
```

Where 'action' is the desired server action and 'data' is the optional data package.

Data.js handles all ajax calls and contains the get() and give() functions.

```javascript
    Data.give(action, data, callback, asList);
    Data.get(action, callback, asList);
```

Where callback and asList are optional. Parameter asList is a boolean flag which if true
expects a List as a repsonse. The callback is invoked upon a successful ajax call.

Data can alternatively be fed to the json request via a string array containing element IDs. 
The Data.js object will use the array element values to match its corresponding HTML element
and fetch its value. Supported element types are ```<input> and <select>.```

Data.give() is used as follows:

```html
   <input id='login-email'>
   <input id='login-password'>
   <input onsubmit='login("login-email", "login-password")'>
```
```javascript
    User.login(email, password){
        Data.give('login', [email, password],
            function(response){
                switch(response.code){
                    case 1: success(); break;
                    case 2: failure(); break;
                }
        });  
    }
```

Here the data parameter of Data.give() is a string array containing HTML element ids.
The asList boolean is not set.

The request is then sent to a front controller servlet where it is dispatched to the appropriate
action, using the action URL provided. The URL must match the action classname. Classpath to Actions
are provided by the controller servlet. All Actions extend Action and must override the method
perform(HttpRequest, HttpResponse).

Actions access the sent data through Data.java, a utility class that parses the data element in the 
JSON request string. 

Data is accessed in the Action as follows:

```java
    Data data = new Data(request);
    String email = data.get("login-email");
    String password = data.get("login-password");
```
Action has a protected Service instance which contains and abstracts the persistance layer DAOs.

```java
    User user = service.login(email, password);
    DTO dto = new DTO(user);
```

After services are performed a response is generated. Response.java handles all responses.
It has 3 overloaded methods for responsding which accept either a String, List<String> or DTO
as parameters along with an HttpResponse. A response is then packaged as JSON and sent to the client
using HttpResonse.getWriter().

```java
    Response.send(dto, response);
```

To handle the response, Data.js uses the asList boolean flag to determine how the response should 
be handled before being passed to the callback. 

```javascript
    if(callback !== undefined){
        if(asList)
            callback(response);
        else
            callback(JSON.parse(response);
    }
```
