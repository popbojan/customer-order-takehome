create table product_offerings (
    id varchar(64) primary key,
    name varchar(255) not null,
    price numeric(12, 2) not null
);

insert into product_offerings (id, name, price) values
    ('po-1', 'Fiber Internet 100', 29.99),
    ('po-2', 'Fiber Internet 500', 49.99),
    ('po-3', 'Mobile Unlimited', 19.99);
