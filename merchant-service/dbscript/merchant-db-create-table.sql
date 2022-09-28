
DROP TABLE IF EXISTS permission;

CREATE TABLE permission (
  id varchar(255) NOT NULL,
  created_at datetime DEFAULT NULL,
  is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
name varchar(255) DEFAULT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table address
--

DROP TABLE IF EXISTS address;

CREATE TABLE address (
id varchar(255) NOT NULL,
created_at datetime DEFAULT NULL,
is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
address_line1 varchar(255) DEFAULT NULL,
address_line2 varchar(255) DEFAULT NULL,
city varchar(255) DEFAULT NULL,
country varchar(100) DEFAULT NULL,
latitude varchar(100) DEFAULT NULL,
longitude varchar(100) DEFAULT NULL,
state varchar(100) DEFAULT NULL,
zip_code varchar(10) DEFAULT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table contacts
--

DROP TABLE IF EXISTS contacts;

CREATE TABLE contacts (
id varchar(255) NOT NULL,
created_at datetime DEFAULT NULL,
is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
email varchar(255) DEFAULT NULL,
extension_number varchar(255) DEFAULT NULL,
fax_number varchar(255) DEFAULT NULL,
is_notification_active bit(1) DEFAULT NULL,
phone_number varchar(255) DEFAULT NULL,
second_phone_number varchar(255) DEFAULT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table role
--
DROP TABLE IF EXISTS role;

CREATE TABLE role (
  id varchar(255) NOT NULL,
  created_at datetime DEFAULT NULL,
  is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
description varchar(500) DEFAULT NULL,
name varchar(30) DEFAULT NULL,
user_id VARCHAR(255) DEFAULT NULL,
PRIMARY KEY (id),
UNIQUE KEY name_UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table users
--

DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id varchar(255) NOT NULL,
  created_at datetime DEFAULT NULL,
  is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
contact_number varchar(255) NOT NULL,
date_of_birth date DEFAULT NULL,
default_password bit(1) NOT NULL,
email varchar(255) NOT NULL,
is_email_verified bit(1) DEFAULT NULL,
extension_number varchar(255) DEFAULT NULL,
fcm_token varchar(255) DEFAULT NULL,
first_name varchar(255) DEFAULT NULL,
gender varchar(255) DEFAULT NULL,
invalid_login_attempts int(11) DEFAULT NULL,
is_kyc_done bit(1) DEFAULT NULL,
is_merchant bit(1) DEFAULT NULL,
is_notification_active bit(1) DEFAULT NULL,
last_login datetime DEFAULT NULL,
last_name varchar(255) DEFAULT NULL,
latitude bigint(20) DEFAULT NULL,
longitude bigint(20) DEFAULT NULL,
password varchar(255) NOT NULL,
is_phone_verified bit(1) DEFAULT NULL,
profile_picture_url varchar(255) DEFAULT NULL,
upi_qr_code varchar(255) DEFAULT NULL,
user_type varchar(255) DEFAULT NULL,
user_name varchar(255) DEFAULT NULL,
role_id varchar(255) DEFAULT NULL,
temp varchar(255) DEFAULT NULL,
kyc_status varchar(255) DEFAULT NULL,
aadhar_number varchar(255) DEFAULT NULL,
pan_number varchar(255) DEFAULT NULL,
PRIMARY KEY (id),
UNIQUE KEY UK_n14mnjfh0j5psw8omlegp1d68 (contact_number),
UNIQUE KEY UK_6dotkott2kjsp8vw4d0m25fb7 (email),
KEY FK4qu1gr772nnf6ve5af002rwya (role_id),
CONSTRAINT FK4qu1gr772nnf6ve5af002rwya FOREIGN KEY (role_id) REFERENCES role (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



--
-- Table structure for table users_document
--

CREATE TABLE user_document (
id varchar(255) NOT NULL,
created_at datetime DEFAULT NULL,
is_deleted tinyint(1) DEFAULT 0,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
doc_type varchar(255) DEFAULT NULL,
document_data longtext DEFAULT NULL,
verified_status varchar(15) DEFAULT NULL,
user_id varchar(255) DEFAULT NULL,
id_no varchar(255) DEFAULT NULL,
PRIMARY KEY (id),
UNIQUE KEY user_doc_ukey (id_no),
KEY user_fk_user_doc (user_id),
CONSTRAINT user_fk_user_doc FOREIGN KEY (user_id) REFERENCES users (id)
);
--
-- Table structure for table merchant_details
--

DROP TABLE IF EXISTS merchant_details;

CREATE TABLE merchant_details (
id varchar(255) NOT NULL,
created_at datetime DEFAULT NULL,
is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
business_name varchar(255) NOT NULL,
full_address varchar(255) DEFAULT NULL,
licence_number varchar(255) NOT NULL,
merchant_type varchar(255) DEFAULT NULL,
resource_type varchar(255) DEFAULT NULL,
shop_address varchar(255) DEFAULT NULL,
user_id varchar(255) DEFAULT NULL,
PRIMARY KEY (id),
KEY FKe3tj0gtbb50ercpeyex8vqvbo (user_id),
CONSTRAINT FKe3tj0gtbb50ercpeyex8vqvbo FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table merchant_outlet
--

DROP TABLE IF EXISTS merchant_outlet;

CREATE TABLE merchant_outlet (
id varchar(255) NOT NULL,
created_at datetime DEFAULT NULL,
is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
merchant_id varchar(255) DEFAULT NULL,
outlet_id varchar(255) DEFAULT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table merchant_qr
--

DROP TABLE IF EXISTS merchant_qr;

CREATE TABLE merchant_qr (
id varchar(255) NOT NULL,
created_at datetime DEFAULT NULL,
is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
merchant_id varchar(255) DEFAULT NULL,
merchant_user_id varchar(255) DEFAULT NULL,
upi_qr_code varchar(255) DEFAULT NULL,
upi_qr_image longblob DEFAULT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table merchant_settings
--

DROP TABLE IF EXISTS merchant_settings;

CREATE TABLE merchant_settings (
id varchar(255) NOT NULL,
created_at datetime DEFAULT NULL,
is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
param_description varchar(255) DEFAULT NULL,
param_name varchar(255) NOT NULL,
param_value varchar(255) DEFAULT NULL,
merchant_id varchar(255) DEFAULT NULL,
PRIMARY KEY (id),
KEY FKg4pm1v8q4xmmwae03vtkr191p (merchant_id),
CONSTRAINT FKg4pm1v8q4xmmwae03vtkr191p FOREIGN KEY (merchant_id) REFERENCES merchant_details (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table merchant_submerchant
--

DROP TABLE IF EXISTS merchant_submerchant;

CREATE TABLE merchant_submerchant (
id varchar(255) NOT NULL,
created_at datetime DEFAULT NULL,
is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
merchant_id varchar(255) DEFAULT NULL,
submerchant_id varchar(255) DEFAULT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table outlet
--

DROP TABLE IF EXISTS outlet;

CREATE TABLE outlet (
id varchar(255) NOT NULL,
created_at datetime DEFAULT NULL,
is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
contact_number varchar(255) DEFAULT NULL,
description varchar(255) DEFAULT NULL,
extension_number varchar(255) DEFAULT NULL,
licence_number varchar(255) DEFAULT NULL,
resource_type varchar(255) DEFAULT NULL,
title varchar(255) DEFAULT NULL,
address_id varchar(255) DEFAULT NULL,
merchant_id varchar(255) DEFAULT NULL,
PRIMARY KEY (id),
KEY FK5itmo2mky64wosq0ap3440eo3 (address_id),
KEY FK98gthb37tgdoeklus0bna221w (merchant_id),
CONSTRAINT FK5itmo2mky64wosq0ap3440eo3 FOREIGN KEY (address_id) REFERENCES address (id),
CONSTRAINT FK98gthb37tgdoeklus0bna221w FOREIGN KEY (merchant_id) REFERENCES merchant_details (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table outlet_permission
--

DROP TABLE IF EXISTS outlet_permission;

CREATE TABLE outlet_permission (
id varchar(255) NOT NULL,
created_at datetime DEFAULT NULL,
is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
outlet_id varchar(255) DEFAULT NULL,
permission_id varchar(255) DEFAULT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table product_tax
--

DROP TABLE IF EXISTS product_tax;

CREATE TABLE product_tax (
  id varchar(255) NOT NULL,
  created_at datetime DEFAULT NULL,
  is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
cgst double NOT NULL,
sgst double NOT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table product
--

DROP TABLE IF EXISTS product;

CREATE TABLE product (
id varchar(255) NOT NULL,
created_at datetime DEFAULT NULL,
is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
actual_price double DEFAULT NULL,
category_id varchar(255) DEFAULT NULL,
deal_price double DEFAULT NULL,
description varchar(500) DEFAULT NULL,
image varchar(255) DEFAULT NULL,
name varchar(255) DEFAULT NULL,
stock_count int(11) DEFAULT NULL,
status varchar(255) NOT NULL,
subcategory_id varchar(255) DEFAULT NULL,
tax_id varchar(255) DEFAULT NULL,
merchant_id varchar(255) DEFAULT NULL,
product_tax_id varchar(255) DEFAULT NULL,
outlet_id varchar(255) NOT NULL,
PRIMARY KEY (id,outlet_id),
UNIQUE KEY UK_jmivyxk9rmgysrmsqw15lqr5b (name),
KEY FKnxaf2kffhk4f7h59hmc7ok2nn (merchant_id),
KEY FK9mj8gpii9x8ey24xsbi3e24m3 (product_tax_id),
CONSTRAINT FK9mj8gpii9x8ey24xsbi3e24m3 FOREIGN KEY (product_tax_id) REFERENCES product_tax (id),
CONSTRAINT FKnxaf2kffhk4f7h59hmc7ok2nn FOREIGN KEY (merchant_id) REFERENCES merchant_details (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table role_api_mapping
--

DROP TABLE IF EXISTS role_api_mapping;

CREATE TABLE role_api_mapping (
id varchar(255) NOT NULL,
created_at datetime DEFAULT NULL,
is_deleted tinyint(4) DEFAULT NULL,
is_active tinyint(4) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
role_id varchar(255) DEFAULT NULL,
permission_id varchar(255) DEFAULT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table role_api_mapping_outlet
--

DROP TABLE IF EXISTS role_api_mapping_outlet;

CREATE TABLE role_api_mapping_outlet (
id varchar(255) NOT NULL,
created_at datetime DEFAULT NULL,
is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
outlet_id varchar(255) DEFAULT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table user_opt_verification
--

DROP TABLE IF EXISTS user_opt_verification;

CREATE TABLE user_opt_verification (
id varchar(255) NOT NULL,
created_at datetime DEFAULT NULL,
is_deleted bit(1) DEFAULT NULL,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
action_count int(11) DEFAULT NULL,
confirmed_date datetime DEFAULT NULL,
token_expire_at datetime NOT NULL,
extension_number varchar(255) DEFAULT NULL,
token_issued_at datetime NOT NULL,
otp varchar(255) DEFAULT NULL,
phone_number varchar(255) DEFAULT NULL,
token varchar(255) DEFAULT NULL,
type varchar(30) NOT NULL,
user_id varchar(255) DEFAULT NULL,
user_verification_status varchar(30) NOT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



CREATE TABLE user_document (
  id varchar(255) NOT NULL,
  created_at datetime DEFAULT NULL,
is_deleted tinyint(1) DEFAULT 0,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
doc_type varchar(255) DEFAULT NULL,
document_data longtext DEFAULT NULL,
verified_status varchar(15) DEFAULT NULL,
user_id varchar(255) DEFAULT NULL,
id_no varchar(255) DEFAULT NULL,
reason_description varchar(500) DEFAULT NULL,
PRIMARY KEY (id),
UNIQUE KEY user_doc_ukey (id_no),
KEY user_fk_user_doc (user_id),
CONSTRAINT user_fk_user_doc FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE user_doc_error (
  id varchar(255) NOT NULL,
  created_at datetime DEFAULT NULL,
is_deleted tinyint(1) DEFAULT 0,
is_active bit(1) DEFAULT NULL,
updated_at datetime DEFAULT NULL,
error_message varchar(255) DEFAULT NULL,
document_id varchar(255) DEFAULT NULL,
PRIMARY KEY (id),
KEY FK_userDoc_doc_error (document_id),
CONSTRAINT FK_userDoc_doc_error FOREIGN KEY (document_id) REFERENCES user_document (id)
);

CREATE TABLE merchant_db.merchant_report_history
(
    `id`                     varchar(255),
    `report_name`                varchar(255)   DEFAULT NULL,
    `report_type`                 varchar(255)   DEFAULT NULL,
    `report_url`               varchar(555) DEFAULT NULL,
    `user_id`               varchar(255)         DEFAULT NULL,
    `is_active`           bit(1)         DEFAULT b'1',
    `is_deleted`                bit(1)         DEFAULT b'0',
    `created_at`              datetime       DEFAULT NULL,
    `updated_at`              datetime       DEFAULT NULL,
    PRIMARY KEY (`id`)
);