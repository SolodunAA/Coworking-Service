1. User registration
   POST: http://localhost:8080/Coworking-Service-1.0/registration
   {
   "login": "userTest",
   "password": "passwordTest"
   }
2. User authentication
   POST: http://localhost:8080/Coworking-Service-1.0/authentication
   {
   "login": "userTest",
   "password": "passwordTest"
   }
3. Viewing available time slots on a date for bookingDto halls
   GET: http://localhost:8080/Coworking-Service-1.0/bookingDto/bookHall
   {
   "date": "2024-07-07"
   }
4. Hall reservation
   POST: http://localhost:8080/Coworking-Service-1.0/bookingDto/bookHall
   {
   "hallName": "Paris",
   "date": "2024-01-01",
   "startTime": "10:00:00",
   "endTime": "15:00:00"
   }
5. Viewing available time slots on a date for bookingDto desks
   GET: http://localhost:8080/Coworking-Service-1.0/bookingDto/bookDesk
   {
   "roomName": "Red",
   "date": "2024-07-07"
   }
6. Desk reservation
   POST: http://localhost:8080/Coworking-Service-1.0/bookingDto/bookDesk
   {
   "roomName": "Red",
   "deskNumber": 2,
   "date": "2024-01-01",
   "startTime": "10:00:00",
   "endTime": "15:00:00"
   }
7. Viewing all bookingDtos for user
   GET: http://localhost:8080/Coworking-Service-1.0/bookingDto/manager
8. Changing the bookingDto
   PUT: http://localhost:8080/Coworking-Service-1.0/bookingDto/manager
   {
   "bookingId": 1,
   "date": "2024-01-01",
   "startTime": "10:00:00",
   "endTime": "15:00:00"
   }
9. Deleting the bookingDto
   DELETE: http://localhost:8080/Coworking-Service-1.0/bookingDto/manager
   {
   "bookingId": 1,
   }
10. Viewing all halls in coworking
    GET: http://localhost:8080/Coworking-Service-1.0/coworking/halls
11. Deleting the hall
    DELETE: http://localhost:8080/Coworking-Service-1.0/coworking/halls
    {
    "placeName": "Paris"
    }
12. Adding the hall
    POST: http://localhost:8080/Coworking-Service-1.0/coworking/halls
    {
    "placeName": "Rome"
    }
13. Viewing all rooms in coworking
    GET: http://localhost:8080/Coworking-Service-1.0/coworking/rooms
14. Deleting the room
    DELETE: http://localhost:8080/Coworking-Service-1.0/coworking/rooms
    {
    "placeName": "Red",
    }
15. Adding the room
    POST: http://localhost:8080/Coworking-Service-1.0/coworking/rooms
    {
    "placeName": "Pink",
    }
16. Deleting the desk
    DELETE: http://localhost:8080/Coworking-Service-1.0/coworking/desks
    {
    "roomName": "Red",
    "deskNumber": "2"
    }
17. Adding the desk
    POST: http://localhost:8080/Coworking-Service-1.0/coworking/desks
    {
    "placeName": "Pink",
    }
18. Viewing all bookingDtos in coworking
    GET: http://localhost:8080/Coworking-Service-1.0/bookingDto/bookingDto/bydate