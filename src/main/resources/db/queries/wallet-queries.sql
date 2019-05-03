-- id: find-by-id
SELECT
    *
FROM
    WALLET
WHERE
    ID = :id;

-- id: lock-by-id
SELECT
    *
FROM
    WALLET
WHERE
    ID = :id
FOR UPDATE;

-- id: find-by-account
SELECT
    WLT.ID,
    WLT.ACCOUNT_ID,
    WLT.STATUS,
    WLT.CREATION_DATE,
    WLT.BALANCE,
    WLT.BALANCE_AVAILABLE,
    WLT.CURRENCY
FROM
    WALLET WLT
    JOIN ACCOUNT ACC
        ON WLT.ACCOUNT_ID = ACC.ID
WHERE
    ACC.ID = :accountId;

-- id: insert
INSERT INTO
    WALLET(
        ACCOUNT_ID,
        STATUS,
        CREATION_DATE,
        BALANCE,
        BALANCE_AVAILABLE,
        CURRENCY
    )
VALUES (
    :accountId,
    :status,
    :creationDate,
    :balance,
    :balanceAvailable,
    :currency
);

-- id: update
UPDATE
    WALLET
SET
    BALANCE = :balance,
    BALANCE_AVAILABLE = :balanceAvailable
WHERE
    ID = :id;