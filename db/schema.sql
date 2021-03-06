CREATE TABLE USERS (
  ID BIGSERIAL PRIMARY KEY,
  USERNAME VARCHAR(64) UNIQUE NOT NULL,
  PASSWORD VARCHAR(128) NOT NULL,
  CREATION_TIME TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  LAST_LOGIN_TIME TIMESTAMP
);

CREATE TABLE ACCOUNT (
  ID BIGSERIAL PRIMARY KEY,
  USERS_ID BIGSERIAL NOT NULL REFERENCES USERS(ID),
  NAME VARCHAR(128) NOT NULL,
  DESCRIPTION VARCHAR(4096),
  CREATION_TIME TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TYPE EVENT_TYPE AS ENUM ('transfer', 'change');

CREATE TABLE EVENT (
  ID BIGSERIAL PRIMARY KEY,
  ACCOUNT_ID BIGSERIAL NOT NULL REFERENCES ACCOUNT(ID),
  DESCRIPTION VARCHAR(128),
  AMOUNT NUMERIC(14,2) NOT NULL,
  CUMULATIVE_AMOUNT NUMERIC(14,2),
  CHANGE_TYPE VARCHAR(32) NOT NULL,
  EVENT_DATE DATE NOT NULL
);

CREATE FUNCTION updateCumulativeInsert() RETURNS TRIGGER AS '
  BEGIN
    UPDATE event SET cumulative_amount = cumulative_amount + NEW.amount 
      WHERE (event_date > NEW.event_date OR (event_date = NEW.event_date AND id > NEW.id)) AND account_id = NEW.account_id;
    IF EXISTS (SELECT * FROM event WHERE (event_date < NEW.event_date OR (event_date = NEW.event_date AND id < NEW.id)) AND account_id = NEW.account_id) THEN
      UPDATE event SET cumulative_amount = (
        SELECT cumulative_amount FROM event WHERE
          (event_date < NEW.event_date OR (event_date = NEW.event_date AND id < NEW.id)) AND account_id = NEW.account_id
          ORDER BY event_date DESC, id DESC
          LIMIT 1
      ) + NEW.amount WHERE id = NEW.id;
    ELSE
	  UPDATE event SET cumulative_amount = amount WHERE id = NEW.id;
    END IF;
    RETURN NULL;
  END'
LANGUAGE plpgsql;

CREATE TRIGGER UPDATE_CUMULATIVE AFTER INSERT ON EVENT FOR EACH ROW EXECUTE PROCEDURE updateCumulativeInsert();

CREATE FUNCTION updateCumulativeDelete() RETURNS TRIGGER AS '
  BEGIN
    UPDATE event SET cumulative_amount = cumulative_amount - OLD.amount 
      WHERE (event_date > OLD.event_date OR (event_date = OLD.event_date AND id > OLD.id)) AND account_id = OLD.account_id;
    RETURN NULL;
  END'
LANGUAGE plpgsql;

CREATE TRIGGER UPDATE_CUMULATIVE_DEL AFTER DELETE ON EVENT FOR EACH ROW EXECUTE PROCEDURE updateCumulativeDelete();