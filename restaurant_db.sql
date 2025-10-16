-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: restaurant_db
-- ------------------------------------------------------
-- Server version	8.0.43

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
-- Table structure for table `menu_items`
--

DROP TABLE IF EXISTS `menu_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `available` tinyint(1) DEFAULT '1',
  `image_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu_items`
--

LOCK TABLES `menu_items` WRITE;
/*!40000 ALTER TABLE `menu_items` DISABLE KEYS */;
INSERT INTO `menu_items` VALUES (1,'Veg Fried Rice','Main',120.00,1,'images/fried.jpeg'),(2,'Chilli Paneer','Starter',150.00,1,'images/paneer.jpeg'),(3,'Spring Rolls','Starter',90.00,1,'images/springroll.jpg'),(4,'manchow soup','Soup',80.00,1,'images/manchow soup.jpeg'),(5,'kung pao chicken','Main',220.00,1,'images/kung pao chicken.jpg'),(6,'Paneer Manchurian','Starter',230.00,1,'images/Paneer Manchurian.jpeg'),(7,'Gobi Manchurian','Starter',197.00,1,'images/Gobi Manchurian.jpg'),(8,'Hakka Noodlles','Main',125.00,1,'images/Hakka Noodles.jpg'),(9,'Schezwan Fried Rice','Main',165.00,1,'C:\\Users\\dsyaz\\Desktop\\images\\schezwan.jpg'),(10,'Chilli Chicken','Main',400.00,1,'images/Chilli Chicken.jpeg'),(11,'Honey Chilli Chicken','Starter',220.00,1,'images/Honey-chilli-chicken-recipe.jpg'),(12,'Veg Hot and Sour Soup','Soup',85.00,1,'images/chinese-hot-and-sour-soup-sq.jpg'),(13,'Sweet Corn Soup','Soup',65.00,1,'images/sweet corn.jpg'),(14,'Red Rice','Main',70.00,1,'images/red-rice-7.jpg'),(15,'White Rice','Main',50.00,1,'images/white rice.jpg'),(16,'Gulab Jamun','desserts',150.00,1,'images/Gulab Jamun.jpg'),(17,'Tiramisu','desserts',250.00,1,'images/Tiramisu.jpg'),(18,'Fried Ice cream','desserts',200.00,1,'images/Fried Ice cream.jpg'),(19,'Chicken lollipop','Starter',250.00,1,'images/Chicken lollipop.jpg'),(20,'Chicken Tikka','Appetizers',300.00,1,'images/Chicken tikka.jpg'),(21,'Fish Fingers','Appetizers',300.00,1,'images/Fish Fingers.jpg'),(22,'Prawn Tempura','Appetizers',370.00,1,'images/Prawn.jpg'),(23,'Lentil Soup (Dal Shorba)','Soup',180.00,1,'images/lentil soup.jpg'),(24,'Crab Meat Soup','Soup',200.00,1,'images/homemade-maryland-crab-soup.jpg'),(25,'Veg kofta curry','Main',380.00,1,'images/Veg Kofta.jpeg'),(26,'Tandoori roti','Main',120.00,1,'images/Tandoori roti.jpg'),(27,'Chocolate Brownie with Ice Cream','desserts',260.00,1,'images/Chocolate Brownie with Ice Cream.jpg'),(28,'key lime pie','desserts',220.00,1,'images/key lime pie.jpg'),(29,'French Entremet','desserts',200.00,1,'images/french entremet.jpg'),(30,'Stuffed Mushrooms','Appetizers',258.00,1,'images/Stuffed Mushroom.jpg'),(31,'Corn Cheese Balls','Appetizers',180.00,1,'images/Corn Cheese Balls.jpg'),(32,'Spaghetti bolognese','Main',190.00,1,'images/eggplant-bolognese.jpg'),(33,'Chicken biryani','Main',150.00,1,'images/Chicken biryani.jpg'),(34,'Sambar Rice','Main',90.00,1,'images/sambar.jpeg'),(35,'Dahi Ke Kebab','Starter',120.00,1,'images/Dahi Ke Kebab.jpg'),(36,'Smoked Salmon','Starter',190.00,1,'images/Smoked Salmon.jpg'),(37,'Greek Bruschetta','Starter',200.00,1,'images/Greek Bruschetta.jpg'),(38,'Birria Tacos','Starter',250.00,1,'images/Birria Tacos.jpg'),(39,'Stuffed Bell Peppers','Starter',120.00,1,'images/Stuffed Bell Peppers.jpg'),(41,'Chicken Manchow Soup','Soup',130.00,1,'images/Chicken Manchow Soup.jpg'),(42,'japanese clear soup','Soup',85.00,1,'images/Japanese clear soup.jpeg');
/*!40000 ALTER TABLE `menu_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `menu_item_id` int DEFAULT NULL,
  `menu_name` varchar(100) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `price` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (2,4,1,'Veg Fried Rice',1,120),(3,4,2,'Chilli Paneer',1,150),(4,5,1,'Veg Fried Rice',1,120),(5,5,2,'Chilli Paneer',1,150),(6,6,4,'Kung Pao Chicken',2,220),(7,6,5,'Manchow Soup',1,80),(8,6,3,'Spring Rolls',1,90),(13,8,6,'Paneer Manchurian',1,240),(14,9,18,'Fried Ice Cream',1,200),(15,10,6,'Paneer Manchurian',2,240),(17,12,8,'Hakka Noodlles',1,125),(18,13,23,'Lentil Soup (Dal Shorba)',1,180),(19,14,30,'Stuffed Mushrooms',1,258),(20,15,24,'Crab Meat Soup',1,200),(21,16,15,'White Rice',1,50),(22,16,24,'Crab Meat Soup',1,200),(23,17,5,'kung pao chicken',1,220),(24,18,5,'kung pao chicken',1,220),(25,19,5,'kung pao chicken',1,220),(26,20,10,'Chilli Chicken',1,400),(27,20,19,'Chicken lollipop',1,250),(28,20,23,'Lentil Soup (Dal Shorba)',1,180),(29,20,29,'French Entremet',1,200),(30,21,19,'Chicken lollipop',1,250),(31,22,26,'Tandoori roti',1,120),(32,23,5,'kung pao chicken',1,220),(35,25,1,'Veg Fried Rice',1,120),(36,26,8,'Hakka Noodlles',1,125),(38,28,8,'Hakka Noodlles',1,125),(39,29,10,'Chilli Chicken',1,400),(40,30,8,'Hakka Noodlles',1,125),(41,31,17,'Tiramisu',1,250),(42,32,25,'Veg kofta curry',1,380),(43,33,8,'Hakka Noodlles',1,125),(44,34,14,'Red Rice',1,70),(45,35,10,'Chilli Chicken',1,400),(46,36,8,'Hakka Noodlles',1,125),(47,37,9,'Schezwan Fried Rice',1,165),(48,38,1,'Veg Fried Rice',1,120),(49,39,10,'Chilli Chicken',1,400),(50,40,9,'Schezwan Fried Rice',1,165),(51,40,19,'Chicken lollipop',1,250),(52,40,23,'Lentil Soup (Dal Shorba)',1,180),(53,40,28,'key lime pie',1,220),(54,40,21,'Fish Fingers',1,300),(55,41,8,'Hakka Noodlles',1,125),(56,42,10,'Chilli Chicken',1,400),(57,43,10,'Chilli Chicken',1,400),(58,44,33,'Chicken biryani',1,150),(59,44,36,'Smoked Salmon',1,190),(60,44,24,'Crab Meat Soup',1,200),(61,44,29,'French Entremet',1,200),(62,44,21,'Fish Fingers',1,300);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `table_number` varchar(20) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `total` decimal(10,2) DEFAULT '0.00',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'10','Placed',290.00,'2025-10-14 05:21:18'),(2,'1','Placed',80.00,'2025-10-14 05:21:51'),(4,'6','Paid',270.00,'2025-10-14 05:46:49'),(5,'1','Paid',270.00,'2025-10-14 05:54:43'),(6,'2','Paid',610.00,'2025-10-14 06:00:36'),(8,'7','Paid',240.00,'2025-10-14 10:22:31'),(9,'5','Paid',200.00,'2025-10-14 10:24:43'),(10,'1','Paid',480.00,'2025-10-14 10:28:46'),(12,'5','Paid',125.00,'2025-10-14 13:12:12'),(13,'2','Paid',180.00,'2025-10-14 14:07:29'),(14,'2','Paid',258.00,'2025-10-14 14:10:30'),(15,'7','Paid',200.00,'2025-10-14 14:12:57'),(16,'5','Paid',250.00,'2025-10-14 14:14:46'),(17,'1','Paid',220.00,'2025-10-14 14:17:23'),(18,'2','Paid',220.00,'2025-10-14 14:23:25'),(19,'2','Paid',220.00,'2025-10-14 14:28:30'),(20,'6','Paid',1030.00,'2025-10-14 14:30:47'),(21,'4','Paid',250.00,'2025-10-14 14:32:57'),(22,'8','Paid',120.00,'2025-10-14 14:35:59'),(23,'2','Paid',220.00,'2025-10-14 14:40:42'),(25,'5','Paid',120.00,'2025-10-14 14:46:19'),(26,'2','Paid',125.00,'2025-10-14 14:51:58'),(28,'3','Paid',125.00,'2025-10-14 14:57:59'),(29,'4','Paid',400.00,'2025-10-14 14:59:53'),(30,'5','Paid',125.00,'2025-10-14 15:01:40'),(31,'1','Paid',250.00,'2025-10-14 15:03:12'),(32,'9','Paid',380.00,'2025-10-14 15:04:51'),(33,'9','Paid',125.00,'2025-10-14 15:06:31'),(34,'2','Paid',70.00,'2025-10-14 15:06:45'),(35,'4','Paid',400.00,'2025-10-14 15:28:27'),(36,'7','Paid',125.00,'2025-10-14 15:30:53'),(37,'1','Paid',165.00,'2025-10-14 15:37:46'),(38,'1','Paid',120.00,'2025-10-14 15:42:35'),(39,'1','Paid',400.00,'2025-10-14 15:58:56'),(40,'2','Paid',1115.00,'2025-10-14 16:06:22'),(41,'1','Paid',125.00,'2025-10-14 16:59:08'),(42,'2','Paid',400.00,'2025-10-15 03:09:54'),(43,'2','Paid',400.00,'2025-10-15 05:10:08'),(44,'5','Paid',1040.00,'2025-10-15 05:12:47');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-15 11:40:14
