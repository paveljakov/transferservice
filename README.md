# Demo RESTful money transfer service

Service is implemented using following technology stack:
* Spark Java
* Dagger 2
* FluentJDBC
* H2

## API documentation
| Path                               | HTTP method | Example request/response  |
|------------------------------------|-------------|---------------------------|
| /accounts                          | GET         | Get all accounts           |
| /accounts                          | PUT         | Add new account           |
| /accounts/{accountId}              | GET         | Get account by ID         |
| /accounts/{accountId}/wallets      | GET         | Get account wallets       |
| /accounts/{accountId}/wallets      | PUT         | Add new wallet to account |
| /accounts/{accountId}/transactions | GET         | Get account transactions  |
| /wallets/{walletId}                | GET         | Get wallet by ID          |
| /wallets/{walletId}                | POST        | Add funds to wallet       |
| /wallets/{walletId}/transactions   | GET         | Get wallet transactions   |
| /transactions/{transactionId}      | GET         | Get transaction by ID     |
| /transactions                      | PUT         | Create new transaction    |

### Get all accounts
`GET /accounts`

Response:
```
[ {
  "id" : "e3360d34-12ea-4caf-a201-a8a5c3b0ebbc",
  "firstName" : "Sample",
  "lastName" : "User1",
  "email" : "sample.user1@none.com"
}, {
  "id" : "36fee24f-e02e-4673-88e7-48b6e9194040",
  "firstName" : "Sample",
  "lastName" : "User2",
  "email" : "sample.user2@none.com"
}, {
  "id" : "c9840e5a-09f5-4a25-a6c6-8461cc60a3de",
  "firstName" : "Sample",
  "lastName" : "User3",
  "email" : "sample.user3@none.com"
} ]
```

### Add new account
`PUT /accounts`

Request:
```
{
  "firstName" : "Sample",
  "lastName" : "User3",
  "email" : "sample.user3@none.com"
}
```
Response:
```
{
    "id": "c9840e5a-09f5-4a25-a6c6-8461cc60a3de"
}
```

### Get account by ID
`GET /accounts/{accountId}`

Response:
```
{
  "id" : "36fee24f-e02e-4673-88e7-48b6e9194040",
  "firstName" : "Sample",
  "lastName" : "User2",
  "email" : "sample.user2@none.com"
}
```

### Get account wallets
`GET /accounts/{accountId}/wallets`

Response:
```
[ {
  "id" : "733c9bbd-d49f-43e9-b1f7-417cd9e59813",
  "accountId" : "36fee24f-e02e-4673-88e7-48b6e9194040",
  "status" : "ACTIVE",
  "creationDate" : "2019-04-27T22:20:59.123",
  "balance" : 199.29,
  "balanceAvailable" : 199.29,
  "currency" : "EUR"
}, {
  "id" : "8dca1834-6664-4049-921d-832ac3403a6a",
  "accountId" : "36fee24f-e02e-4673-88e7-48b6e9194040",
  "status" : "ACTIVE",
  "creationDate" : "2019-04-27T22:20:59.123",
  "balance" : 5.29,
  "balanceAvailable" : 5.29,
  "currency" : "USD"
} ]
```

### Add new wallet to account
`PUT /accounts/{accountId}/wallets`

Request:
```
{
  "currency" : "USD"
}
```
Response:
```
{
    "id": "c9840e5a-09f5-4a25-a6c6-8461cc60a3de"
}
```

### Get account transactions
`GET /accounts/{accountId}/transactions`

Response:
```
[ {
  "id" : "c7642515-0eac-4a2b-a566-77bc249d39a1",
  "status" : "CONFIRMED",
  "creationDate" : "2019-04-28T13:20:32.005",
  "executionDate" : "2019-04-28T13:20:32.007",
  "authorizationDate" : "2019-04-28T13:20:32.006",
  "senderWalletId" : "733c9bbd-d49f-43e9-b1f7-417cd9e59813",
  "receiverWalletId" : "fd5832e1-03d4-45a5-9bbf-2829ef6ebebb",
  "amount" : 50.00,
  "authorizedAmount" : 50.00
}, {
  "id" : "b9d59f3f-d7ee-4d90-80df-9e5fa59db056",
  "status" : "AUTHORIZED",
  "creationDate" : "2019-04-28T13:20:33.005",
  "executionDate" : null,
  "authorizationDate" : "2019-04-28T13:20:33.006",
  "senderWalletId" : "fd5832e1-03d4-45a5-9bbf-2829ef6ebebb",
  "receiverWalletId" : "733c9bbd-d49f-43e9-b1f7-417cd9e59813",
  "amount" : 50.00,
  "authorizedAmount" : 50.00
}, {
  "id" : "bc464d5d-db36-49ff-9032-d41fcc0e87a5",
  "status" : "PENDING",
  "creationDate" : "2019-04-28T13:20:34.005",
  "executionDate" : null,
  "authorizationDate" : null,
  "senderWalletId" : "fd5832e1-03d4-45a5-9bbf-2829ef6ebebb",
  "receiverWalletId" : "733c9bbd-d49f-43e9-b1f7-417cd9e59813",
  "amount" : 50.00,
  "authorizedAmount" : null
} ]
```

### Get wallet by ID
`GET /wallets/{walletId}`

Response:
```
{
  "id" : "733c9bbd-d49f-43e9-b1f7-417cd9e59813",
  "accountId" : "36fee24f-e02e-4673-88e7-48b6e9194040",
  "status" : "ACTIVE",
  "creationDate" : "2019-04-27T22:20:59.123",
  "balance" : 199.29,
  "balanceAvailable" : 199.29,
  "currency" : "EUR"
}
```

### Add funds to wallet
`POST /wallets/{walletId}`

Request:
```
{
  "amount" : 100.00
}
```

### Get wallet transactions
`GET /wallets/{walletId}/transactions`

Response:
```
[ {
  "id" : "c7642515-0eac-4a2b-a566-77bc249d39a1",
  "status" : "CONFIRMED",
  "creationDate" : "2019-04-28T13:20:32.005",
  "executionDate" : "2019-04-28T13:20:32.007",
  "authorizationDate" : "2019-04-28T13:20:32.006",
  "senderWalletId" : "733c9bbd-d49f-43e9-b1f7-417cd9e59813",
  "receiverWalletId" : "fd5832e1-03d4-45a5-9bbf-2829ef6ebebb",
  "amount" : 50.00,
  "authorizedAmount" : 50.00
}, {
  "id" : "b9d59f3f-d7ee-4d90-80df-9e5fa59db056",
  "status" : "AUTHORIZED",
  "creationDate" : "2019-04-28T13:20:33.005",
  "executionDate" : null,
  "authorizationDate" : "2019-04-28T13:20:33.006",
  "senderWalletId" : "fd5832e1-03d4-45a5-9bbf-2829ef6ebebb",
  "receiverWalletId" : "733c9bbd-d49f-43e9-b1f7-417cd9e59813",
  "amount" : 50.00,
  "authorizedAmount" : 50.00
}, {
  "id" : "bc464d5d-db36-49ff-9032-d41fcc0e87a5",
  "status" : "PENDING",
  "creationDate" : "2019-04-28T13:20:34.005",
  "executionDate" : null,
  "authorizationDate" : null,
  "senderWalletId" : "fd5832e1-03d4-45a5-9bbf-2829ef6ebebb",
  "receiverWalletId" : "733c9bbd-d49f-43e9-b1f7-417cd9e59813",
  "amount" : 50.00,
  "authorizedAmount" : null
} ]
```

### Get transaction by ID
`GET /transactions/{transactionId}`

Response:
```
{
  "id" : "c7642515-0eac-4a2b-a566-77bc249d39a1",
  "status" : "CONFIRMED",
  "creationDate" : "2019-04-28T13:20:32.005",
  "executionDate" : "2019-04-28T13:20:32.007",
  "authorizationDate" : "2019-04-28T13:20:32.006",
  "senderWalletId" : "733c9bbd-d49f-43e9-b1f7-417cd9e59813",
  "receiverWalletId" : "fd5832e1-03d4-45a5-9bbf-2829ef6ebebb",
  "amount" : 50.00,
  "authorizedAmount" : 50.00
}
```

### Create new transaction
`PUT /transactions`

Request:
```
{
  "senderWalletId" : "733c9bbd-d49f-43e9-b1f7-417cd9e59813",
  "receiverWalletId" : "fd5832e1-03d4-45a5-9bbf-2829ef6ebebb",
  "amount" : 50.00
}
```
Response:
```
{
    "id": "c9840e5a-09f5-4a25-a6c6-8461cc60a3de"
}
```
