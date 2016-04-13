# REST API for OROBOKS

Following are the REST API available for Oroboks client

> Note : This information will be updated as new REST API are added.

## User Information Retrieval 
 **GET Requests** :  
`/users` : Fetches all *active* users in the specified JSON format.  
`/users?emailId={EmailId}` :  Fetches user with given emailId in the specified JSON format.  
`/users?role={RoleName}`: Fetches all users for the specified role in JSON format.  
`/users/{userId}` :  Fetches user with primary id of the user(UUID generated from database).   
`/users/deactivate/{emailId}` : Deactivates user with the given emailId. Returns 200 OK, if user is successfully deactivated. Will return 304(No Modified content) if user associated with the emailid is already deactivated.   

**Sample JSON Response when user is retrieved**  
```json
{
    "Users": [{
        "id": "1",
        "profile_pic_url": "http://localhost:8080/sampleRest/oroboks/users/images/default",
        "rolename": "Customer",
        "locations": [{
            "locationlink": "http://localhost:8080/sampleRest/oroboks/locations/1",
            "isDefaultLocation": true
        }, {
            "locationlink": "http://localhost:8080/sampleRest/oroboks/locations/2",
            "isDefaultLocation": false
        }],
        "birthdate": "1989-11-21",
        "userid": "anarain1989@gmail.com",
        "links": [{
            "rel": "self",
            "href": "http://localhost:8080/sampleRest/oroboks/users/1"
        }]
    }]
}
```
*Please Note : If users is empty, empty users map is returned*  
*User JSON contains following fields:*  
`id`: primary key of the user saved in the database ( This is the UUID saved in database). Will never be null or empty  
`profile_pic_url`: contains the link to the user profile picture.   
*Please Note : The profile pic url will be saved in the format: `~/oroboks/users/images/{user-email-id}`*  
*Default id of the user will be “default” so for instance given above default picture will be there.*  
`roleName` : role of the user i.e if person is Customer/Provider  
`locations` : locations is the map containing location information associated with the user. This is subdivided into following :  
	`locationlink`: This gives the url to get the location  
	`isDefaultLocation`: Determines if the user location is default location.  
*Please Note : If no locations are associated with user, locations field in the JSON in an empty array.*  
`birthdate`: States the D.O.B of the user. *If D.O.B is not mentioned, this field does not appear in the JSON*. This is an optional field in the JSON.  
`links`:  These specify some other useful links w.r.t user. Currently it has links to self.   
T.B.D: What other things we can chip in.  

**POST  Request**:  
`/users` : Will add the user specified in the content.  

*The content will be provided in the JSON format. Hence be sure of following:*  
`Content-Type: application/json`  

// Be sure of case-sensitive for keys  
Data to be passed:  
```
{
	“userId” :”<Email Id of the Person>”  // Required
	“roleName”: “<Customer/Provider>” //Required
	“birthDate” : “<yyyy-mm-dd>” //Optional but please note the format of date.
}
```
`userId`: Required field  
`roleName`: Required field  
`birthDate`: optional field but if entering it please note the format for instance : 1989-11-21  

## Location Retrieval
**GET Requests** :  
`/locations?zipcode={zipCode}`: Fetches all locations with the specified zipCode.
`/locations/{locationid}` : Fetches data for displaying locations.
**Sample JSON Response when location is retrieved**  
```json
{
  "locations": [
    {
      "id":"Location@8f0cd406-c138-46ed-b183-8798568de0e3",
      "address":"12490 QUIVIRA ROAD",
      "apt":"1620",
      "city":"OVERLAND PARK",
      "state":"KANSAS",
      "country":"UNITED STATES",
      "zip":"66213",
      "users":[
          {
            "username":"aditya-narain@live.in",
            "link":"http://localhost:8080/sampleRest/oroboks/users/1"
          }
        ],
        "links": [
                {
                    "rel": "self",
                    "href": "http://localhost:8080/sampleRest/oroboks/locations/8f0cd406-c138-46ed-b183-8798568de0e3"
                }
            ]
    },
    {
      "id":"Location@1",
      "address":"CERNER, 10236 MARION PARK DRIVE",
      "city":"KANSAS CITY",
      "state":"MISSOURI",
      "country":"UNITED STATES",
      "zip":"64137",
      "users":[
          {
            "username":"aditya-narain@live.in",
            "link":"http://localhost:8080/sampleRest/oroboks/users/1"
          }
        ],
        "links": [
                {
                    "rel": "self",
                    "href": "http://localhost:8080/sampleRest/oroboks/locations/1"
                }
            ]
      
    }
  ]
}
```
*Note : If Apartment does not exist JSON does not show that field.*   

**POST Request**  
`/users/{userid}/locations` - POST Location associated with the user.   
*The content will be provided in the JSON format. Hence be sure of following:*    
`Content-Type: application/json`  

// Be sure of case-sensitive for keys  
Data to be passed:  
```
{
  "streetAddress": "<Street address> //Required",
  "city":"<City>" //Required,
  "state":"<State>"//Required ,
  "zipCode":"<zipCode>"//Required,
  "apt":"<Apartment Name or Number>" //Optional if we do not put in null will registered in Database which allowed,   
  "country":"<Country Name>" //Optional. If we do not put country United States is stored by default
}
```
