create table runners (
    nickname varchar(100) primary key,
    runner_name varchar(250) not null,
    surname varchar(250) not null,
    email varchar(255) not null,
    birth_date date not null,
    subscription_date date not null,
    last_race varchar(250)
);

ALTER TABLE runners
ADD CONSTRAINT email_unique UNIQUE (email);