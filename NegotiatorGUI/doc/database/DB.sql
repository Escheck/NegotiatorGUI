-- phpMyAdmin SQL Dump
-- version 2.11.11
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 30, 2012 at 10:28 AM
-- Server version: 5.0.77
-- PHP Version: 5.1.6

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `DistributedGenius`
--

-- --------------------------------------------------------

--
-- Table structure for table `Jobs`
--

CREATE TABLE IF NOT EXISTS `Jobs` (
  `jobID` int(11) NOT NULL auto_increment,
  `sessionname` varchar(20) NOT NULL,
  `tournament` blob NOT NULL,
  PRIMARY KEY  (`jobID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=483 ;

-- --------------------------------------------------------

--
-- Table structure for table `Sessions`
--

CREATE TABLE IF NOT EXISTS `Sessions` (
  `sessionID` int(11) NOT NULL auto_increment,
  `jobID` int(11) NOT NULL,
  `jobLow` smallint(6) NOT NULL,
  `jobHigh` smallint(6) NOT NULL,
  `result` mediumtext,
  `status` tinyint(4) NOT NULL,
  PRIMARY KEY  (`sessionID`),
  KEY `jobID` (`jobID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=61851 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Sessions`
--
ALTER TABLE `Sessions`
  ADD CONSTRAINT `Sessions_ibfk_1` FOREIGN KEY (`jobID`) REFERENCES `Jobs` (`jobID`) ON DELETE CASCADE;
