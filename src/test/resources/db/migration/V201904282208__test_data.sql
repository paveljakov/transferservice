INSERT INTO ACCOUNT(ID, FIRST_NAME, LAST_NAME, EMAIL)
VALUES ('33d7199f-0e9d-4bd0-baff-2087c3a6f152', 'Sample', 'User1', 'sample.user1@none.com');

INSERT INTO ACCOUNT(ID, FIRST_NAME, LAST_NAME, EMAIL)
VALUES ('8a898c75-9b03-4ecd-9019-65bdebe41de9', 'Sample', 'User2', 'sample.user2@none.com');

INSERT INTO WALLET(ID, ACCOUNT_ID, STATUS, CREATION_DATE, BALANCE, BALANCE_AVAILABLE, CURRENCY)
VALUES ('d2410f40-f9fd-40c5-8902-ea3ede77c7cd',
        '33d7199f-0e9d-4bd0-baff-2087c3a6f152',
        'ACTIVE',
        '2019-04-27 22:20:59.123',
        99.99,
        99.99,
        'EUR');

INSERT INTO WALLET(ID, ACCOUNT_ID, STATUS, CREATION_DATE, BALANCE, BALANCE_AVAILABLE, CURRENCY)
VALUES ('8ba41a90-2f7c-465d-b4ff-990420138e22',
        '8a898c75-9b03-4ecd-9019-65bdebe41de9',
        'ACTIVE',
        '2019-04-27 22:20:59.123',
        199.29,
        199.29,
        'EUR');

INSERT INTO WALLET(ID, ACCOUNT_ID, STATUS, CREATION_DATE, BALANCE, BALANCE_AVAILABLE, CURRENCY)
VALUES ('88bc98f8-cc8c-4b3f-a92e-c4a423ed807f',
        '8a898c75-9b03-4ecd-9019-65bdebe41de9',
        'ACTIVE',
        '2019-04-27 22:20:59.123',
        5.29,
        5.29,
        'USD');

INSERT INTO TRANSACTION_LOG(ID, STATUS, CREATION_DATE, EXECUTION_DATE, AUTHORIZATION_DATE, SENDER_WALLET_ID, RECEIVER_WALLET_ID, AMOUNT,
                            AUTHORIZED_AMOUNT)
VALUES ('0c66f551-21b8-41c2-91a2-44cedf93753c',
        'CONFIRMED',
        '2019-04-28 13:20:32.005',
        '2019-04-28 13:20:32.005',
        NULL,
        '8ba41a90-2f7c-465d-b4ff-990420138e22',
        'd2410f40-f9fd-40c5-8902-ea3ede77c7cd',
        50.00,
        NULL);