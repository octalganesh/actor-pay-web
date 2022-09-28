INSERT INTO actor_pay_user_service_db.order_validation VALUES
(1,'CANCELLED','SUCCESS,READY','customer'),
(2,'CANCELLED','SUCCESS,READY','admin'),
(3,'CANCELLED','SUCCESS,READY','merchant'),
(4,'READY','SUCCESS','admin'),
(5,'READY','SUCCESS','merchant'),
(6,'DISPATCHED','READY','admin'),
(7,'DISPATCHED','READY','merchant'),
(8,'DELIVERED','DISPATCHED','admin'),
(9,'DELIVERED','DISPATCHED','merchant'),
(10,'SUCCESS','','merchant'),
(11,'PENDING','','admin'),
(12,'FAILED','','customer'),
(13,'RETURNING','DISPATCHED,DELIVERED','customer'),
(14,'RETURNING','DISPATCHED,DELIVERED','merchant'),
(15,'RETURNING','DISPATCHED,DELIVERED','admin'),
(16,'RETURNING_ACCEPTED','RETURNING','merchant'),
(17,'RETURNING_ACCEPTED','RETURNING','admin'),
(18,'RETURNING_DECLINED ','RETURNING','merchant'),
(19,'RETURNED','RETURNING_ACCEPTED,RETURNING_DECLINED','merchant'),
(21,'RETURNED','RETURNING_ACCEPTED,RETURNING_DECLINED','admin');