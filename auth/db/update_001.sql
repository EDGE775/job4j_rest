create table employee
(
    id          serial primary key not null,
    first_name  varchar(2000),
    second_name varchar(2000),
    inn         int,
    created     timestamp
);

create table person
(
    id       serial primary key not null,
    login    varchar(2000),
    password varchar(2000)
);

create table employee_person
(
    id          serial primary key not null,
    employee_id int,
    person_id   int,
    FOREIGN KEY (employee_id) references employee (id),
    FOREIGN KEY (person_id) references person (id)
);

insert into employee (first_name, second_name, inn)
values ('dmitry', 'hlapov', '123456789');

insert into person (login, password)
values ('dmitry', '123');

insert into employee_person (employee_id, person_id)
values (1, 1);
