-- Order validation condition for CANCELLED update
-- ===============================================
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('CANCELLED','SUCCESS,READY','customer');
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('CANCELLED','SUCCESS,READY','admin');
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('CANCELLED','SUCCESS,READY','merchant');

-- Order validation condition for RETURNING update
-- ===============================================
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('RETURNING','DISPATCHED,DELIVERED','customer');
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('RETURNING','DISPATCHED,DELIVERED','merchant');
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('RETURNING','DISPATCHED,DELIVERED','admin');

-- Order validation condition for RETURNED update
-- ===============================================
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('RETURNED','ADMIN,MERCHANT','customer');
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('RETURNED','ADMIN,MERCHANT','merchant');

-- Order validation condition for READY update
-- ===============================================
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('READY','SUCCESS','admin');
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('READY','SUCCESS','merchant');

-- Order validation condition for DISPATCHED update
-- ===============================================
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('DISPATCHED','READY','admin');
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('DISPATCHED','READY','merchant');


-- Order validation condition for DELIVERED update
-- ===============================================
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('DELIVERED','DISPATCHED','admin');
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('DELIVERED','DISPATCHED','merchant');

-- Order validation condition for SUCCESS  update
-- ===============================================
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('SUCCESS', "", 'merchant');


-- Order validation condition for PENDING  update
-- ===============================================
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('PENDING',"",'admin');

-- Order validation condition for FAILED  update
-- ===============================================
insert into order_validation (order_status_to_be_update,order_status_condition,user_type)
values('FAILED',"",'customer');
