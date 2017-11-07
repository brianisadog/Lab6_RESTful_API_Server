# Lab6_RESTful_API_Server

Two multi-threaded RESTful API servers to access hotel information and provide JSON result. Implement with Socket and Jetty separately.

## How to use

There are two .jar file in */out/artifacts/Lab6_jar* represent two identical RESTful API servers, but one implemented by using Socket *(port:3000)*, another one implemented by using Jetty *(port:3050)*.

```
Run it and try:
* Hotel information:
	Socket:	http://localhost:3000/hotelInfo?hotelId=2336
	Jetty:	http://localhost:3050/hotelInfo?hotelId=2336

* Hotel reviews:
	Socket:	http://localhost:3000/reviews?hotelId=10323&num=20
	Jetty:	http://localhost:3050/reviews?hotelId=10323&num=20

* Attractions near hotel(Google Place API):
	Socket:	http://localhost:3000/attractions?hotelId=25622&radius=3
	Jetty:	http://localhost:3050/attractions?hotelId=25622&radius=3

* Attractions/Things-to-do near hotel(Expedia Scraper):
	Socket:	http://localhost:3000/attractions?hotelId=662368&radius=3
	Jetty:	http://localhost:3050/attractions?hotelId=662368&radius=3
```

## Running the test

You can simplely use python3 to run lab6Test.py to test the servers.
```
python3 lab6Test.py localhost 3000
```

## Other things

* All hotel details and reviews provided in */input*.
* Two scraper samples provided in */test*.
* More detail please see Java document in */JavaDoc*.

## Authors

* **Brian Sung** - *Computer Science Master student* - [LinkedIn](https://www.linkedin.com/in/brianisadog/)

## References
* [Google Place API](https://developers.google.com/places/web-service/search)
* [Expedia](https://www.expedia.com/Activities)

## Acknowledgment

Not using for any commercial purpose.