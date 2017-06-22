# Web-Calendar

## Client Appointment Management

**After registering with using a registeration access code provided by the administrator, users can schedule and cancel appointments.**

<img align="center" src="https://user-images.githubusercontent.com/16550024/27458116-57a03e32-575c-11e7-9aca-a0cc603dd078.png" width="400"><img align="center" src="https://user-images.githubusercontent.com/16550024/27458114-579e70fc-575c-11e7-8002-047bfeb1c8cb.png" width="400">

<br>
<br>

**The administrator page shows scheduling information in a variety of ways. At the top the next five appointments for the day are shown.
Just below that, the admin can view all appointments for a specific day.** 

**The admin control panel also allows for days off to be set meaning they aren't added to the schedule. The registeration access code is also set here.**

<img align="center" src="https://user-images.githubusercontent.com/16550024/27458115-579f4504-575c-11e7-997c-1c6974c19f60.png" width="400"><img align="center" src="https://user-images.githubusercontent.com/16550024/27458118-57b1c864-575c-11e7-9f04-ac016ffbfa7b.png" width="400">

<br>
<br>

**The admin's schedule can be controlled by opening/closing time slots, making them available/not available for users to book.**

**New days are automatically added to the schedule with all of its time slots closed. The admin can go through the schedule and open the desired time slots. Red time slots are closed, green are open.**

<img align="center" src="https://user-images.githubusercontent.com/16550024/27458117-57a32fde-575c-11e7-8724-5c7cdf5e147e.png" width="400"><img align="center" src="https://user-images.githubusercontent.com/16550024/27458120-57b2bb7a-575c-11e7-9e89-12436c516298.png" width="400">

<br>
<br>

**Timeslots that have already been booked show up as yellow in which case the corresponding time slot button is disabled.**

<img align="center" src="https://user-images.githubusercontent.com/16550024/27458119-57b21576-575c-11e7-978e-e567cfc56656.png" width="400">

<br>
<br>

**A record of all appointments is kept and will be used for business analytics which will be viewable in a future update.** 

<br>
<br>

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
