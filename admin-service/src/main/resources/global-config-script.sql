INSERT INTO  actor_pay_admin_db.system_configuration
(id, created_at, is_deleted, is_active, param_description, param_name, param_value, created_by)
VALUES
( '9ba1f024-c1d3-4e60-9f5a-60c0c6773f0e',now(),false,true,'Time duration for returning the Product',
'return_days',5,'76da3052-c83f-4e1c-9129-4ca0498d4ce1'),
( '8d86c68c-7de1-11ec-90d6-0242ac120003',now(),false,true,'Return Fee',
'return-fee',7,'76da3052-c83f-4e1c-9129-4ca0498d4ce1'),
( 'c8ac147e-7de1-11ec-90d6-0242ac120003',now(),false,true,'Admin Commission',
'admin-commission',9,'76da3052-c83f-4e1c-9129-4ca0498d4ce1'),
( 'ee9ebdc2-7de0-11ec-90d6-0242ac120003',now(),false,true,'Cancellation Fee',
'cancellation-fee',5,'76da3052-c83f-4e1c-9129-4ca0498d4ce1');