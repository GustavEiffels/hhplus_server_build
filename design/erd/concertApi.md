```mermaid
---
title: Concert API
---
erDiagram
    User {
        int id PK
        string name
        int point
        datetime createAt
        datetime updateAt
    }
    Seat {
        int id PK
        string ConcertSchedule
        string status "reserved (default), reservable"
        int number
        decimal price
        datetime createAt
        datetime updateAt
        datetime expiredAt
    }
    Reservation {
        int id PK
        int userId FK
        int ConcertScheduleId FK
        string status "reserved (default), cancel, purchase"
        datetime createAt
        datetime updateAt
    }
    QueueToken {
        int id PK
        int userId FK
        string status "wait (default), active, delete"
        datetime expireAt "Token expiration time"
        datetime createAt
        datetime updateAt
    }

    Concert {
        int id PK
        string title
        string performer        
    }

    ConcertSchedule {
        int id PK
        int concertId FK
        datetime showTime "Performance start time"
        datetime reservationStart
        datetime reservationEnd
        int leftTicket
    }

    User ||--o{ Reservation : "has"
    User ||--o{ QueueToken  : "has"
    Reservation ||--|| ConcertSchedule : "references"
    Concert ||--o{ ConcertSchedule : "has"
    ConcertSchedule ||--o{ Seat : "has"
        
```