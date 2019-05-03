-- id: find-by-id
SELECT
    *
FROM
    TRANSACTION_LOG
WHERE
    ID = :id;

-- id: lock-by-id
SELECT
    *
FROM
    TRANSACTION_LOG
WHERE
    ID = :id
FOR UPDATE;

-- id: find-by-wallet
SELECT
    TX.ID,
    TX.STATUS,
    TX.CREATION_DATE,
    TX.EXECUTION_DATE,
    TX.AUTHORIZATION_DATE,
    TX.SENDER_WALLET_ID,
    TX.RECEIVER_WALLET_ID,
    TX.AMOUNT,
    TX.AUTHORIZED_AMOUNT
FROM
    TRANSACTION_LOG TX
        JOIN WALLET WLT
            ON TX.SENDER_WALLET_ID = WLT.ID
                OR TX.RECEIVER_WALLET_ID = WLT.ID
WHERE
    WLT.ID = :walletId;

-- id: find-by-account
SELECT
    TX.ID,
    TX.STATUS,
    TX.CREATION_DATE,
    TX.EXECUTION_DATE,
    TX.AUTHORIZATION_DATE,
    TX.SENDER_WALLET_ID,
    TX.RECEIVER_WALLET_ID,
    TX.AMOUNT,
    TX.AUTHORIZED_AMOUNT
FROM
    TRANSACTION_LOG TX
        JOIN WALLET WLT
             ON TX.SENDER_WALLET_ID = WLT.ID
                 OR TX.RECEIVER_WALLET_ID = WLT.ID
WHERE
    WLT.ACCOUNT_ID = :accountId;

-- id: insert
INSERT INTO
    TRANSACTION_LOG(
        STATUS,
        CREATION_DATE,
        EXECUTION_DATE,
        AUTHORIZATION_DATE,
        SENDER_WALLET_ID,
        RECEIVER_WALLET_ID,
        AMOUNT,
        AUTHORIZED_AMOUNT
    )
VALUES (
    :status,
    :creationDate,
    :executionDate,
    :authorizationDate,
    :senderWalletId,
    :receiverWalletId,
    :amount,
    :authorizedAmount
);

-- id: update
UPDATE
    TRANSACTION_LOG
SET
    STATUS = :status,
    EXECUTION_DATE = :executionDate,
    AUTHORIZATION_DATE = :authorizationDate,
    AUTHORIZED_AMOUNT = :authorizedAmount
WHERE
    ID = :id;


