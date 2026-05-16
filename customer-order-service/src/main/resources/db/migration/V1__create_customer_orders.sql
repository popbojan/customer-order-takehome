create table customer_orders (
    id uuid primary key,
    state varchar(32) not null,
    category varchar(16) not null,
    customer_id varchar(255) not null,
    site_id varchar(255) not null,
    payment_type varchar(32) not null,
    payment_iban varchar(64),
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table customer_order_items (
    order_id uuid not null references customer_orders(id) on delete cascade,
    position integer not null,
    product_offering_id varchar(255) not null,
    quantity integer not null,
    primary key (order_id, position)
);

create table idempotency_records (
    idempotency_key varchar(255) primary key,
    payload_hash varchar(64) not null,
    order_id uuid not null references customer_orders(id),
    created_at timestamptz not null
);

create index idx_customer_orders_category on customer_orders(category);
create index idx_customer_orders_created_at on customer_orders(created_at);
create index idx_idempotency_records_order_id on idempotency_records(order_id);
