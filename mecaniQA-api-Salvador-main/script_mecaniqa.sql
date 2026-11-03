CREATE DATABASE IF NOT EXISTS mecaniqa
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE mecaniqa;

-- 1. Tabela de Peças
CREATE TABLE IF NOT EXISTS pecas (
                                     codigo_unico BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     nome VARCHAR(255) NOT NULL,
    preco DOUBLE NOT NULL DEFAULT 0.0,
    data_cadastro DATETIME NOT NULL,
    data_ultima_atualizacao DATETIME NULL
    );

-- 2. Tabela de Serviços
CREATE TABLE IF NOT EXISTS servicos (
                                        codigo_unico BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        nome VARCHAR(255) NOT NULL,
    preco DOUBLE NOT NULL DEFAULT 0.0,
    data_criacao DATETIME NOT NULL,
    data_ultima_atualizacao DATETIME NULL
    );

-- 3. Tabela de Pedidos de Peças
CREATE TABLE IF NOT EXISTS pedidos (
                                       codigo_unico BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       fornecedor VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    valor_total DOUBLE NOT NULL DEFAULT 0.0,
    data_pedido DATETIME NOT NULL,
    data_ultima_atualizacao DATETIME NULL
    );

-- 4. Tabela Associativa: Itens do Pedido de Peças (sem UNIQUE composto para permitir repetição de peça)
CREATE TABLE IF NOT EXISTS itens_pedido_peca (
                                                 codigo_unico BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                 pedido_id BIGINT NOT NULL,
                                                 peca_id BIGINT NOT NULL,
                                                 quantidade INT NOT NULL,
                                                 preco_unitario DOUBLE NOT NULL,
                                                 CONSTRAINT fk_item_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(codigo_unico) ON DELETE CASCADE,
    CONSTRAINT fk_item_peca FOREIGN KEY (peca_id) REFERENCES pecas(codigo_unico) ON DELETE CASCADE
    );

-- 5. Tabela de Ordens de Serviço
CREATE TABLE IF NOT EXISTS ordens_servico (
                                              codigo_unico BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              descricao_problema TEXT NOT NULL,
                                              status VARCHAR(50) NOT NULL,
    valor_total DOUBLE NOT NULL DEFAULT 0.0,
    data_abertura DATETIME NOT NULL,
    data_fechamento DATETIME NULL,
    data_ultima_atualizacao DATETIME NULL
    );

-- 6. Tabela Associativa: Ordem de Serviço x Serviços
CREATE TABLE IF NOT EXISTS os_servicos (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           os_id BIGINT NOT NULL,
                                           servico_id BIGINT NOT NULL,
                                           CONSTRAINT fk_os_servico_os FOREIGN KEY (os_id) REFERENCES ordens_servico(codigo_unico) ON DELETE CASCADE,
    CONSTRAINT fk_os_servico_srv FOREIGN KEY (servico_id) REFERENCES servicos(codigo_unico) ON DELETE CASCADE
    );

-- 7. Tabela Associativa: Ordem de Serviço x Pedidos de Peças
CREATE TABLE IF NOT EXISTS os_pedidos (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          os_id BIGINT NOT NULL,
                                          pedido_id BIGINT NOT NULL,
                                          CONSTRAINT fk_os_pedido_os FOREIGN KEY (os_id) REFERENCES ordens_servico(codigo_unico) ON DELETE CASCADE,
    CONSTRAINT fk_os_pedido_ped FOREIGN KEY (pedido_id) REFERENCES pedidos(codigo_unico) ON DELETE CASCADE
    );

-- Carga Inicial (DML) para Testes
INSERT INTO pecas (nome, preco, data_cadastro) VALUES
                                                   ('Filtro de Óleo Motor', 45.90, NOW()),
                                                   ('Pastilha de Freio Dianteira', 180.00, NOW());

INSERT INTO servicos (nome, preco, data_criacao) VALUES
                                                     ('Troca de Óleo e Filtro', 90.00, NOW()),
                                                     ('Revisão do Sistema de Freios', 150.00, NOW());