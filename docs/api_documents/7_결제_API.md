# 7. 결제 API
예약 Id 리스트를 사용하여, 결제를 진행합니다.

<br>

### POST
```method
 /reservations/purchase
```
<br>

### HEADERS
| Name          | Value               |
|---------------|---------------------|
| Content-Type  | application/json    |

<br>




### BODY
| Field             | Type          | Description                  |
|--------------------|---------------|------------------------------|
| userId            | Integer       | 사용자 ID                     |
| tokenId           | Integer       | 대기열 토큰 ID                 |
| reservation_list  | Array<Integer>| 예약 ID 리스트                 |


<br>


### RESPONSE

**STATUS : 200**
```json
{
  "message":"Purchase Sucess!",
  "quantity":2,
  "reservation_list":[
    {
      "reservation_id":2,
      "seat_num":14,
      "concert_name":"서커스!",
      "concert_performer":"황광대",
      "concert_time":"2025-03-01T18:00:00",
      "price":12000
    },
    {
      "reservation_id":3,
      "seat_num":18,
      "concert_name":"서커스!",
      "concert_performer":"황광대",
      "concert_time":"2025-03-01T18:00:00",
      "price":12000
    }    
  ]
}
```

**STATUS : 404 : NOT FOUND USER**
```json
{
  "message":"This User isn't valid",
  "dateTime":"2025-01-31:12:00:00"
}
```
**STATUS : 404 : NOT FOUND RESERVATION**
```json
{
  "message":"This Reservation isn't valid",
  "dateTime":"2025-01-31:12:00:00"
}
```
**STATUS : 404 : NOT VALID TOKEN**
```json
{
  "message":"This Token isn't valid",
  "dateTime":"2025-01-31:12:00:00"
}
```
**STATUS : 500 : SERVER ERROR**
```json
{
  "message":"Server Error",
  "dateTime":"2025-01-31:12:00:00"
}
```