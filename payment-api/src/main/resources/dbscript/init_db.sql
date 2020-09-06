CREATE TABLE IF NOT EXISTS `t_payment_account` (
  `id`                BIGINT(20)                NOT NULL AUTO_INCREMENT,
  `accountNo`         VARCHAR(255)
                      COLLATE utf8_bin                   DEFAULT NULL,
  `name`              VARCHAR(255)
                      COLLATE utf8_bin                   DEFAULT NULL,
  `type`              INT(10)                            DEFAULT NULL,
  `useType`           INT(4)                             DEFAULT '1',
  `gatewayURL`        VARCHAR(255)
                      COLLATE utf8_bin                   DEFAULT NULL,
  `configData`        LONGTEXT COLLATE utf8_bin NOT NULL,
  `merchantNo`        VARCHAR(255)
                      COLLATE utf8_bin                   DEFAULT NULL,
  `merchantName`      VARCHAR(255)
                      COLLATE utf8_bin                   DEFAULT NULL,
  `processBean`       VARCHAR(255)
                      COLLATE utf8_bin                   DEFAULT NULL,
  `status`            INT(4)                             DEFAULT '1',
  `created_by`        VARCHAR(255)
                      COLLATE utf8_bin                   DEFAULT NULL,
  `creation_time`     DATETIME(3)                        DEFAULT NULL,
  `modification_time` DATETIME(3)                        DEFAULT NULL,
  `modified_by`       VARCHAR(255)
                      COLLATE utf8_bin                   DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `accountNo` (`accountNo`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

DELETE FROM `t_payment_account`;
INSERT INTO `t_payment_account` (`id`, `accountNo`, `name`, `type`, `useType`, `gatewayURL`, `configData`, `merchantNo`, `merchantName`, `processBean`, `status`, `created_by`, `creation_time`, `modification_time`, `modified_by`)
VALUES
  (1, 'WX01', 'eBaoCloud-Weixin', 1, 1, 'https://api.weixin.qq.com/cgi-bin/token',
      '{"appID":"wx9ab9b4105f111a85","appSecret":"311b6a14529fe083fdc60bd3ddebb479","mchID":"1289029101","securityKey":"Ebaotech123Ebaotech123Ebaotech12"}',
      '01', 'eBaoCloud', 'WeixinJSApiPaymentService', 1, '_UNKNOWN', '2016-04-15 13:32:24.661',
   '2016-04-15 13:32:24.661', '_UNKNOWN'),
  (2, 'ALIPAY01', 'ebaocloud-Alipay01', 3, 1, 'https://mapi.alipay.com/gateway.do',
      '{ "partner": "2088121231409386", "sellerMail": "fi@anyi-tech.com", "securityKey": "t5j69s9sg36o3txnjzs7l3gv0pwga4lx" }',
      '02', 'anyi', 'aliGateWayPaymentService', 1, NULL, NULL, NULL, NULL);
INSERT INTO `t_payment_account` (`id`, `accountNo`, `name`, `type`, `useType`, `gatewayURL`, `configData`, `merchantNo`, `merchantName`, `processBean`, `status`, `created_by`, `creation_time`, `modification_time`, `modified_by`) VALUES (3, '99BILL01', '99BILL01', 5, 1, 'https://sandbox.99bill.com/gateway/recvMerchantInfoAction.htm', '{\r\n    "merchantAcctId": "1001213884201",\r\n    "publicKeyPath": "/bill99/bill99.cert.rsa.20340630.cer",\r\n    "privateKeyPath": "/bill99/tester-rsa.pfx",\r\n    "privateKeyPassword": "123456"\r\n}', '03', 'test', 'bill99PaymentService', 1, NULL, NULL, NULL, NULL );


CREATE TABLE IF NOT EXISTS `t_payment_ext_conf` (
  `id`                         INT(11)      NOT NULL AUTO_INCREMENT,
  `accountType`                INT(3)       NOT NULL,
  `bizType`                    VARCHAR(255) NOT NULL,
  `prepareBillingDataBean`     VARCHAR(255) NOT NULL,
  `processPaymentCallbackBean` VARCHAR(255) NOT NULL,
  `created_by`                 VARCHAR(255)          DEFAULT NULL,
  `creation_time`              DATETIME              DEFAULT NULL,
  `modified_by`                VARCHAR(255)          DEFAULT NULL,
  `modification_time`          DATETIME              DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

DELETE FROM `t_payment_ext_conf`;
INSERT INTO `t_payment_ext_conf` (`id`, `accountType`, `bizType`, `prepareBillingDataBean`, `processPaymentCallbackBean`, `created_by`, `creation_time`, `modified_by`, `modification_time`)
VALUES
  (1, 1, 'NB', 'mockPaymentIntegtationService', 'mockCallbackService', NULL, NULL, NULL, NULL),
  (2, 3, 'NB', 'mockPaymentIntegtationService', 'mockCallbackService', NULL, NULL, NULL, NULL);

CREATE TABLE IF NOT EXISTS `t_payment_transaction` (
  `id`              BIGINT(20)       NOT NULL AUTO_INCREMENT,
  `accountNo`       VARCHAR(255)
                    COLLATE utf8_bin NOT NULL,
  `orderNo`         VARCHAR(255)
                    COLLATE utf8_bin NOT NULL,
  `subject`         VARCHAR(255)
                    COLLATE utf8_bin          DEFAULT NULL,
  `amount`          DECIMAL(19, 2)   NOT NULL,
  `prePayId`        VARCHAR(255)
                    COLLATE utf8_bin          DEFAULT NULL,
  `payId`           VARCHAR(255)
                    COLLATE utf8_bin          DEFAULT NULL,
  `callBackAgent`   VARCHAR(255)
                    COLLATE utf8_bin          DEFAULT NULL,
  `status`          INT(4)                    DEFAULT '0',
  `messageLog`      LONGTEXT COLLATE utf8_bin,
  `creation_time`   DATETIME(3)               DEFAULT NULL,
  `completion_time` DATETIME(3)               DEFAULT NULL,
  `currency`        VARCHAR(3)
                    COLLATE utf8_bin          DEFAULT 'CNY',
  `notificationMsg` VARCHAR(512)
                    COLLATE utf8_bin          DEFAULT NULL,
  `bizNo`           VARCHAR(255)
                    COLLATE utf8_bin          DEFAULT NULL,
  `returnURL`       VARCHAR(512)
                    COLLATE utf8_bin          DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `orderNo` (`orderNo`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

