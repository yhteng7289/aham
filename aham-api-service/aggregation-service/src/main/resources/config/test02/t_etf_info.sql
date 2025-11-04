/*
 Navicat Premium Data Transfer

 Source Server         : 璇玑开发
 Source Server Type    : MySQL
 Source Server Version : 50624
 Source Host           : test.3386.qa.mysql.local
 Source Database       : aham

 Target Server Type    : MySQL
 Target Server Version : 50624
 File Encoding         : utf-8

 Date: 01/11/2019 14:55:13 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `t_etf_info`
-- ----------------------------
-- DROP TABLE IF EXISTS `t_etf_info`;
-- CREATE TABLE `t_etf_info` (
--   `id` int(11) NOT NULL AUTO_INCREMENT,
--   `etf_code` varchar(20) NOT NULL,
--   `uic` int(11) NOT NULL,
--   `exchange_code` varchar(10) NOT NULL,
--   `enable` tinyint(4) NOT NULL DEFAULT '1',
--   PRIMARY KEY (`id`),
--   UNIQUE KEY `uniq_etf` (`etf_code`) USING BTREE
-- ) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Records of `t_etf_info`
-- ----------------------------
BEGIN;
INSERT INTO `t_etf_info` VALUES ('1', 'VT', '36137', 'NYSE_ARCA', '1'), ('2', 'EEM', '31871', 'NYSE_ARCA', '1'), ('3', 'BNDX', '504419', 'NASDAQ', '1'), ('4', 'SHV', '7522055', 'NASDAQ', '1'), ('5', 'EMB', '7521992', 'NASDAQ', '1'), ('6', 'VWOB', '501225', 'NASDAQ', '1'), ('7', 'BWX', '35423', 'NYSE_ARCA', '1'), ('8', 'HYG', '35368', 'NYSE_ARCA', '1'), ('9', 'JNK', '35412', 'NYSE_ARCA', '1'), ('10', 'MUB', '47361', 'NYSE_ARCA', '1'), ('11', 'LQD', '31923', 'NYSE_ARCA', '1'), ('12', 'VCIT', '164100', 'NASDAQ', '1'), ('13', 'FLOT', '7521993', 'BATS_BZX', '1'), ('14', 'IEF', '7522010', 'NASDAQ', '1'), ('15', 'UUP', '35955', 'NYSE_ARCA', '1'), ('16', 'PDBC', '10188761', 'NASDAQ', '1'), ('17', 'GLD', '32664', 'NYSE_ARCA', '1'), ('18', 'VNQ', '34910', 'NYSE_ARCA', '1'), ('19', 'VEA', '47886', 'NYSE_ARCA', '1'), ('20', 'VPL', '34911', 'NYSE_ARCA', '1'), ('21', 'EWA', '31917', 'NYSE_ARCA', '1'), ('22', 'SPY', '36590', 'NYSE_ARCA', '1'), ('23', 'VOO', '51625', 'NYSE_ARCA', '1'), ('24', 'VTI', '34912', 'NYSE_ARCA', '1'), ('25', 'VGK', '34909', 'NYSE_ARCA', '1'), ('26', 'EWJ', '31880', 'NYSE_ARCA', '1'), ('27', 'QQQ', '4328771', 'NASDAQ', '1'), ('28', 'EWS', '31886', 'NYSE_ARCA', '1'), ('29', 'EWZ', '31890', 'NYSE_ARCA', '1'), ('30', 'ASHR', '507028', 'NYSE_ARCA', '1'), ('31', 'VWO', '34914', 'NYSE_ARCA', '1'), ('32', 'ILF', '31969', 'NYSE_ARCA', '1'), ('33', 'RSX', '31994', 'NYSE_ARCA', '1'), ('34', 'AAXJ', '38560', 'NASDAQ', '1');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
