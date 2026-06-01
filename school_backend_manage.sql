-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: school_backend_manage
-- ------------------------------------------------------
-- Server version	8.0.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `school_backend_manage`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `school_backend_manage` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `school_backend_manage`;

--
-- Table structure for table `announcement`
--

DROP TABLE IF EXISTS `announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcement` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(100) NOT NULL COMMENT '公告标题',
  `content` text NOT NULL COMMENT '公告内容',
  `publisher` varchar(50) NOT NULL COMMENT '发布单位/人',
  `publish_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除（0-正常，1-已删除）',
  `announcement_type_id` int NOT NULL COMMENT '公告类型id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `announcement_type`
--

DROP TABLE IF EXISTS `announcement_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcement_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `type_name` varchar(50) NOT NULL COMMENT '类型名称',
  `priority` int DEFAULT '0' COMMENT '优先级',
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `build`
--

DROP TABLE IF EXISTS `build`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `build` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `building_number` varchar(10) NOT NULL COMMENT '楼号',
  `assigned_gender` enum('男','女') NOT NULL COMMENT '该楼对应的性别：男或女',
  `description` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` enum('正常','异常') DEFAULT '正常',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='宿舍楼表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `complaint_suggestion`
--

DROP TABLE IF EXISTS `complaint_suggestion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `complaint_suggestion` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '投诉建议ID',
  `user_no` varchar(20) NOT NULL COMMENT '提交人学号',
  `content` text NOT NULL COMMENT '投诉/建议内容',
  `status` enum('未处理','已处理') DEFAULT '未处理' COMMENT '处理状态',
  `reply` text COMMENT '处理回复',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除（0-正常，1-删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='投诉与建议表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dorm_change_application`
--

DROP TABLE IF EXISTS `dorm_change_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dorm_change_application` (
  `application_id` int NOT NULL AUTO_INCREMENT,
  `student_no` varchar(20) NOT NULL COMMENT '学号',
  `current_dormitory_id` int NOT NULL COMMENT '当前宿舍id',
  `target_dormitory_id` int NOT NULL COMMENT '分配宿舍id',
  `reason` text COMMENT '调换原因',
  `application_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `status` enum('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING，待审批；APPROVED，已同意；REJECTED，已拒绝',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approver_no` varchar(20) DEFAULT NULL COMMENT '审批人工号',
  `comment` text COMMENT '意见',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0=正常，1=删除）',
  PRIMARY KEY (`application_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='调换宿舍申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dormitory`
--

DROP TABLE IF EXISTS `dormitory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dormitory` (
  `dormitory_id` int NOT NULL AUTO_INCREMENT,
  `dormitory_no` varchar(20) NOT NULL COMMENT '宿舍号',
  `building_id` tinyint NOT NULL COMMENT '宿舍楼',
  `capacity` int NOT NULL COMMENT '容量',
  `available_beds` int NOT NULL COMMENT '剩余床位',
  `status` enum('正常','异常') DEFAULT '正常' COMMENT '状态',
  `is_delete` tinyint DEFAULT '0' COMMENT '0 正常 1 删除',
  PRIMARY KEY (`dormitory_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='宿舍表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repair_application`
--

DROP TABLE IF EXISTS `repair_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repair_application` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '报修单ID',
  `user_no` varchar(20) NOT NULL COMMENT '申请人学号',
  `dormitory_id` int NOT NULL COMMENT '宿舍ID',
  `content` text NOT NULL COMMENT '报修内容',
  `image_url` varchar(255) DEFAULT NULL COMMENT '报修图片路径',
  `status` enum('待处理','处理中','已完成') DEFAULT '待处理' COMMENT '工单状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除（0-正常 1-已删除）',
  `repair_type` varchar(50) DEFAULT '' COMMENT 'AI识别的报修类型',
  `urgency_level` enum('高','中','低') DEFAULT '中' COMMENT '紧急程度',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='维修申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `student_dormitory_info`
--

DROP TABLE IF EXISTS `student_dormitory_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_dormitory_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_no` varchar(20) NOT NULL,
  `building_id` tinyint DEFAULT NULL COMMENT '住宿楼编号id',
  `dormitory_number` varchar(10) DEFAULT NULL COMMENT '宿舍号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_info_completed` tinyint DEFAULT '0' COMMENT '是否完成首次信息填写(0:未完成,1:已完成)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生住宿表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_no` varchar(20) NOT NULL COMMENT '学号/工号',
  `password` varchar(100) NOT NULL COMMENT '加密密码',
  `username` varchar(50) DEFAULT NULL COMMENT '姓名',
  `role` tinyint NOT NULL DEFAULT '1' COMMENT '角色: 1-学生, 4-系统管理员',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1-正常, 0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gender` enum('男','女') DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_no` (`user_no`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'school_backend_manage'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-01 14:49:31
