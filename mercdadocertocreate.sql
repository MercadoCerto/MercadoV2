-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: mercadocerto
-- ------------------------------------------------------
-- Server version	5.5.5-10.5.29-MariaDB

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
-- Table structure for table `pais`
--

DROP TABLE IF EXISTS `pais`;
CREATE TABLE `pais` (
  `id_pais` int(11) NOT NULL AUTO_INCREMENT,
  `nome_pais` varchar(255) NOT NULL,
  PRIMARY KEY (`id_pais`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `pais` WRITE;
INSERT INTO `pais` VALUES (1,'Brasil');
UNLOCK TABLES;

--
-- Table structure for table `cidade`
--

DROP TABLE IF EXISTS `cidade`;
CREATE TABLE `cidade` (
  `id_cidade` int(11) NOT NULL AUTO_INCREMENT,
  `nome_cidade` varchar(255) NOT NULL,
  `id_pais` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_cidade`),
  KEY `id_pais` (`id_pais`),
  CONSTRAINT `cidade_ibfk_1` FOREIGN KEY (`id_pais`) REFERENCES `pais` (`id_pais`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `cidade` WRITE;
INSERT INTO `cidade` VALUES (1,'São Paulo',1);
UNLOCK TABLES;

--
-- Table structure for table `endereco`
--

DROP TABLE IF EXISTS `endereco`;
CREATE TABLE `endereco` (
  `id_endereco` int(11) NOT NULL AUTO_INCREMENT,
  `logradouro` varchar(255) NOT NULL,
  `numero` varchar(10) DEFAULT NULL,
  `bairro` varchar(255) DEFAULT NULL,
  `complemento` varchar(255) DEFAULT NULL,
  `cep` varchar(20) DEFAULT NULL,
  `id_cidade` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_endereco`),
  KEY `id_cidade` (`id_cidade`),
  CONSTRAINT `endereco_ibfk_1` FOREIGN KEY (`id_cidade`) REFERENCES `cidade` (`id_cidade`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `endereco` WRITE;
INSERT INTO `endereco` VALUES (1,'Rua Teste','123','Centro',NULL,'01000-000',1);
UNLOCK TABLES;

--
-- Table structure for table `usuario`
-- CORRIGIDO: adicionadas colunas tipo_conta e cnpj que existem na entidade JPA
--

DROP TABLE IF EXISTS `usuario`;
CREATE TABLE `usuario` (
  `id_usuario` int(11) NOT NULL AUTO_INCREMENT,
  `nome_usuario` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `login` varchar(255) DEFAULT NULL,
  `senha` varchar(255) NOT NULL,
  `tipo_conta` varchar(20) NOT NULL DEFAULT 'USUARIO',
  `cnpj` varchar(255) DEFAULT NULL,
  `cpf` varchar(255) DEFAULT NULL,
  `data_nascimento` date DEFAULT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  `id_endereco` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `login` (`login`),
  UNIQUE KEY `cpf` (`cpf`),
  UNIQUE KEY `cnpj` (`cnpj`),
  KEY `id_endereco` (`id_endereco`),
  CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`id_endereco`) REFERENCES `endereco` (`id_endereco`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `usuario` WRITE;
INSERT INTO `usuario` VALUES
  (1,'testeCadastro','email@email.com','email@email.com','$2a$10$L6bJ2EZzGOCkMZJDhW4NneVj4pj5.n5HksazmY5JkGfU8h3ARk7Ti','USUARIO',NULL,NULL,NULL,NULL,NULL),
  (2,'teste','teste@email.com','teste@email.com','$2a$10$EcbHH20YpmMWl0oN2nOISO/VxsVNT.j7f.HFuUmmqxcKOINvfNjiW','USUARIO',NULL,NULL,NULL,NULL,NULL),
  (3,'David Souza','david.souza1605@gmail.com','david.souza1605@gmail.com','$2a$10$8o9r7DuQAHC/34NGW4ZTuecpbcROyH7dtePyvD/PrpyHK8ZpxAKo2','USUARIO',NULL,NULL,NULL,NULL,NULL),
  (5,'testegravado','emailgravado@email.com','emailgravado@email.com','$2a$10$DpmTvje8lEwXYymdY6XQO.u0fDeAXHKLSaDVk65FXNrOVZOtu2lwK','USUARIO',NULL,NULL,NULL,NULL,NULL);
UNLOCK TABLES;

--
-- Table structure for table `mercado`
-- CORRIGIDO: adicionadas colunas latitude e longitude que existem na entidade JPA
--

DROP TABLE IF EXISTS `mercado`;
CREATE TABLE `mercado` (
  `id_mercado` int(11) NOT NULL AUTO_INCREMENT,
  `nome_mercado` varchar(255) NOT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `id_endereco` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_mercado`),
  KEY `id_endereco` (`id_endereco`),
  CONSTRAINT `mercado_ibfk_1` FOREIGN KEY (`id_endereco`) REFERENCES `endereco` (`id_endereco`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `mercado` WRITE;
UNLOCK TABLES;

--
-- Table structure for table `produto`
--

DROP TABLE IF EXISTS `produto`;
CREATE TABLE `produto` (
  `id_produto` int(11) NOT NULL AUTO_INCREMENT,
  `nome_produto` varchar(100) NOT NULL,
  `marca` varchar(60) NOT NULL,
  `categoria` varchar(60) NOT NULL,
  `codigo_barras` varchar(30) NOT NULL,
  `imagem` varchar(255) DEFAULT NULL,
  `preco` double NOT NULL,
  `validade` varchar(30) NOT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  PRIMARY KEY (`id_produto`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `produto` WRITE;
INSERT INTO `produto` VALUES (1,'testeCadastro','teste cadastro','catse','12346','WhatsApp Image 2025-07-11 at 22.02.38.jpeg',9.9,'2025-11-20',NULL,NULL),(2,'testeCadastro','teste cadastro','catse','123','WhatsApp Image 2025-07-11 at 22.02.38.jpeg',1.5,'2025-11-20',NULL,NULL),(3,'costela','jbs','carnes','123','WhatsApp Image 2025-07-11 at 22.02.37.jpeg',10,'2025-11-15',NULL,NULL),(4,'testeCadastro','teste cadastro','catse','147','WhatsApp Image 2025-07-11 at 22.02.38.jpeg',10,'5999-11-20',NULL,NULL),(5,'testegravado','testegravado','testegravado','789','WhatsApp Image 2025-07-11 at 22.02.37.jpeg',5000,'2025-11-20',-25.53937,-49.27651),(6,'1911','1911','1911','1911','WhatsApp Image 2025-09-08 at 19.34.51.jpeg',10,'2025-11-19',-25.4295,-49.2712);
UNLOCK TABLES;

--
-- Table structure for table `preco`
--

DROP TABLE IF EXISTS `preco`;
CREATE TABLE `preco` (
  `id_preco` int(11) NOT NULL AUTO_INCREMENT,
  `id_produto` int(11) NOT NULL,
  `id_mercado` int(11) NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `data_hora` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id_preco`),
  KEY `id_produto` (`id_produto`),
  KEY `id_mercado` (`id_mercado`),
  CONSTRAINT `preco_ibfk_1` FOREIGN KEY (`id_produto`) REFERENCES `produto` (`id_produto`),
  CONSTRAINT `preco_ibfk_2` FOREIGN KEY (`id_mercado`) REFERENCES `mercado` (`id_mercado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `preco` WRITE;
UNLOCK TABLES;

--
-- Table structure for table `avaliacao`
-- CORRIGIDO: id_usuario agora permite NULL para avaliações anônimas
--

DROP TABLE IF EXISTS `avaliacao`;
CREATE TABLE `avaliacao` (
  `id_avaliacao` int(11) NOT NULL AUTO_INCREMENT,
  `id_mercado` int(11) NOT NULL,
  `id_usuario` int(11) DEFAULT NULL,
  `nota` int(11) DEFAULT NULL CHECK (`nota` between 1 and 5),
  `comentario` text DEFAULT NULL,
  `data_avaliacao` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id_avaliacao`),
  KEY `id_mercado` (`id_mercado`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `avaliacao_ibfk_1` FOREIGN KEY (`id_mercado`) REFERENCES `mercado` (`id_mercado`),
  CONSTRAINT `avaliacao_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `avaliacao` WRITE;
UNLOCK TABLES;

--
-- Table structure for table `lista_compras`
--

DROP TABLE IF EXISTS `lista_compras`;
CREATE TABLE `lista_compras` (
  `id_lista` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) NOT NULL,
  `nome_lista` varchar(100) NOT NULL,
  `data_criacao` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id_lista`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `lista_compras_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `lista_compras` WRITE;
UNLOCK TABLES;

--
-- Table structure for table `item_lista`
--

DROP TABLE IF EXISTS `item_lista`;
CREATE TABLE `item_lista` (
  `id_item` int(11) NOT NULL AUTO_INCREMENT,
  `id_lista` int(11) NOT NULL,
  `id_produto` int(11) NOT NULL,
  `quantidade` int(11) DEFAULT 1,
  PRIMARY KEY (`id_item`),
  KEY `id_lista` (`id_lista`),
  KEY `id_produto` (`id_produto`),
  CONSTRAINT `item_lista_ibfk_1` FOREIGN KEY (`id_lista`) REFERENCES `lista_compras` (`id_lista`),
  CONSTRAINT `item_lista_ibfk_2` FOREIGN KEY (`id_produto`) REFERENCES `produto` (`id_produto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `item_lista` WRITE;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed
