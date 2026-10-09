-- MiniProject : schema สำหรับ freeSQLdatabase (รันใน phpMyAdmin > SQL)
-- Host: sql12.freesqldatabase.com | DB: sql12838569

CREATE TABLE IF NOT EXISTS `user` (
  `id`       VARCHAR(20) NOT NULL,   -- รหัสนักศึกษา (student) / email-prefix (teacher) / username (admin)
  `password` VARCHAR(20) NOT NULL,
  `type`     VARCHAR(5)  NOT NULL DEFAULT '2',   -- 1=admin, 2=นักศึกษา/ผู้ใช้ทั่วไป, 3=อาจารย์
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- admin เริ่มต้น (type 1) + user ตัวอย่าง (type 2)
INSERT INTO `user` (`id`, `password`, `type`) VALUES
  ('admin', '1234', '1'),
  ('t1', 't1', '1'),
  ('t2', 't2', '2')
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`), `type` = VALUES(`type`);
