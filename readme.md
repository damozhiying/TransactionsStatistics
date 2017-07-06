Simple RESTful API to handle and calcualte the statistics of current transactions.

### Spec
##### POST /transactions

Every Time a new transaction happened, this endpoint will be called. 

The body:

```bash
{
    "amount": 12.3,
    "timestamp": 1478192204000
}
```

where:

 - ```amount``` - transaction amount
 - ```timestamp``` - transaction time in epoch in millis in UTC time zone (this is not current
                  timestamp)
                  
Returns:

 - ```201``` in case of success
 - ```204``` if transaction is older than specified period of time
 - ```400``` if transaction is from future or amount is zero
 
 
##### GET /statistics

Returns:

```bash
{
    "sum": 1000,
    "avg": 100,
    "max": 200,
    "min": 50,
    "count": 10
}
```
Where:

 - ```sum``` is a double specifying the total sum of transaction value in the period of time
 - ```avg``` is a double specifying the average amount of transaction value in the lperiod of time
 - ```max``` is a double specifying single highest transaction value in the period of time
 - ```min``` is a double specifying single lowest transaction value in the period of time
 - ```count``` is a long specifying the total number of transactions happened in the period of time
 
Aggregation period given in seconds and specified in application.properties. Default is 60s.
   
The main requirement is to make the ```GET /statistics``` execute in constant time and space.

This requirement supported by in-memory cache implemented with Deque, whose last elements are constantly checked for outdated timestamps.
GET request guarantees 20 correct responses per second within the specified period of time. 
The higher the load, the more transactions can be in response - but all of them will have no more than 20ms difference between each other.

Thread safety is guaranteed by synchronized blocks on write and CAS on read.

### Testing

Run local server with ```gradle bootRun```, after that you can peform request to localhost:8080:

```bash
curl http://localhost:8080/statistics
```

```bash
curl -v -H "Content-Type: application/json" -X POST -d '{"amount":10,"timestamp":1499372377767}' http://localhost:8080/transactions
```
