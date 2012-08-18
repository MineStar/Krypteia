-- -----------------------------------------------------
-- Table `blocks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `blocks` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `blockID` INT NOT NULL ,
  `world` VARCHAR(32) NOT NULL ,
  `x` DOUBLE NOT NULL ,
  `y` DOUBLE NOT NULL ,
  `z` DOUBLE NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mobs`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mobs` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `mobID` INT NOT NULL ,
  `world` VARCHAR(32) NOT NULL ,
  `x` DOUBLE NOT NULL ,
  `y` DOUBLE NOT NULL ,
  `z` DOUBLE NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;