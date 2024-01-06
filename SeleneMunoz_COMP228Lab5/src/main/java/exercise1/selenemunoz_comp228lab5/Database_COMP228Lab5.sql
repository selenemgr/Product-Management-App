-- Database for Product Management App /  SeleneMunoz_COMP228Lab5Database

create database SeleneMunoz_COMP228Lab5Database;
use SeleneMunoz_COMP228Lab5Database;

drop table products;

create table products(
id int primary key,
name varchar(255),
description varchar(255),
price double,
category varchar(255)
);

select * from products;

delete from products where id=0;

