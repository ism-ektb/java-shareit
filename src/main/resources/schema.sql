CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS as IDENTITY PRIMARY KEY,
    name_user VARCHAR(100),
    email VARCHAR(320)
    );
CREATE TABLE IF NOT EXISTS item_requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description VARCHAR(1000),
    requestor_id BIGINT,
    created TIMESTAMP,
    CONSTRAINT fk_user_id FOREIGN KEY(requestor_id) REFERENCES users(id)
);
CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name_item VARCHAR(100),
    description VARCHAR(1000),
    available BOOLEAN,
    owner_id BIGINT,
    request_id BIGINT,
    CONSTRAINT fk_owner_id FOREIGN KEY(owner_id) REFERENCES users(id),
    CONSTRAINT fk_reqiests_id FOREIGN KEY(request_id) REFERENCES item_requests(id)
);
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    item_id BIGINT,
    booker_id BIGINT,
    status VARCHAR(50),
    CONSTRAINT fr_item_id FOREIGN KEY(item_id) REFERENCES items(id),
    CONSTRAINT fr_booker_id FOREIGN KEY(booker_id) REFERENCES users(id)
);
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text VARCHAR(1000),
    item_id BIGINT,
    author_id BIGINT,
    created TIMESTAMP,
    CONSTRAINT fr_item_comment_id FOREIGN KEY(item_id) REFERENCES items(id),
    CONSTRAINT fr_author_id FOREIGN KEY(author_id) REFERENCES users(id)
);