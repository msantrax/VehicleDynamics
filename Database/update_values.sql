SET CURRENT SCHEMA VEGA;

DELETE FROM GRUPOS;

INSERT INTO GRUPOS (NOME,COMENT) VALUES ('Manuten�o','Acesso total');
INSERT INTO GRUPOS (NOME,COMENT) VALUES ('Usuarios','Plebe rude');
INSERT INTO GRUPOS (NOME,COMENT) VALUES ('Laboratorio','Usuarios administradores');

delete from users;

INSERT INTO USERS (LOGIN,NOME,SENHA,GRUPO,SECRET,RESPOSTA,COMENT) VALUES ('antrax','Antrax','00000',0,'Pergunta1','Resposta','Administrador');
INSERT INTO USERS (LOGIN,NOME,SENHA,GRUPO,SECRET,RESPOSTA,COMENT) VALUES ('brito','Marco Brito','00000',0,'Pergunta1','Resposta','Administrador');
INSERT INTO USERS (LOGIN,NOME,SENHA,GRUPO,SECRET,RESPOSTA,COMENT) VALUES ('fernando','Fernando','00000',0,'Pergunta1','Resposta','Administrador');
INSERT INTO USERS (LOGIN,NOME,SENHA,GRUPO,SECRET,RESPOSTA,COMENT) VALUES ('user1','Usuario 1','00000',2,'Pergunta1','Resposta','Comentario');
INSERT INTO USERS (LOGIN,NOME,SENHA,GRUPO,SECRET,RESPOSTA,COMENT) VALUES ('user2','Usuario 2','00000',1,'Pergunta1','Resposta','Comentario');
INSERT INTO USERS (LOGIN,NOME,SENHA,GRUPO,SECRET,RESPOSTA,COMENT) VALUES ('user3','Usuario 3','00000',1,'Pergunta1','Resposta','Comentario');
  
UPDATE USERS U SET U.GRUPO = (SELECT G.ID FROM GRUPOS G WHERE G.NOME = 'Manuten�o') WHERE U.GRUPO = 0;
UPDATE USERS U SET U.GRUPO = (SELECT G.ID FROM GRUPOS G WHERE G.NOME = 'Usuarios') WHERE U.GRUPO = 1;
UPDATE USERS U SET U.GRUPO = (SELECT G.ID FROM GRUPOS G WHERE G.NOME = 'Laboratorio') WHERE U.GRUPO = 2;



DELETE FROM CLASSMAP;

INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Banco',0,0,   '',0,
    (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
    (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
    777,  'Root do banco de dados');



INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Programas',
    (SELECT C.ID FROM CLASSMAP C WHERE C.PARENT = 0),
    1,
    '',0,
    (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
    (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
    777,  'Caminho para os programas de digest�');

    INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Org�icos',
        (SELECT C.ID FROM CLASSMAP C WHERE C.NODE = 'Programas'),
        11,
        '',0,
        (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
        (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
        777,  'Digest�s de materiais org�icos');
    INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Metais',
        (SELECT C.ID FROM CLASSMAP C WHERE C.NODE = 'Programas'),
        11,
        '',0,
        (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
        (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
        777,  'Digest�s de ligas metalicas');
    INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Solos',
        (SELECT C.ID FROM CLASSMAP C WHERE C.NODE = 'Programas'),
        11,
        '',0,
        (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
        (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
        777,  'Digest�s da area geoqu�ica');

/* Item Normas */
INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Normas',
    (SELECT C.ID FROM CLASSMAP C WHERE C.PARENT = 0),
    2,
    '',0,
    (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
    (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
    777,  'Caminho para a biblioteca de Normas');

    INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('EPA',
        (SELECT C.ID FROM CLASSMAP C WHERE C.NODE = 'Normas'),
        21,
        '',0,
        (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
        (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
        777,  'Normas EPA');
    INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('LabSolos',
        (SELECT C.ID FROM CLASSMAP C WHERE C.NODE = 'Normas'),
        21,
        '',0,
        (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
        (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
        777,  'Normas espec�icas do Lab de Solos');

/* Item Identifica�es */
INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Identifica�es',
    (SELECT C.ID FROM CLASSMAP C WHERE C.PARENT = 0),
    3,
    '',0,
    (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
    (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
    777,  'Padr�s de Identifica�o de tarefas');


/* Item Relat�ios */
INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Relat�ios',
    (SELECT C.ID FROM CLASSMAP C WHERE C.PARENT = 0),
    4,
    '',0,
    (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
    (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
    777,  'Padr�s de relat�ios de tarefas');

/* Item Eventos */
INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Eventos',
    (SELECT C.ID FROM CLASSMAP C WHERE C.PARENT = 0),
    5,
    '',0,
    (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
    (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
    777,  '�ea de armazenamento de resultados de tarefas');

/* Item Clientes */
INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Clientes',
    (SELECT C.ID FROM CLASSMAP C WHERE C.PARENT = 0),
    6,
    '',0,
    (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
    (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
    777,  'Identifica�o de clientes');

/* Item Usuarios */
INSERT INTO CLASSMAP (NODE,PARENT,TIPO,DTBLK,DTBLF,DONO,GRUPO,PERM,COMENT) VALUES ('Usu�ios',
    (SELECT C.ID FROM CLASSMAP C WHERE C.PARENT = 0),
    7,
    '',0,
    (SELECT U.ID FROM USERS U WHERE U.LOGIN = 'antrax') ,
    (SELECT U.GRUPO FROM USERS U WHERE U.LOGIN = 'antrax'),
    777,  'Registro de usu�ios do sistema');



