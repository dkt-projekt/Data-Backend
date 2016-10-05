CREATE TABLE IF NOT EXISTS `Collections` (
  `collectionId` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `collectionName` varchar(50) NOT NULL,
  `description` varchar(250) NOT NULL,
  `private` int(11) NOT NULL DEFAULT '0',
  `analysis` varchar(250) NOT NULL,
  `users` varchar(250) NOT NULL,
  `timelining` mediumtext NOT NULL,
  `geolocalization` mediumtext NOT NULL,
  `entitylinking` mediumtext NOT NULL,
  `clustering` mediumtext NOT NULL,
  `documents` mediumtext NOT NULL,
  PRIMARY KEY (`collectionId`),
  UNIQUE KEY `collectionName` (`collectionName`)
)  ;

CREATE TABLE IF NOT EXISTS `Models` (
  `modelId` int(11) NOT NULL AUTO_INCREMENT,
  `modelName` varchar(50) NOT NULL,
  `modelType` varchar(20) NOT NULL,
  `url` varchar(100) NOT NULL,
  `analysis` varchar(50) DEFAULT NULL,
  `models` varchar(100) DEFAULT NULL,
  `language` varchar(10) DEFAULT NULL,
  `informat` varchar(15) NOT NULL,
  `outformat` varchar(15) NOT NULL,
  `mode` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`modelId`),
  UNIQUE KEY `modelName` (`modelName`)
) ;

INSERT INTO `Models` (`modelId`, `modelName`, `modelType`, `url`, `analysis`, `models`, `language`, `informat`, `outformat`, `mode`) VALUES
(1, 'ner_PER_ORG_LOC_en_spot', 'ner', '/e-nlp/namedEntityRecognition', 'ner', 'ner-wikinerEn_PER;ner-wikinerEn_ORG;ner-wikinerEn_LOC', 'en', 'turtle', 'turtle', 'spot'),
(2, 'ner_PER_ORG_LOC_en_link', 'ner', '/e-nlp/namedEntityRecognition', 'ner', 'ner-wikinerEn_PER;ner-wikinerEn_ORG;ner-wikinerEn_LOC', 'en', 'turtle', 'turtle', 'link'),
(3, 'ner_PER_ORG_LOC_en_all', 'ner', '/e-nlp/namedEntityRecognition', 'ner', 'ner-wikinerEn_PER;ner-wikinerEn_ORG;ner-wikinerEn_LOC', 'en', 'turtle', 'turtle', 'all'),
(4, 'temp_en', 'timex', '/e-nlp/namedEntityRecognition', 'temp', 'englishDates', 'en', 'turtle', 'turtle', 'none'),
(6, 'dict11_ORG', 'dict', '/e-nlp/namedEntityRecognition', 'dict', 'dict11_ORG', 'en', 'turtle', 'turtle', ''),
(20, 'translateENDE', 'translate', '/e-smt', NULL, 'de', 'en', 'turtle', 'turtle', 'text/plain'),
(21, 'translateENES', 'translate', '/e-smt', NULL, 'es', 'en', 'turtle', 'turtle', 'text/plain'),
(37, 'DictionaryJMS_OTHER', 'dict', '/e-nlp/namedEntityRecognition', 'dict', 'DictionaryJMS_OTHER', 'en', 'turtle', 'turtle', ''),
(38, 'Dictionary13_OTHER', 'dict', '/e-nlp/namedEntityRecognition', 'dict', 'Dictionary13_OTHER', 'en', 'turtle', 'turtle', ''),
(39, 'mendelsohn_PER_OTHER', 'dict', '/e-nlp/namedEntityRecognition', 'dict', 'mendelsohn_PER_OTHER', 'en', 'turtle', 'turtle', ''),
(40, 'fronteoDict_ORG_OTHER', 'dict', '/e-nlp/namedEntityRecognition', 'dict', 'fronteoDict_ORG_OTHER', 'en', 'turtle', 'turtle', ''),
(41, 'fronteoDict_ORG', 'dict', '/e-nlp/namedEntityRecognition', 'dict', 'fronteoDict_ORG', 'en', 'turtle', 'turtle', ''),
(42, 'sent_en_corenlp', 'sent', '/e-sentimentanalysis', 'sent', 'corenlp', 'en', 'turtle', 'turtle', 'none');

CREATE TABLE IF NOT EXISTS `Users` (
  `userId` int(11) NOT NULL AUTO_INCREMENT,
  `user` varchar(20) NOT NULL,
  `password` varchar(1024) NOT NULL,
  `name` varchar(150) NOT NULL,
  `role` varchar(10) NOT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `user` (`user`)
)  ;


