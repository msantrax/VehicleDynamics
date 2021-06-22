
create schema vega;
set current schema vega;

/* ==================== TABELA DE CONTROLE =========================================== */
DROP TABLE controle;

CREATE TABLE controle (
	
	id				integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	tabela			char(20),
	campo			char(20),
	campo_seq		integer NOT NULL,
	tipo			char(20),
	comp			integer DEFAULT 0,
	widget			char(20),
	ref_tbl			char(20),
	ref_campo		char(20),
	tbl_label		char(20),
	tbl_icon		char(20),
	porta			integer DEFAULT 0,
	synchro			integer DEFAULT 0,
	flag1			integer DEFAULT 0,
	flag2			integer DEFAULT 0,
	coment			varchar(256)
	
);

ALTER TABLE controle 
	ADD CONSTRAINT controle_pk Primary Key (id);


/* ==================== TABELA DE DESCRITORES =========================================== */
DROP TABLE descrip;

CREATE TABLE descrip (

	id			    integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	tabela			character(20),
	device			smallint DEFAULT 1,
	criada			timestamp DEFAULT CURRENT_TIMESTAMP,
	usuario		    character(20),
	islock 		    smallint DEFAULT 0,
	tag1			smallint DEFAULT 0,
	tag2			smallint DEFAULT 0,
	comando	     	varchar(2048)
);

ALTER TABLE descrip 
	ADD CONSTRAINT descrip_pk Primary Key (id);


                                  
 
