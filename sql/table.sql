# 生成高德表
DROP TABLE IF EXISTS `gaode`;
CREATE TABLE `gaode` (
  `id` int(11) NOT NULL AUTO_INCREMENT comment '主键',
  `name` varchar(255) DEFAULT NULL comment '岗位名称',
  `modify_time` DATETIME DEFAULT NULL comment '更新时间',
  `work_locations` varchar(255) DEFAULT NULL comment '工作地点',
  `requirement` text DEFAULT NULL comment '岗位要求',
  `description` text DEFAULT NULL comment '岗位描述',
  `category_type` varchar(32) DEFAULT NULL comment '岗位类型 实习生，日常实习，应届校招'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;