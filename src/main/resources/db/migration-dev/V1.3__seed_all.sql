--- ENDERECO
INSERT INTO "endereco" ("end_id", "end_tipo_logradouro", "end_logradouro", "end_numero", "end_bairro", "cid_id") VALUES
(1,	'Estrada',	'xyz',	420,	'Centro',	9993),
(2,	'Rua',	'abc',	69,	'Zona Leste',	9995),
(3,	'Avenida',	'Tancredo Neves',	1010,	'Central Norte',	87),
(4,	'Rodovia',	'BR 403',	0,	'São José',	145),
(7,	'Alameda',	'das Flores',	251,	'Parque Celeste',	350),
(8,	'Rua',	'23 de Março',	1806,	'Amores',	2011),
(9,	'Avenida',	'Biro-biro',	50,	'Arroz Doce',	5),
(10,	'Alameda',	'Fioravanço',	2300,	'Coabi',	31),
(11,	'Estrada',	'da Morte',	666,	'Vale do Cemitério',	758);

SELECT setval(pg_get_serial_sequence('endereco', 'end_id'), COALESCE((SELECT MAX(end_id) + 1 FROM endereco), 1), false);
--- ENDERECO

--- PESSOA
INSERT INTO "pessoa" ("pes_id", "pes_nome", "pes_data_nascimento", "pes_sexo", "pes_mae", "pes_pai") VALUES
(1,	'Josney Silva',	'2000-03-09',	'Masculino',	'Mariazinha Joana',	'Gerson Geraldo'),
(2,	'Julia Jasmin',	'1999-03-08',	'Feminino',	'Mariquinha Moana',	'João Pedro'),
(3,	'Carlos Alexandre',	'1963-06-25',	'Masculino',	'Antônia Felícia',	'Nonato Alexandre'),
(4,	'Francisco Chagas',	'2004-11-14',	'Masculino',	'Tina Gusman',	'Manoel Chagas'),
(5,	'Marieta Rosa',	'1980-10-12',	'Feminino',	'Vânia Vampi',	'João Rosa'),
(6,	'Bella Pocas',	'1992-12-02',	'Feminino',	'Ceci Jucá',	'Luigi Pocas');

SELECT setval(pg_get_serial_sequence('pessoa', 'pes_id'), COALESCE((SELECT MAX(pes_id) + 1 FROM pessoa), 1), false);
--- PESSOA

--- UNIDADE
INSERT INTO "unidade" ("unid_id", "unid_nome", "unid_sigla") VALUES
(1,	'Casinha do Centro',	'CDC'),
(2,	'Tropa de Elite',	'BOPE'),
(3,	'Unidade de Apoio Regional',	'UAR');

SELECT setval(pg_get_serial_sequence('unidade', 'unid_id'), COALESCE((SELECT MAX(unid_id) + 1 FROM unidade), 1), false);
--- UNIDADE

--- LOTACAO
INSERT INTO "lotacao" ("lot_id", "pes_id", "unid_id", "lot_data_lotacao", "lot_data_remocao", "lot_portaria") VALUES
(6,	6,	3,	'2024-10-01',	NULL,	'ghi-456'),
(5,	5,	3,	'2000-12-10',	NULL,	'ghi-123'),
(4,	4,	2,	'2015-04-15',	'2020-03-30',	'def-456'),
(3,	3,	2,	'2013-06-02',	NULL,	'def-123'),
(2,	2,	1,	'2022-03-09',	'2022-03-09',	'abc-456'),
(1,	1,	1,	'2022-03-09',	'2022-03-09',	'abc-123');

SELECT setval(pg_get_serial_sequence('lotacao', 'lot_id'), COALESCE((SELECT MAX(lot_id) + 1 FROM lotacao), 1), false);
--- LOTACAO

INSERT INTO "pessoa_endereco" ("pes_id", "end_id") VALUES
(3,	3),
(4,	4),
(1,	10),
(2,	11),
(5,	7),
(6,	8);

INSERT INTO "servidor_efetivo" ("pes_id", "se_matricula") VALUES
(4,	'abc-456'),
(6,	'abc-789'),
(2,	'abc-123');

INSERT INTO "servidor_temporario" ("pes_id", "st_data_admissao", "st_data_demissao") VALUES
(1,	'2022-03-09',	'2022-03-09'),
(3,	'2013-06-02',	'2026-06-02'),
(5,	'2000-12-10',	'2005-12-10');

INSERT INTO "unidade_endereco" ("unid_id", "end_id") VALUES
(1,	2),
(2,	1),
(3,	9);

--INSERT INTO "foto_pessoa" ("fp_id", "pes_id", "fp_data", "fp_bucket", "fp_hash") VALUES
--(2,	1,	'2025-03-23',	'govmt-bucket',	'4951fb11ada213ba7f7707014c01236e.png');