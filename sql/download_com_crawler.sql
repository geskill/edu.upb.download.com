-- phpMyAdmin SQL Dump
-- version 4.5.3.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Erstellungszeit: 06. Jan 2016 um 13:25
-- Server-Version: 5.6.24
-- PHP-Version: 5.6.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `download_com_crawler`
--
CREATE DATABASE IF NOT EXISTS `download_com_crawler` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `download_com_crawler`;

DELIMITER $$
--
-- Funktionen
--
DROP FUNCTION IF EXISTS `GET_OID`$$
CREATE DEFINER=`root`@`localhost` FUNCTION `GET_OID` (`APID` INT) RETURNS VARCHAR(100) CHARSET utf8mb4 BEGIN
  DECLARE OID_FOUND INT;

  SELECT `oid` INTO OID_FOUND FROM `download_com_products` WHERE `pid` = APID;

  RETURN CONCAT('3000-', OID_FOUND, '_4-', APID);
END$$

DROP FUNCTION IF EXISTS `GET_URL`$$
CREATE DEFINER=`root`@`localhost` FUNCTION `GET_URL` (`APID` INT) RETURNS VARCHAR(500) CHARSET utf8mb4 BEGIN
  DECLARE OID varchar(100);

  SELECT GET_OID(APID) INTO OID;

  RETURN CONCAT('http://download.cnet.com/id/', OID, '.html');
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `download_com_products`
--

DROP TABLE IF EXISTS `download_com_products`;
CREATE TABLE `download_com_products` (
  `id` int(11) NOT NULL COMMENT 'internal usage',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'internal usage',
  `modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'internal usage',
  `pid` int(9) NOT NULL COMMENT 'product id',
  `oid` int(9) NOT NULL COMMENT 'ontology id',
  `editors_review_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `editors_review_date` date DEFAULT NULL,
  `editors_review_description` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `editors_review_text` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `editors_review_rating` double NOT NULL,
  `users_review_rating` double NOT NULL,
  `users_review_rating_count` int(6) NOT NULL,
  `publisher_description` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `publisher_description_alterations` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `publisher_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `publisher_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `platform` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `subcategory` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `latest_id_v` int(9) NOT NULL COMMENT 'latest version of the product'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `download_com_product_user_reviews`
--

DROP TABLE IF EXISTS `download_com_product_user_reviews`;
CREATE TABLE `download_com_product_user_reviews` (
  `id` int(11) NOT NULL COMMENT 'internal usage',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'internal usage',
  `modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'internal usage',
  `mid` int(9) NOT NULL COMMENT 'message id',
  `id_p` int(9) NOT NULL COMMENT 'related product id',
  `id_v` int(9) NOT NULL COMMENT 'related version id of product',
  `rating` double NOT NULL,
  `title` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `author` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date` date NOT NULL,
  `pros` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `cons` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `summary` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `thumbs_up` int(6) NOT NULL,
  `thumbs_down` int(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `download_com_product_versions`
--

DROP TABLE IF EXISTS `download_com_product_versions`;
CREATE TABLE `download_com_product_versions` (
  `id` int(11) NOT NULL COMMENT 'internal usage',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'internal usage',
  `modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'internal usage',
  `id_p` int(9) NOT NULL COMMENT 'related product id',
  `vid` int(9) NOT NULL COMMENT 'version id of the product',
  `version_name` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version_alterations` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `version_publish_date` date NOT NULL,
  `version_added_date` date NOT NULL,
  `version_identifier` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `operating_systems` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `additional_requirements` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `download_size` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `download_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `download_link` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `downloads_total` int(10) NOT NULL,
  `downloads_last_week` int(10) NOT NULL,
  `license_model` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `license_limitations` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `license_cost` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `download_com_products`
--
ALTER TABLE `download_com_products`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `pid` (`pid`);

--
-- Indizes für die Tabelle `download_com_product_user_reviews`
--
ALTER TABLE `download_com_product_user_reviews`
  ADD PRIMARY KEY (`id`),
  ADD KEY `mid` (`mid`);

--
-- Indizes für die Tabelle `download_com_product_versions`
--
ALTER TABLE `download_com_product_versions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `vid` (`vid`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `download_com_products`
--
ALTER TABLE `download_com_products`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'internal usage';
--
-- AUTO_INCREMENT für Tabelle `download_com_product_user_reviews`
--
ALTER TABLE `download_com_product_user_reviews`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'internal usage';
--
-- AUTO_INCREMENT für Tabelle `download_com_product_versions`
--
ALTER TABLE `download_com_product_versions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'internal usage';
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
