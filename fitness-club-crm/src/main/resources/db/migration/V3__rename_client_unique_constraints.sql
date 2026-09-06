ALTER TABLE client
    RENAME CONSTRAINT client_email_key TO uk_client_email;

ALTER TABLE client
    RENAME CONSTRAINT client_phone_key TO uk_client_phone;
