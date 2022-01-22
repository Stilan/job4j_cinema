CREATE TABLE account (
  id SERIAL PRIMARY KEY,
  username VARCHAR NOT NULL,
  email VARCHAR NOT NULL UNIQUE,
  phone VARCHAR NOT NULL UNIQUE
);

CREATE TABLE ticket (
    id SERIAL PRIMARY KEY,
    session_id INT NOT NULL,
    row INT NOT NULL,
    cell INT NOT NULL,
    account_id INT NOT NULL REFERENCES account(id),
    UNIQUE(session_id, row, cell)
);

DROP TABLE ticket;

SELECT * FROM account;
SELECT * FROM ticket;