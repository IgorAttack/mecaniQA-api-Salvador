DROP DATABASE IF EXISTS mecaniqa;
CREATE DATABASE mecaniqa;
USE mecaniqa;

-- Tabela de Peças
CREATE TABLE pecas (
                       codigo_unico BIGINT AUTO_INCREMENT PRIMARY KEY,
                       nome VARCHAR(255) NOT NULL,
                       preco DECIMAL(10, 2) NOT NULL,
                       data_cadastro DATETIME,
                       data_ultima_atualizacao DATETIME
);

-- Tabela de Pedidos
CREATE TABLE pedidos (
                         codigo_unico BIGINT AUTO_INCREMENT PRIMARY KEY,
                         fornecedor VARCHAR(255),
                         status VARCHAR(50),
                         valor_total DECIMAL(10, 2),
                         data_pedido DATETIME,
                         data_ultima_atualizacao DATETIME
);

CREATE TABLE itens_pedido_peca (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   pedido_id BIGINT NOT NULL,
                                   peca_id BIGINT NOT NULL,
                                   quantidade INT NOT NULL,
                                   preco_unitario DECIMAL(10, 2) NOT NULL,
                                   FOREIGN KEY (pedido_id) REFERENCES pedidos(codigo_unico) ON DELETE CASCADE,
                                   FOREIGN KEY (peca_id) REFERENCES pecas(codigo_unico)
);

INSERT INTO pecas (nome, preco, data_cadastro) VALUES ('Filtro de Óleo', 35.90, NOW());
INSERT INTO pedidos (fornecedor, status, valor_total, data_pedido) VALUES ('AutoParts S.A', 'ORCANDO', 0.0, NOW());