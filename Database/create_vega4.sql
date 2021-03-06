
set current schema vega;

drop table canais;
drop table classmap;
drop table classtemp;
drop table users;
drop table grupos;


/* ==================== TABELA DE GRUPOS =========================================== */
CREATE TABLE GRUPOS (
	
	ID				integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	NOME			char(50),
	CRIADO			timestamp DEFAULT CURRENT_TIMESTAMP,
	MODIFICADO		timestamp DEFAULT CURRENT_TIMESTAMP,
	COMENT			varchar(256)	
);

ALTER TABLE GRUPOS ADD CONSTRAINT grupos_pk Primary Key (ID);


/* ==================== TABELA DE USUARIOS =========================================== */
CREATE TABLE USERS (
	
	ID				integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	LOGIN			char(12),
	NOME			char(50),
	SENHA			char(30),
	GRUPO			integer DEFAULT 0,
	CRIADO			timestamp DEFAULT CURRENT_TIMESTAMP,
	MODIFICADO		timestamp DEFAULT CURRENT_TIMESTAMP,
	SECRET			varchar(256),
	RESPOSTA		char(30),
	COMENT			varchar(256)	
);

ALTER TABLE USERS ADD CONSTRAINT users_pk Primary Key (ID);
ALTER TABLE USERS ADD CONSTRAINT users_fk FOREIGN KEY(GRUPO) REFERENCES GRUPOS (ID);


/* ==================== TABELA DE MAPAS =========================================== */

CREATE TABLE CLASSMAP (
	
	ID				integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	NODE			CHAR(30),
	PARENT			integer NOT NULL,
	TIPO			integer DEFAULT 0,
	KEYWORD			CHAR(60),
	DTBLK			char(30),
	DTBLF			integer DEFAULT 0,
	DONO			integer DEFAULT 0,
	GRUPO			integer DEFAULT 0,
	PERM			integer DEFAULT 667,
	CRIADO			timestamp DEFAULT CURRENT_TIMESTAMP,
	MODIFICADO		timestamp DEFAULT CURRENT_TIMESTAMP,
	COMENT			varchar(256),
	FLAG1			integer DEFAULT 0,
	FLAG2			integer DEFAULT 0	
);

ALTER TABLE CLASSMAP ADD CONSTRAINT classmap_pk Primary Key (ID);
ALTER TABLE CLASSMAP ADD CONSTRAINT classmap_dono_fk FOREIGN KEY  (DONO) REFERENCES USERS (ID);
ALTER TABLE CLASSMAP ADD CONSTRAINT classmap_grupos_fk FOREIGN KEY (GRUPO) REFERENCES GRUPOS (ID);

/* ==================== TABELA DE MAPAS temporaria =========================================== */
CREATE TABLE CLASSTEMP (
	
	ID				integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	TAG				integer DEFAULT 0,
	NODEID			integer,
	NODE			CHAR(30),
	PARENT			integer NOT NULL,
	TIPO			integer DEFAULT 0,
	KEYWORD			CHAR(60),
	DTBLK			char(30),
	DTBLF			integer DEFAULT 0,
	DONO			integer DEFAULT 0,
	GRUPO			integer DEFAULT 0,
	PERM			integer DEFAULT 667,
	CRIADO			timestamp DEFAULT CURRENT_TIMESTAMP,
	MODIFICADO		timestamp DEFAULT CURRENT_TIMESTAMP,
	COMENT			varchar(256),
	FLAG1			integer DEFAULT 0,
	FLAG2			integer DEFAULT 0	
);
ALTER TABLE CLASSTEMP ADD CONSTRAINT classtemp_pk Primary Key (ID);
ALTER TABLE CLASSTEMP ADD CONSTRAINT classtemp_dono_fk FOREIGN KEY  (DONO) REFERENCES USERS (ID);
ALTER TABLE CLASSTEMP ADD CONSTRAINT classtemp_grupos_fk FOREIGN KEY (GRUPO) REFERENCES GRUPOS (ID);



/* ==================== TABELA DE Canais =========================================== */
CREATE TABLE CANAIS (

	ID				integer NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	MASCARA			CHAR(10) DEFAULT '',
	UNIDADE			CHAR(10) DEFAULT '',
    CANAL			CHAR(2) DEFAULT '00',
	TIPO			CHAR(2) DEFAULT '00',
    A0				CHAR(10) DEFAULT '0.0',
	A1				CHAR(10) DEFAULT '0.0',
    A2				CHAR(10) DEFAULT '0.0',
    A3				CHAR(10) DEFAULT '0.0',
    A4				CHAR(10) DEFAULT '0.0',
    A5				CHAR(10) DEFAULT '0.0',
    ROFFSET			CHAR(10) DEFAULT '0.0',
    RSLOPE			CHAR(10) DEFAULT '0.0',
    FLAG1			integer DEFAULT 0,
	FLAG2			integer DEFAULT 0
);

ALTER TABLE CANAIS ADD CONSTRAINT canais_pk Primary Key (ID);



/* ================================================= FIM DA CRIA????O DE TABELAS ==================== */



INSERT INTO GRUPOS (NOME,COMENT) VALUES ('Manutencao','Acesso total');
INSERT INTO GRUPOS (NOME,COMENT) VALUES ('Usuarios','Plebe rude');
INSERT INTO GRUPOS (NOME,COMENT) VALUES ('Admin','Usuarios administradores');

delete from users;

INSERT INTO USERS (LOGIN,NOME,SENHA,GRUPO,SECRET,RESPOSTA,COMENT) VALUES ('antrax','Antrax Tecnologia','00000',1,'Pergunta1','Resposta','Administrador');
INSERT INTO USERS (LOGIN,NOME,SENHA,GRUPO,SECRET,RESPOSTA,COMENT) VALUES ('ewerton','Ewerton Mecati','00000',3,'Pergunta1','Resposta','Administrador');
INSERT INTO USERS (LOGIN,NOME,SENHA,GRUPO,SECRET,RESPOSTA,COMENT) VALUES ('jader','Jader Nogueira','00000',3,'Pergunta1','Resposta','Administrador');
INSERT INTO USERS (LOGIN,NOME,SENHA,GRUPO,SECRET,RESPOSTA,COMENT) VALUES ('user1','Usuario 1','00000',2,'Pergunta1','Resposta','Comentario');
INSERT INTO USERS (LOGIN,NOME,SENHA,GRUPO,SECRET,RESPOSTA,COMENT) VALUES ('user2','Usuario 2','00000',3,'Pergunta1','Resposta','Comentario');
INSERT INTO USERS (LOGIN,NOME,SENHA,GRUPO,SECRET,RESPOSTA,COMENT) VALUES ('user3','Usuario 3','00000',3,'Pergunta1','Resposta','Comentario');
  
UPDATE USERS U SET U.GRUPO = (SELECT G.ID FROM GRUPOS G WHERE G.NOME = 'Manutencao') WHERE U.GRUPO = 1;
UPDATE USERS U SET U.GRUPO = (SELECT G.ID FROM GRUPOS G WHERE G.NOME = 'Usuarios') WHERE U.GRUPO = 2;
UPDATE USERS U SET U.GRUPO = (SELECT G.ID FROM GRUPOS G WHERE G.NOME = 'Admin') WHERE U.GRUPO = 3;



DELETE FROM CLASSMAP;

INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Banco',0,0,'',0,
    (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
    (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
    777,'Root do banco de dados');


INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Programas',
    (SELECT C.ID FROM CLASSMAP C WHERE C.PARENT = 0),
    1,
    '',0,
    (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
    (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
    777,  'Caminho para os programas de teste');

    
/* Item Normas */
INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Normas',
    (SELECT C.ID FROM CLASSMAP C WHERE C.PARENT = 0),
    2,
    '',0,
    (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
    (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
    777,  'Caminho para a biblioteca de Normas');

/* Item Relatorios */
INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Relatorios',
    (SELECT C.ID FROM CLASSMAP C WHERE C.PARENT = 0),
    3,
    '',0,
    (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
    (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
    777,  '');

/* Item Eventos */
INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Eventos',
    (SELECT C.ID FROM CLASSMAP C WHERE C.PARENT = 0),
    4,
    '',0,
    (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
    (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
    777,  'Area de armazenamento de resultados de tarefas');

/* Item Usuarios */
INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Usuarios',
    (SELECT C.ID FROM CLASSMAP C WHERE C.PARENT = 0),
    5,
    '',0,
    (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
    (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
    777,  'Registro de usuarios do sistema');











