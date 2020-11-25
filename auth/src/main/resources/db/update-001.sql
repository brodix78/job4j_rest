create table employee (
   id serial primary key not null,
   firstname varchar(2000),
   secondname varchar(2000),
   inn varchar(20),
   date date
);

create table employee_person (
    employee_id int,
    person_id int,
    constraint fk_employee foreign key (employee_id) references employee(id),
    constraint fk_person foreign key (person_id) references person(id),
    constraint pr_key primary key (employee_id, person_id)
)