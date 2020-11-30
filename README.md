# LEFTO : ANDROID SCHOOL PROJECT

## Summary
Lefto is a community application aimed to restaurants and people in need of food (users), that will allow restaurant to give away their edible leftovers to people that want to get it.
Restaurant can register their leftovers via the application, their type and the quantity they have.

The user can browse the map around his position, and every restaurant that gives away his leftovers will be displayed.

(Link to Trello : https://trello.com/b/HD4dy8Jb/lefto-android-project)

## DB Schema
Restaurant : Name, Latitude(double), Longitude(double),Type (string,ex: italian), Vegan : bool, Halal : bool
Client : Username, City
Leftover : Name, description, Vegan : bool, Hallal : bool, quantity
