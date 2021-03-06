
set current schema vega;

/* ==================== TABELA DE USUARIOS =========================================== */
DROP TABLE USERS;

CREATE TABLE USERS (
	
	ID			integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
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


/* ==================== TABELA DE GRUPOS =========================================== */
DROP TABLE GRUPOS;

CREATE TABLE GRUPOS (
	
	ID				integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	NOME			char(50),
	CRIADO			timestamp DEFAULT CURRENT_TIMESTAMP,
	MODIFICADO		timestamp DEFAULT CURRENT_TIMESTAMP,
	COMENT			varchar(256)	
);

ALTER TABLE GRUPOS ADD CONSTRAINT grupos_pk Primary Key (ID);


/* ==================== TABELA DE MAPAS =========================================== */
DROP TABLE CLASSMAP;

CREATE TABLE CLASSMAP (
	
	ID				integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	NODE			integer DEFAULT 0,
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
DROP TABLE CLASSTEMP;
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
	COMENT			varchar(256)
	FLAG1			integer DEFAULT 0,
	FLAG2			integer DEFAULT 0	
);
ALTER TABLE CLASSTEMP ADD CONSTRAINT classtemp_pk Primary Key (ID);
ALTER TABLE CLASSTEMP ADD CONSTRAINT classtemp_dono_fk FOREIGN KEY  (DONO) REFERENCES USERS (ID);
ALTER TABLE CLASSTEMP ADD CONSTRAINT classtemp_grupos_fk FOREIGN KEY (GRUPO) REFERENCES GRUPOS (ID);


/* ==================== TABELA DE PROGRAMAS =========================================== */
DROP TABLE PGNS;

CREATE TABLE PGNS (
	
	ID				integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	REAGENTE		char(15),
	DESCR			char(80),
	CTRL			integer default 1
);

ALTER TABLE PGNS ADD CONSTRAINT pgns_pk Primary Key (ID);


/* ==================== TABELA DE PASSOS =========================================== */
DROP TABLE PASSOS;

CREATE TABLE PASSOS (
	
	ID				integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	TIPO			integer DEFAULT 0,
	KEYWORD			CHAR(60),
	PARAM1			double,
	PARAM2			double,
	COMENT			varchar(256)	
);

ALTER TABLE PASSOS ADD CONSTRAINT passos_pk Primary Key (ID);


/* ==================== TABELA DE EVENTOS =========================================== */
DROP TABLE EVENTOS;

CREATE TABLE EVENTOS (
	
	ID				integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	PGN				integer default 0,
	TIMEON			timestamp DEFAULT CURRENT_TIMESTAMP,
	TIMEOFF			timestamp DEFAULT CURRENT_TIMESTAMP,
	EXITCODE	    integer default 1
);

ALTER TABLE EVENTOS ADD CONSTRAINT eventos_pk Primary Key (ID);
ALTER TABLE EVENTOS ADD CONSTRAINT eventos_fk FOREIGN KEY (PGN) REFERENCES PGNS (ID);

/* ==================== TABELA DE IDS =========================================== */
DROP TABLE IDS;

CREATE TABLE IDS (
	
	ID				integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	EVENTO			integer default 0,
	CLIENTE			integer default 0,
	FRASCO			integer default 0,
	ANALISE			varchar(256),
	VOLUME			double,
	REAGENTE		char(20),
	MASSA			double,
	OK			    integer default 1
);

ALTER TABLE IDS ADD CONSTRAINT ids_pk Primary Key (ID);
ALTER TABLE IDS ADD CONSTRAINT ids_evento_fk FOREIGN KEY (EVENTO) REFERENCES EVENTOS (ID);
ALTER TABLE IDS ADD CONSTRAINT ids_cliente_fk FOREIGN KEY (CLIENTE) REFERENCES CLIENTES (ID);


/* ==================== TABELA DE CLIENTES =========================================== */
DROP TABLE CLIENTES;

CREATE TABLE CLIENTES (
	
	ID				integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	NOME			char(80)
);

ALTER TABLE CLIENTES ADD CONSTRAINT clientes_pk Primary Key (ID);

CREATE TABLE VERSION (
	
	ID			integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	TRUNK			integer DEFAULT 0,
	MAJOR			integer DEFAULT 0,
	MINOR			integer DEFAULT 0,
	CRIADO			timestamp DEFAULT CURRENT_TIMESTAMP,
	COMENT			varchar(256)	
);

ALTER TABLE VERSION ADD CONSTRAINT version_pk Primary Key (ID);
