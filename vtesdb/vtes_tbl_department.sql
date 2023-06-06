-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: vtes
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tbl_department`
--

DROP TABLE IF EXISTS `tbl_department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_department` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `DEPARTMENT_NAME` varchar(100)
   NOT NULL,
  `CREATE_DT` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_DT` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `DELETE_FLAG` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_department`
--

LOCK TABLES `tbl_department` WRITE;
/*!40000 ALTER TABLE `tbl_department` DISABLE KEYS */;
INSERT INTO `tbl_department` VALUES (1,'取締役会','2023-05-22 17:11:56','2023-05-31 03:50:16',0),(2,'営業第一部','2023-05-22 17:11:56','2023-05-31 03:50:16',0),(3,'営業第二部','2023-05-22 17:11:56','2023-05-31 03:50:16',0),(4,'営業第三部','2023-05-22 17:11:56','2023-05-31 03:50:16',0),(5,'バックオフィス部','2023-05-22 17:11:56','2023-05-31 03:50:16',0),(6,'開発部','2023-05-22 17:11:56','2023-05-31 03:50:16',0),(7,'第一開発部','2023-05-22 17:11:56','2023-05-31 03:50:16',0),(8,'第二開発部','2023-05-22 17:11:56','2023-05-31 03:50:16',0),(9,'第三開発部','2023-05-22 17:11:56','2023-05-31 03:50:16',0),(10,'第四開発部','2023-05-22 17:11:56','2023-05-31 03:50:16',0),(11,'第五開発部','2023-05-22 17:11:56','2023-05-31 03:50:16',0),(12,'第六開発部','2023-05-22 17:11:56','2023-05-31 03:50:16',0),(12,'名古屋営業所','2023-05-22 17:11:56','2023-05-31 03:50:16',0);

/*!40000 ALTER TABLE `tbl_department` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-31 16:16:11
