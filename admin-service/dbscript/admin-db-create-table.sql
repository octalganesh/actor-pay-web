CREATE TABLE actor_pay_admin_db.system_configuration (
  `id` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL,
`is_deleted` tinyint(1) DEFAULT 0,
`is_active` bit(1) DEFAULT NULL,
`updated_at` datetime DEFAULT NULL,
`service` varchar(255) DEFAULT NULL,
`param_description` varchar(255) DEFAULT NULL,
`param_name` varchar(255) NOT NULL,
`param_value` varchar(255) DEFAULT NULL,
`created_by` varchar(255) DEFAULT NULL,
`settings_type` varchar(255) DEFAULT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `param_name_UNIQUE` (`param_name`),
KEY `FKgienkle395iia05oipk1lw1md` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;