CREATE TABLE cidade (
    cid_id INT PRIMARY KEY,
    cid_nome VARCHAR(200) NOT NULL,
    cid_uf VARCHAR(2) NOT NULL
);

CREATE TABLE endereco (
    end_id INT PRIMARY KEY,
    end_tipo_logradouro VARCHAR(50) NOT NULL,
    end_logradouro VARCHAR(200) NOT NULL,
    end_numero INT NOT NULL,
    end_bairro VARCHAR(100) NOT NULL,
    cid_id INT NOT NULL,
    FOREIGN KEY (cid_id) REFERENCES cidade(cid_id) ON DELETE CASCADE
);

CREATE TABLE pessoa (
    pes_id INT PRIMARY KEY,
    pes_nome VARCHAR(200) NOT NULL,
    pes_data_nascimento DATE NOT NULL,
    pes_sexo VARCHAR(9) NOT NULL,
    pes_mae VARCHAR(200) NOT NULL,
    pes_pai VARCHAR(200) NOT NULL
);

CREATE TABLE unidade (
    unid_id INT PRIMARY KEY,
    unid_nome VARCHAR(200) NOT NULL,
    unid_sigla VARCHAR(20) NOT NULL
);

CREATE TABLE unidade_endereco (
    unid_id INT NOT NULL,
    end_id INT NOT NULL,
    PRIMARY KEY (unid_id, end_id),
    FOREIGN KEY (unid_id) REFERENCES unidade(unid_id) ON DELETE CASCADE,
    FOREIGN KEY (end_id) REFERENCES endereco(end_id) ON DELETE CASCADE
);

CREATE TABLE lotacao (
    lot_id INT PRIMARY KEY,
    pes_id INT NOT NULL,
    unid_id INT NOT NULL,
    lot_data_lotacao DATE NOT NULL,
    lot_data_remocao DATE,
    lot_portaria VARCHAR(100) NOT NULL,
    FOREIGN KEY (pes_id) REFERENCES pessoa(pes_id) ON DELETE CASCADE,
    FOREIGN KEY (unid_id) REFERENCES unidade(unid_id) ON DELETE CASCADE
);

CREATE TABLE servidor_efetivo (
    pes_id INT PRIMARY KEY,
    se_matricula VARCHAR(20) NOT NULL,
    FOREIGN KEY (pes_id) REFERENCES pessoa(pes_id) ON DELETE CASCADE
);

CREATE TABLE servidor_temporario (
    pes_id INT PRIMARY KEY,
    st_data_admissao DATE NOT NULL,
    st_data_demissao DATE,
    FOREIGN KEY (pes_id) REFERENCES pessoa(pes_id) ON DELETE CASCADE
);

CREATE TABLE pessoa_endereco (
    pes_id INT NOT NULL,
    end_id INT NOT NULL,
    PRIMARY KEY (pes_id, end_id),
    FOREIGN KEY (pes_id) REFERENCES pessoa(pes_id) ON DELETE CASCADE,
    FOREIGN KEY (end_id) REFERENCES endereco(end_id) ON DELETE CASCADE
);

CREATE TABLE foto_pessoa (
    fp_id INT PRIMARY KEY,
    pes_id INT NOT NULL,
    fp_data DATE NOT NULL,
    fp_bucket VARCHAR(50) NOT NULL,
    fp_hash VARCHAR(50) NOT NULL,
    FOREIGN KEY (pes_id) REFERENCES pessoa(pes_id) ON DELETE CASCADE
);

