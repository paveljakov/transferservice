-- id: find-by-id
SELECT
    *
FROM
    ACCOUNT
WHERE
    ID = :id;

-- id: find-all
SELECT
    *
FROM
    ACCOUNT;

-- id: insert
INSERT INTO
    ACCOUNT(
        FIRST_NAME,
        LAST_NAME,
        EMAIL
    )
VALUES (
    :firstName,
    :lastName,
    :email
);