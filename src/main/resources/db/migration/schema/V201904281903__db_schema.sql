CREATE TABLE ACCOUNT
(
    ID         UUID DEFAULT random_uuid() PRIMARY KEY,
    FIRST_NAME NVARCHAR2(255) NOT NULL,
    LAST_NAME  NVARCHAR2(255) NOT NULL,
    EMAIL      NVARCHAR2(255) NOT NULL
);

CREATE UNIQUE INDEX IX_ACCOUNT_EMAIL ON ACCOUNT (EMAIL);

CREATE TABLE WALLET
(
    ID                UUID DEFAULT random_uuid() PRIMARY KEY,
    ACCOUNT_ID        UUID           NOT NULL,
    STATUS            VARCHAR(128)   NOT NULL,
    CREATION_DATE     TIMESTAMP      NOT NULL,
    BALANCE           DECIMAL(20, 2) NOT NULL,
    BALANCE_AVAILABLE DECIMAL(20, 2) NOT NULL,
    CURRENCY          CHAR(3)        NOT NULL,

    CONSTRAINT FK_WALLET_ACCOUNT FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT (ID)
);

CREATE INDEX IX_WALLET_ACCOUNT ON WALLET (ACCOUNT_ID);
CREATE UNIQUE INDEX IX_WALLET_CURRENCY_ACCOUNT ON WALLET (ACCOUNT_ID, CURRENCY);

CREATE TABLE TRANSACTION_LOG
(
    ID                 UUID DEFAULT random_uuid() PRIMARY KEY,
    STATUS             VARCHAR(128)   NOT NULL,
    CREATION_DATE      TIMESTAMP      NOT NULL,
    EXECUTION_DATE     TIMESTAMP,
    AUTHORIZATION_DATE TIMESTAMP,
    SENDER_WALLET_ID   UUID           NOT NULL,
    RECEIVER_WALLET_ID UUID           NOT NULL,
    AMOUNT             DECIMAL(20, 2) NOT NULL,
    AUTHORIZED_AMOUNT  DECIMAL(20, 2),

    CONSTRAINT FK_TRANSACTION_LOG_SENDER_WALLET_ID FOREIGN KEY (SENDER_WALLET_ID) REFERENCES WALLET (ID),
    CONSTRAINT FK_TRANSACTION_LOG_RECEIVER_WALLET_ID FOREIGN KEY (RECEIVER_WALLET_ID) REFERENCES WALLET (ID)
);

CREATE INDEX IX_TRANSACTION_LOG_SENDER_WALLET_ID ON TRANSACTION_LOG (SENDER_WALLET_ID);
CREATE INDEX IX_TRANSACTION_LOG_RECEIVER_WALLET_ID ON TRANSACTION_LOG (RECEIVER_WALLET_ID);