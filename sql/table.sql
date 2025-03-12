# 生成高德表
DROP TABLE IF EXISTS `gaode`;
CREATE TABLE `gaode` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `citycode` varchar(255) DEFAULT NULL,
  `adcode` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `pname` varchar(255) DEFAULT NULL,
  `cityname` varchar(255) DEFAULT NULL,
  `adname` varchar(255)DEFAULT NULL,
)