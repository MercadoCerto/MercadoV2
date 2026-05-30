-- =====================================================
-- Script de migração para alinhar banco existente
-- com as entidades JPA do projeto MercadoCerto
-- Execute este script se você já tem o banco criado
-- =====================================================

-- 1. Adicionar latitude e longitude na tabela mercado
ALTER TABLE `mercado`
  ADD COLUMN IF NOT EXISTS `latitude` double DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `longitude` double DEFAULT NULL;

-- 2. Adicionar tipo_conta e cnpj na tabela usuario
ALTER TABLE `usuario`
  ADD COLUMN IF NOT EXISTS `tipo_conta` varchar(20) NOT NULL DEFAULT 'USUARIO',
  ADD COLUMN IF NOT EXISTS `cnpj` varchar(255) DEFAULT NULL;

-- Adicionar constraint UNIQUE no cnpj (se não existir)
ALTER TABLE `usuario`
  ADD UNIQUE KEY IF NOT EXISTS `cnpj` (`cnpj`);

-- 3. Permitir avaliações anônimas (id_usuario NULL)
ALTER TABLE `avaliacao`
  MODIFY COLUMN `id_usuario` int(11) DEFAULT NULL;

-- 4. Adicionar campos de endereço na tabela mercado
ALTER TABLE `mercado`
  ADD COLUMN IF NOT EXISTS `endereco` varchar(300) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `cidade` varchar(100) DEFAULT NULL;

-- 5. Adicionar campos de quantidade/peso na tabela produto
ALTER TABLE `produto`
  ADD COLUMN IF NOT EXISTS `quantidade` int DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `peso` double DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `unidade` varchar(10) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `tipo_medida` varchar(20) DEFAULT NULL;

-- 5.1. Inferir tipo_medida para produtos já cadastrados a partir da unidade.
--      PACK não é inferido — o comerciante precisa reabrir o cadastro para
--      marcar produtos multipack manualmente.
UPDATE `produto`
   SET `tipo_medida` = CASE
        WHEN `unidade` IN ('kg','g')  THEN 'PESO'
        WHEN `unidade` IN ('L','ml')  THEN 'VOLUME'
        WHEN `unidade` = 'un'         THEN 'UNIDADE'
        ELSE 'UNIDADE'
   END
 WHERE `tipo_medida` IS NULL;

-- 6. Adicionar foto do mercado
ALTER TABLE `mercado`
  ADD COLUMN IF NOT EXISTS `foto` varchar(255) DEFAULT NULL;

-- 7. Permitir tipo_conta ADMIN na tabela usuario
ALTER TABLE `usuario`
  MODIFY COLUMN `tipo_conta` varchar(20) NOT NULL DEFAULT 'USUARIO';

-- 8. Criar tabela de tokens de recuperação de senha
CREATE TABLE IF NOT EXISTS `password_reset_token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `token` varchar(100) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `expiracao` datetime NOT NULL,
  `usado` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `prt_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
