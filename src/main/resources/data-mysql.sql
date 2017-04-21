INSERT INTO buyer (`buyer_id`,`name`,`team`) VALUES
(1,'Landmark','TL-Rajeshwari'),
(2,'UCB','TL-Rajeshwari'),
(3,'Strong Suit','TL-Rajeshwari'),
(4,'Edifice','TL-Rajeshwari'),
(5,'Other','TL-Rajeshwari'),
(6,'TML - Formal','TL-Amit'),
(7,'TML - Casual & Women','TL-Amit'),
(8,'Carrefour','TL-Amit'),
(9,'PRL','TL-Amit'),
(10,'Moss Bros','TL-Amit'),
(11,'Other','TL-Amit'),
(12,'Dressmann','TL-Tanushree'),
(13,'Reiss','TL-Tanushree'),
(14,'Jaeger','TL-Tanushree'),
(15,'Esprit','TL-Tanushree'),
(16,'BS- Selected','TL-Tanushree'),
(17,'BS- Jack & Jones','TL-Tanushree'),
(18,'BS- Jack & Jones India','TL-Tanushree'),
(19,'Tommy Hilfiger','TL-Tanushree'),
(20,'Other','TL-Tanushree'),
(21,'Thomas Pink','TL-Malini'),
(22,'Celio','TL-Malini'),
(23,'Defursac','TL-Malini'),
(24,'CT','TL-Malini'),
(25,'Hackett','TL-Malini'),
(26,'Mango','TL-Malini'),
(27,'Peter Jackson','TL-Malini'),
(28,'H&C','TL-Malini'),
(29,'Other','TL-Malini'),
(29,'Indian Terrain','TL-Smitha'),
(29,'Other','TL-Smitha');

INSERT INTO problem (`prob_id`,`name`,`department`) VALUES
(1,'Loading Problem','Cutting'),
(2,'Re-Cutting Problem','Cutting'),
(3,'Other Problem','Cutting'),
(4,'Operator Discipline','Human Resource'),
(5,'Unplanned Absenteeism','Human Resource'),
(6,'Other Problem','Human Resource'),
(7,'Allocated Operator Unavailable','Industrial Engineering'),
(8,'Machine not available','Industrial Engineering'),
(9,'Balancing Not Done','Industrial Engineering'),
(10,'Other Problem','Industrial Engineering'),
(11,'Machine Breakdown','Maintenance'),
(12,'Machine Settings','Maintenance'),
(13,'Other Problem','Maintenance'),
(14,'Cutting Quality','Quality'),
(15,'Sewing alteration','Quality'),
(16,'Quality approval','Quality'),
(17,'Other Problem','Quality'),
(18,'Quality Problem','Trims'),
(19,'Wrong Trims Issued','Trims'),
(20,'Mixing Trims Problem','Trims'),
(21,'Other Problem','Trims'),
(22,'Operator Handling Problem','Training'),
(23,'Skills Problem','Training'),
(24,'Other Problem','Training'),
(25,'No Pulling','Finishing'),
(26,'Other Problem','Finishing'),
(27,'No Monitoring','Production'),
(28,'Feeding Helper Problem','Production'),
(29,'Other Problem','Production');

INSERT INTO `designation` (`desgn_id`, `name`, `line`) VALUES
(1, 'Floor IE 1st Floor', '1,2,3,4'),
(2, 'Floor IE 2nd Floor', '5,6,7,8'),
(3, 'Cutting Executive 1st Floor', '1,2,3,4'),
(4, 'Cutting Executive 2nd Floor', '5,6,7,8'),
(5, 'Production Manager 1st Floor', '1,2,3,4'),
(6, 'Production Manager 2nd Floor', '5,6,7,8'),
(7, 'Senior Executive Quality 1st Floor', '1,2,3,4'),
(8, 'Senior Executive Quality 2nd Floor', '5,6,7,8'),
(9, 'Senior Mechanic 1st Floor', '1,2,3,4'),
(10, 'Senior Mechanic 2nd Floor', '5,6,7,8'),
(11, 'Welfare Officer 1st Floor', '1,2,3,4'),
(12, 'Welfare Officer 2nd Floor', '5,6,7,8'),
(13, 'Input Quality Manager', '1,2,3,4,5,6,7,8'),
(14, 'Trim Controller', '1,2,3,4,5,6,7,8'),
(15, 'Mechanic Line 1', '1'),
(16, 'Mechanic Line 2', '2'),
(17, 'Mechanic Line 3', '3'),
(18, 'Mechanic Line 4', '4'),
(19, 'Mechanic Line 5', '5'),
(20, 'Mechanic Line 6', '6'),
(21, 'Mechanic Line 7', '7'),
(22, 'Mechanic Line 8', '8'),
(23, 'Senior Electrician', '1,2,3,4,5,6,7,8'),
(24, 'Head Mechanics', '1,2,3,4,5,6,7,8'),
(25, 'Assitant Manager Cutting', '1,2,3,4,5,6,7,8'),
(26, 'Manager Cutting', '1,2,3,4,5,6,7,8'),
(27, 'Quality Manager', '1,2,3,4,5,6,7,8'),
(28, 'Assistant Factory Manager', '1,2,3,4,5,6,7,8'),
(29, 'Assistant Manager Maintenance', '1,2,3,4,5,6,7,8'),
(30, 'Manager Maintenance', '1,2,3,4,5,6,7,8'),
(31, 'IE Manager', '1,2,3,4,5,6,7,8'),
(32, 'Senior Manager Projects', '1,2,3,4,5,6,7,8'),
(33, 'Senior Manager Quality', '1,2,3,4,5,6,7,8'),
(34, 'Assistant Manager HR', '1,2,3,4,5,6,7,8'),
(35, 'HR Head', '1,2,3,4,5,6,7,8'),
(36, 'Factory Manager', '1,2,3,4,5,6,7,8'),
(37, 'Head of Operations', '1,2,3,4,5,6,7,8'),
(38, 'Technical and Quality Head', '1,2,3,4,5,6,7,8'),
(39, 'Washing Coordinator', '1,2,3,4,5,6,7,8'),
(40, 'Line IE', '1,2,3,4,5,6,7,8'),
(41, 'Line Incharge', '1,2,3,4,5,6,7,8'),
(42, 'Team Leader Quality', '1,2,3,4,5,6,7,8'),
(43, 'SMED Executive', '1,2,3,4,5,6,7,8'),
(44, 'Line 1 Trainer', '1'),
(45, 'Line 2 Trainer', '2'),
(46, 'Line 3 Trainer', '3'),
(47, 'Line 4 Trainer', '4'),
(48, 'Line 5 Trainer', '5'),
(49, 'Line 6 Trainer', '6'),
(50, 'Line 7 Trainer', '7'),
(51, 'Line 8 Trainer', '8'),
(52, 'Planning Executive', '1,2,3,4,5,6,7,8'),
(53, 'Supervisor', '1,2,3,4,5,6,7,8'),
(54, 'Training Head', '1,2,3,4,5,6,7,8'),
(55, 'Pre-Production', '1,2,3,4,5,6,7,8'),
(56, 'Finishing Incharge', '1,2,3,4,5,6,7,8');

INSERT INTO `DESIGNATION_PROBLEM` (`prob_id`,`desgn_id`) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,25),(1,26),(1,52),
(2,1),(2,2),(2,3),(2,4),(2,5),(2,6),(2,25),(2,26),
(3,1),(3,2),(3,3),(3,4),(3,5),(3,6),(3,25),(3,26),
(4,11),(4,12),(4,34),(4,35),
(5,1),(5,2),(5,5),(5,6),(5,11),(5,12),(5,34),(5,35),
(6,2),(6,5),(6,6),(6,11),(6,12),(6,34),(6,35),
(7,1),(7,2),(7,5),(7,6),(7,31),(7,32),
(8,1),(8,2),(8,5),(8,6),(8,31),(8,32),
(9,1),(9,2),(9,5),(9,6),(9,31),(9,32),
(10,1),(10,2),(10,5),(10,6),(10,31),(10,32),
(11,9),(11,10),(11,15),(11,16),(11,17),(11,18),(11,19),(11,20),(11,21),(11,22),(11,23),(11,24),(11,29),(11,30),
(12,9),(12,10),(12,15),(12,16),(12,17),(12,18),(12,19),(12,20),(12,21),(12,22),(12,23),(12,24),(12,29),(12,30),
(13,9),(13,10),(13,15),(13,16),(13,17),(13,18),(13,19),(13,20),(13,21),(13,22),(13,23),(13,24),(13,29),(13,30),
(14,1),(14,2),(14,3),(14,4),(14,5),(14,6),(14,7),(14,8),(14,25),(14,26),(14,27),(14,33),(14,38),
(15,7),(15,8),(15,27),(15,33),(15,38),
(16,7),(16,8),(16,27),(16,33),(16,38),(16,55),
(17,7),(17,8),(17,27),(17,33),(17,38),(17,55),
(18,7),(18,8),(18,13),(18,27),(18,28),(18,33),(18,38),
(19,1),(19,2),(19,5),(19,6),(19,14),(19,28),
(20,1),(20,2),(20,5),(20,6),(20,14),(20,28),
(21,1),(21,2),(21,5),(21,6),(21,14),(21,28),
(22,32),(22,44),(22,45),(22,46),(22,47),(22,48),(22,49),(22,50),(22,51),(22,54),
(23,32),(23,44),(23,45),(23,46),(23,47),(23,48),(23,49),(23,50),(23,51),(23,54),
(24,32),(24,44),(24,45),(24,46),(24,47),(24,48),(24,49),(24,50),(24,51),(24,54),
(25,28),(25,39),(25,56),
(26,28),(26,39),(26,56),
(27,1),(27,2),(27,5),(27,6),
(28,1),(28,2),(28,5),(28,6),
(29,1),(29,2),(29,5),(29,6);


