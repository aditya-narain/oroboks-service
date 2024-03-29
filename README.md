Current Build Status:
[ ![Codeship Status for aditya-narain/oroboks-service](https://codeship.com/projects/449d2d60-3127-0134-312c-367c60ad3115/status?branch=master)](https://codeship.com/projects/164365)
# REST API for OROBOKS
> Note : This information will be updated as new REST API are added.


## Following are the REST API available for Oroboks client
1. [User Operation](https://github.com/aditya-narain/oroboks-service#user-information-retrieval)
  * [GET Request](https://github.com/aditya-narain/oroboks-service#get-requests-)
  * [POST Request](https://github.com/aditya-narain/oroboks-service#post--request)
2. [Location Operation](https://github.com/aditya-narain/oroboks-service#location-retrieval)
  * [GET Request](https://github.com/aditya-narain/oroboks-service#get-requests--1)
  * [POST Request](https://github.com/aditya-narain/oroboks-service#post-request)
3. [Providers/Combos Operation](https://github.com/aditya-narain/oroboks-service#providersrestaurants-retrieval)
  * [GET Request](https://github.com/aditya-narain/oroboks-service#get-request)
4. [Order Operation](https://github.com/aditya-narain/oroboks-service#Oroboks Orders)
 

## User Information Retrieval 
##### GET Requests :
`/users/currentuser` : Fetches current *active* user (according to Coookie value) in the specified JSON format. Returns 403 if cookie is tried to be changed/mishandled/expires. If cookie does not have the token, 403 is returned.       
`/users/currentuser/{userId}` :  Fetches user with primary id of the user(UUID generated from database).   
`/users/currentuser/deactivate` : Deactivates the current user saved in the cookie. Returns 200 OK, if user is successfully deactivated. Will return 304(No Modified content) if user associated with the emailid is already deactivated. Returns 403 if cookie is tried to be changed/mishandled/expires.
 
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
`/users/currentuser/orders` : Orders for the currentuser. 
```json
{
    "YYYY-MM-DDT00:00:00.000+0000": [
        {
            "summary": "<Summary>",
            "comboName": "<Combo>",
            "price": "<Price>",
            "sideDish": "<Side Dish>",
            "links": [
                {
                    "rel": "user",
                    "href": "http://oroboks.herokuapp.com/users/1480775d-8c4f-4fb1-b0f0-c54620c9b338"
                },
                {
                    "rel": "self",
                    "href": "http://oroboks.herokuapp.com/users/currentusers/orders/08a3c564-dce5-4950-a094-e16de68ad69e"
                },
                {
                    "rel": "comboImage",
                    "href": "http://oroboks.herokuapp.com/combos/default"
                }
            ],
            "mainDish": "<Main Dish>",
            "day": "YYYY-MM-DD 00:00:00.0"
        }
    ]
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

##### POST  Request:  

`/users/getToken?emailId="abc@xyz.com"`: This should be called right after user successfully logs in. This request returns the user information with tokenId in the cookie only if there is secured connection. It returns 500, if user does not exist in the database.    
Example of this request: `/users/getToken?emailId="abc@xyz.com"`    
*The content for adding user will be provided in the JSON format. Hence be sure of following:*  
`Content-Type: application/json`   
Secret key needs to passed in the HEADER so would be something like:    
`Authorization:SecretKey@123`      

`/users` : Will add the user specified in the content.   
*The content for adding user will be provided in the JSON format. Hence be sure of following:*  
`Content-Type: application/json`   
Secret key needs to passed in the HEADER so would be something like:    
`Authorization:SecretKey@123`     
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
##### GET Requests :  
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

##### POST Request  
`/users/locations` - POST Location associated with the specific user as saved in the cookie. If no token exist in the cookie 403 is returned.   
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
## Providers(Combos) Retrieval

##### GET REQUEST
`/combos/locations/{zipcode}` - Fetches all the active combos availaible in 8 miles radius of the zipcode.  
`/combos/locations?latitude={latitude}&longitude={longitude}` : Fetches all the active combos in 8 miles radius of the location coordinates.

*Please Note : I could have written /locations/{zipcode}/combos, makes more sense but was confused how to write the second rest api with location coordinates. I also personally feel this REST API would have meaning something like : Get me combos in specific locations.

**Sample JSON for Combos Tentatively**
```json 
{
    "combos": {
        "Pakistani": [
            {
                "summary": "Spicy combo of supreme nonVeg combo",
                "image": "default",
                "price": "10",
                "comboType": "Nonvegeterian",
                "restaurant": {
                    "restaurantwebsite": "www.kulturekurry.com",
                    "restaurantName": "Kulture Kurry",
                    "link": "http://oroboks.herokuapp.com/restaurants/1"
                },
                "comboId": "2",
                "name": "NonVeg Thali",
                "sideDish": "Rice, bread",
                "nutritionAttributes": [
                    "Low Cholestrol"
                ],
                "ingredients": "Halal Chicken, Pepper, Onion, Tomatoes, Cream",
                "availaibleDates": [
                    "2016-11-19, Saturday",
                    "2016-11-20, Sunday"
                ],
                "mainDish": "Chicken Tikka Masala"
            }
        ]
    }
}
```
`/combos/locations/{zipcode}?sortby=date`- Fetches all the active combos availaible in 8 miles radius of the zipcode and yields results by date.
`/combos/locations?latitude={latitude}&longitude={longitude}&sortby=date`- Fetches all active combos availaible in 8 miles radius of location coordinates and yield results by date.
**Sample JSON for Combos Tentatively for date**
```json
{
    "dates": {
        "2016-11-30, Wednesday": [
            {
                "cuisines": [
                    "Indian",
                    "Pakistani"
                ],
                "summary": "Spicy combo of the supreme veggie combo",
                "image": "default",
                "restaurant": {
                    "restaurantwebsite": "www.kulturekurry.com",
                    "link": "http://localhost:8080/oroboks/restaurants/1",
                    "restaurantName": "Kulture Kurry"
                },
                "ingredients": "Moong Daal, Pepper, Onion, Tomatoes, Cream Sauce",
                "id": "1",
                "comboId": "1",
                "price": "8.50",
                "comboType": "Vegeterian",
                "sideDish": "Rice, Naan bread",
                "name": "Veg Combo",
                "mainDish": "Daal Makhni, Paneer Tikka",
                "nutritionAttributes": [
                    "Low Sodium",
                    "Low Cholestrol"
                ]
            }
        ],
        "2016-12-01, Thursday": [
            {
                "cuisines": [
                    "Indian"
                ],
                "summary": "Medium spicy combo of veggie thali",
                "image": "default",
                "restaurant": {
                    "restaurantwebsite": "www.kulturekurry.com",
                    "link": "http://localhost:8080/oroboks/restaurants/1",
                    "restaurantName": "Kulture Kurry"
                },
                "ingredients": "Mushroom, Panner, Pepper, Onion, Tomatoes, Cream",
                "id": "11",
                "comboId": "3",
                "price": "10",
                "comboType": "Vegeterian",
                "sideDish": "Rice, Naan bread",
                "name": "Veg Deluxe",
                "mainDish": "Paneer Tikka, Mushroom Masala",
                "nutritionAttributes": []
            }
        ],
        "2016-12-02, Friday": [
            {
                "cuisines": [
                    "Indian",
                    "Pakistani"
                ],
                "summary": "Spicy combo of the supreme veggie combo",
                "image": "default",
                "restaurant": {
                    "restaurantwebsite": "www.kulturekurry.com",
                    "link": "http://localhost:8080/oroboks/restaurants/1",
                    "restaurantName": "Kulture Kurry"
                },
                "ingredients": "Moong Daal, Pepper, Onion, Tomatoes, Cream Sauce",
                "id": "13",
                "comboId": "1",
                "price": "8.50",
                "comboType": "Vegeterian",
                "sideDish": "Rice, Naan bread",
                "name": "Veg Combo",
                "mainDish": "Daal Makhni, Paneer Tikka",
                "nutritionAttributes": [
                    "Low Sodium",
                    "Low Cholestrol"
                ]
            }
        ],
        "2016-12-03, Saturday": [],
        "2016-12-04, Sunday": [],
        "2016-12-05, Monday": [],
        "2016-12-06, Tuesday": []
    }
}
```
## Oroboks Orders
#### GET Request
`/oroboks/users/currentuser/orders` - Fetches order for current user for the entire week.
Sample Current JSON
```json
{
    "2016-12-04T06:00:00.000+0000": [
        {
            "summary": "Spicy combo of supreme nonVeg combo",
            "comboName": "NonVeg Thali",
            "price": "10",
            "sideDish": "Rice, bread",
            "mainDish": "Chicken Tikka Masala",
            "links": [
                {
                    "rel": "user",
                    "href": "http://localhost:8080/oroboks/users/1480775d-8c4f-4fb1-b0f0-c54620c9b338"
                },
                {
                    "rel": "self",
                    "href": "http://localhost:8080/oroboks/users/currentusers/orders/a1d7f5fa-3632-47da-a5e2-4e7f32d2752d"
                },
                {
                    "rel": "comboImage",
                    "href": "http://localhost:8080/oroboks/combos/default"
                }
            ],
            "day": "2016-12-04 00:00:00.0",
            "orderId": "a1d7f5fa-3632-47da-a5e2-4e7f32d2752d"
        }
    ]
  }
```
##### POST Request
`/oroboks/users/currentuser/orders` - Creates orders for current user.
*Please Note : User should be authenticated so that we have currentuser in context*
**Sample JSON for creating orders. The content-type would be plain/text**
*This is a list*   
```
    [{
      "comboId":  
      {
        "uuid":"3" //Give UUID
      },
      "quantity": "<Quantity>",
      "orderDate": "YYYY-MM-DD" 
    }]
    
```


