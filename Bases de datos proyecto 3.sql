-- Generado por Oracle SQL Developer Data Modeler 23.1.0.087.0806
--   en:        2024-10-11 21:08:26 COT
--   sitio:      Oracle Database 11g
--   tipo:      Oracle Database 11g



DROP TABLE deuda CASCADE CONSTRAINTS;

DROP TABLE factura CASCADE CONSTRAINTS;

DROP TABLE grupo CASCADE CONSTRAINTS;

DROP TABLE miembrogrupo CASCADE CONSTRAINTS;

DROP TABLE notificacion CASCADE CONSTRAINTS;

DROP TABLE preferencia CASCADE CONSTRAINTS;

DROP TABLE transaccion CASCADE CONSTRAINTS;

DROP TABLE usuario CASCADE CONSTRAINTS;

-- predefined type, no DDL - MDSYS.SDO_GEOMETRY

-- predefined type, no DDL - XMLTYPE

CREATE TABLE deuda (
    codigo_deuda             NUMBER(11) NOT NULL,
    monto                    NUMBER(15, 3) NOT NULL,
    fecha_registro           DATE NOT NULL,
    mora                     CHAR(1),
    estado                   CHAR(1) NOT NULL,
    factura_codigo           NUMBER(11) NOT NULL,
    miembrogrupo_usuario_id  NUMBER(11) NOT NULL,
    miembrogrupo_grupo_id    NUMBER(11) NOT NULL,
    miembrogrupo_usuario_id2 NUMBER(11) NOT NULL,
    miembrogrupo_grupo_id2   NUMBER(11) NOT NULL
);

ALTER TABLE deuda
    ADD CONSTRAINT deuda_pk PRIMARY KEY ( codigo_deuda,
                                          factura_codigo,
                                          miembrogrupo_usuario_id,
                                          miembrogrupo_grupo_id,
                                          miembrogrupo_usuario_id2,
                                          miembrogrupo_grupo_id2 );

CREATE TABLE factura (
    codigo      NUMBER(11) NOT NULL,
    descripcion NVARCHAR2(200) NOT NULL,
    metodo_pago NVARCHAR2(200),
    estado      CHAR(1) NOT NULL,
    monto       NUMBER(15, 3) NOT NULL,
    fecha       DATE NOT NULL,
    foto        CLOB,
    ubicacion   NVARCHAR2(200) NOT NULL
);

ALTER TABLE factura ADD CONSTRAINT factura_pk PRIMARY KEY ( codigo );

CREATE TABLE grupo (
    id             NUMBER(11) NOT NULL,
    nombre         NVARCHAR2(200) NOT NULL,
    estado         CHAR(1) NOT NULL,
    fecha_creacion DATE NOT NULL
);

ALTER TABLE grupo ADD CONSTRAINT grupo_pk PRIMARY KEY ( id );

ALTER TABLE grupo ADD CONSTRAINT grupo_nombre_un UNIQUE ( nombre );

CREATE TABLE miembrogrupo (
    rol        NVARCHAR2(200) NOT NULL,
    estado     CHAR(1) NOT NULL,
    usuario_id NUMBER(11) NOT NULL,
    grupo_id   NUMBER(11) NOT NULL
);

ALTER TABLE miembrogrupo ADD CONSTRAINT miembrogrupo_pk PRIMARY KEY ( usuario_id,
                                                                      grupo_id );

CREATE TABLE notificacion (
    codigo                     NUMBER(11) NOT NULL,
    mensaje                    NVARCHAR2(200) NOT NULL,
    fecha                      DATE NOT NULL,
    usuario_id                 NUMBER(11),
    grupo_id                   NUMBER(11),
    factura_codigo             NUMBER(11),
    transaccion_codigo         NUMBER(11),
    transaccion_codigo_deuda   NUMBER(11),
    transaccion_codigo_factura NUMBER(11),
    transaccion_usuario_id     NUMBER(11),
    transaccion_grupo_id       NUMBER(11),
    transaccion_usuario_id1    NUMBER(11),
    transaccion_grupo_id1      NUMBER(11)
);

ALTER TABLE notificacion ADD CONSTRAINT notificacion_pk PRIMARY KEY ( codigo );

CREATE TABLE preferencia (
    codigo_preferencia NUMBER(11) NOT NULL,
    p_notificacion     NVARCHAR2(200) NOT NULL,
    p_interface        NVARCHAR2(200) NOT NULL,
    p_paypal           NVARCHAR2(200) NOT NULL,
    p_gps              NVARCHAR2(200) NOT NULL,
    usuario_id         NUMBER(11) NOT NULL
);

CREATE UNIQUE INDEX preferencia__idx ON
    preferencia (
        usuario_id
    ASC );

ALTER TABLE preferencia ADD CONSTRAINT preferencia_pk PRIMARY KEY ( codigo_preferencia,
                                                                    usuario_id );

CREATE TABLE transaccion (
    codigo             NUMBER(11) NOT NULL,

    monto              NUMBER(15, 3) NOT NULL,
    estado             CHAR(1) NOT NULL,
    fecha              DATE NOT NULL,
    deuda_codigo_deuda NUMBER(11) NOT NULL,
    deuda_codigo       NUMBER(11) NOT NULL,
    deuda_usuario_id   NUMBER(11) NOT NULL,
    deuda_grupo_id     NUMBER(11) NOT NULL,
    deuda_usuario_id1  NUMBER(11) NOT NULL,
    deuda_grupo_id1    NUMBER(11) NOT NULL
);

ALTER TABLE transaccion
    ADD CONSTRAINT transaccion_pk PRIMARY KEY ( codigo,
                                                deuda_codigo_deuda,
                                                deuda_codigo,
                                                deuda_usuario_id,
                                                deuda_grupo_id,
                                                deuda_usuario_id1,
                                                deuda_grupo_id1 );

CREATE TABLE usuario (
    id              NUMBER(11) NOT NULL,
    nombre          NVARCHAR2(200) NOT NULL,
    balance         NUMBER(15, 3) NOT NULL,
    display_nombre  NVARCHAR2(200) NOT NULL,
    email           NVARCHAR2(200) NOT NULL,
    telefono        NUMBER(11) NOT NULL,
    contrasenia     NVARCHAR2(8) NOT NULL,
    estado          CHAR(1) NOT NULL,
    paypal_username NVARCHAR2(200)
);

ALTER TABLE usuario ADD CONSTRAINT usuario_pk PRIMARY KEY ( id );

ALTER TABLE deuda
    ADD CONSTRAINT deuda_factura_fk FOREIGN KEY ( factura_codigo )
        REFERENCES factura ( codigo );

ALTER TABLE deuda
    ADD CONSTRAINT deuda_miembrogrupo_fk FOREIGN KEY ( miembrogrupo_usuario_id,
                                                       miembrogrupo_grupo_id )
        REFERENCES miembrogrupo ( usuario_id,
                                  grupo_id );

ALTER TABLE deuda
    ADD CONSTRAINT deuda_miembrogrupo_fkv2 FOREIGN KEY ( miembrogrupo_usuario_id2,
                                                         miembrogrupo_grupo_id2 )
        REFERENCES miembrogrupo ( usuario_id,
                                  grupo_id );

ALTER TABLE miembrogrupo
    ADD CONSTRAINT miembrogrupo_grupo_fk FOREIGN KEY ( grupo_id )
        REFERENCES grupo ( id );

ALTER TABLE miembrogrupo
    ADD CONSTRAINT miembrogrupo_usuario_fk FOREIGN KEY ( usuario_id )
        REFERENCES usuario ( id );

ALTER TABLE notificacion
    ADD CONSTRAINT notificacion_factura_fk FOREIGN KEY ( factura_codigo )
        REFERENCES factura ( codigo );

ALTER TABLE notificacion
    ADD CONSTRAINT notificacion_grupo_fk FOREIGN KEY ( grupo_id )
        REFERENCES grupo ( id );

ALTER TABLE notificacion
    ADD CONSTRAINT notificacion_transaccion_fk FOREIGN KEY ( transaccion_codigo,
                                                             transaccion_codigo_deuda,
                                                             transaccion_codigo_factura,
                                                             transaccion_usuario_id,
                                                             transaccion_grupo_id,
                                                             transaccion_usuario_id1,
                                                             transaccion_grupo_id1 )
        REFERENCES transaccion ( codigo,
                                 deuda_codigo_deuda,
                                 deuda_codigo,
                                 deuda_usuario_id,
                                 deuda_grupo_id,
                                 deuda_usuario_id1,
                                 deuda_grupo_id1 );

ALTER TABLE notificacion
    ADD CONSTRAINT notificacion_usuario_fk FOREIGN KEY ( usuario_id )
        REFERENCES usuario ( id );

ALTER TABLE preferencia
    ADD CONSTRAINT preferencia_usuario_fk FOREIGN KEY ( usuario_id )
        REFERENCES usuario ( id );

ALTER TABLE transaccion
    ADD CONSTRAINT transaccion_deuda_fk FOREIGN KEY ( deuda_codigo_deuda,
                                                      deuda_codigo,
                                                      deuda_usuario_id,
                                                      deuda_grupo_id,
                                                      deuda_usuario_id1,
                                                      deuda_grupo_id1 )
        REFERENCES deuda ( codigo_deuda,
                           factura_codigo,
                           miembrogrupo_usuario_id,
                           miembrogrupo_grupo_id,
                           miembrogrupo_usuario_id2,
                           miembrogrupo_grupo_id2 );



-- Informe de Resumen de Oracle SQL Developer Data Modeler: 
-- 
-- CREATE TABLE                             8
-- CREATE INDEX                             1
-- ALTER TABLE                             20
-- CREATE VIEW                              0
-- ALTER VIEW                               0
-- CREATE PACKAGE                           0
-- CREATE PACKAGE BODY                      0
-- CREATE PROCEDURE                         0
-- CREATE FUNCTION                          0
-- CREATE TRIGGER                           0
-- ALTER TRIGGER                            0
-- CREATE COLLECTION TYPE                   0
-- CREATE STRUCTURED TYPE                   0
-- CREATE STRUCTURED TYPE BODY              0
-- CREATE CLUSTER                           0
-- CREATE CONTEXT                           0
-- CREATE DATABASE                          0
-- CREATE DIMENSION                         0
-- CREATE DIRECTORY                         0
-- CREATE DISK GROUP                        0
-- CREATE ROLE                              0
-- CREATE ROLLBACK SEGMENT                  0
-- CREATE SEQUENCE                          0
-- CREATE MATERIALIZED VIEW                 0
-- CREATE MATERIALIZED VIEW LOG             0
-- CREATE SYNONYM                           0
-- CREATE TABLESPACE                        0
-- CREATE USER                              0
-- 
-- DROP TABLESPACE                          0
-- DROP DATABASE                            0
-- 
-- REDACTION POLICY                         0
-- 
-- ORDS DROP SCHEMA                         0
-- ORDS ENABLE SCHEMA                       0
-- ORDS ENABLE OBJECT                       0
-- 
-- ERRORS                                   0
-- WARNINGS                                 0

commit;

-- Inserts de las Tablas

-- TABLA usuario

--(id, nombre, balance, display_nombre, email, telefono, contrasenia, estado, paypal_username)

INSERT INTO usuario VALUES (1, 'Juan Perez', 500.00, 'Juan', 'juan.perez@example.com', 1234567890, 'pass1234', 'A', 'juan.paypal');

INSERT INTO usuario VALUES (2, 'Maria Gomez', 800.00, 'Maria', 'maria.gomez@example.com', 9876543210, 'pass5678', 'A', 'maria.paypal');

INSERT INTO usuario VALUES (3, 'Carlos Lopez', 300.00, 'Carlos', 'carlos.lopez@example.com', 1122334455, 'pass4321', 'A', 'carlos.paypal');

-- TABLA grupo

--(id, nombre, estado, fecha_creacion)

INSERT INTO grupo VALUES (1, 'Grupo A', 'A', TO_DATE('2023-01-01', 'YYYY-MM-DD'));

INSERT INTO grupo VALUES (2, 'Grupo B', 'A', TO_DATE('2023-02-01', 'YYYY-MM-DD'));

INSERT INTO grupo VALUES (3, 'Grupo C', 'A', TO_DATE('2023-03-01', 'YYYY-MM-DD'));

-- TABLA miembrogrupo (Intermedia entre Usuario y Grupo)

-- (rol, estado, usuario_id, grupo_id)

INSERT INTO miembrogrupo VALUES ('Líder', 'A', 1, 1);

INSERT INTO miembrogrupo VALUES ('Miembro', 'A', 2, 1);

INSERT INTO miembrogrupo VALUES ('Líder', 'A', 2, 2);

INSERT INTO miembrogrupo VALUES ('Miembro', 'A', 3, 2);

INSERT INTO miembrogrupo VALUES ('Miembro', 'A', 1, 3);

INSERT INTO miembrogrupo VALUES ('Miembro', 'A', 3, 1);

INSERT INTO miembrogrupo VALUES ('Lider', 'A', 3, 3);

-- Tabla factura

--(codigo, descripcion, estado, monto, fecha, ubicacion)

INSERT INTO factura (codigo, descripcion, estado, monto, fecha, ubicacion) VALUES (1001, 'Compra de alimentos', 'A', 300.00, TO_DATE('2023-03-10', 'YYYY-MM-DD'), 'Supermercado A');

INSERT INTO factura (codigo, descripcion, estado, monto, fecha, ubicacion) VALUES (1002, 'Compra de electrónicos', 'P', 1200.00, TO_DATE('2023-04-15', 'YYYY-MM-DD'), 'Tienda B');

INSERT INTO factura (codigo, descripcion, estado, monto, fecha, ubicacion) VALUES (1003, 'Pago de servicios públicos', 'A', 150.00, TO_DATE('2023-05-20', 'YYYY-MM-DD'), 'Ciudad C');

-- Tabla deuda

--(codigo_deuda, monto, fecha_registro, estado, factura_codigo, miembrogrupo_usuario_id, miembrogrupo_grupo_id, miembrogrupo_usuario_id2, miembrogrupo_grupo_id2)

INSERT INTO deuda VALUES (2001, 150.00, TO_DATE('2023-03-15', 'YYYY-MM-DD'), 'N', 'P', 1001, 1, 1, 2, 1);

INSERT INTO deuda VALUES (2002, 200.00, TO_DATE('2023-04-20', 'YYYY-MM-DD'), 'N', 'P', 1002, 3, 2, 2, 2);

INSERT INTO deuda VALUES (2003, 100.00, TO_DATE('2023-05-25', 'YYYY-MM-DD'), 'N', 'P', 1003, 1, 3, 3, 3);

INSERT INTO deuda VALUES (2004, 180.00, TO_DATE('2023-06-01', 'YYYY-MM-DD'), 'N', 'A', 1001, 3, 1, 2, 1);

INSERT INTO deuda VALUES (2005, 250.00, TO_DATE('2023-06-15', 'YYYY-MM-DD'), 'N', 'A', 1003, 1, 3, 3, 3);


-- Tabla transaccion

--(codigo, monto, fecha, deuda_codigo_deuda, deuda_codigo, deuda_usuario_id, deuda_grupo_id, deuda_usuario_id1, deuda_grupo_id1)

INSERT INTO transaccion VALUES (3001, 150.00, 'A', TO_DATE('2023-03-16', 'YYYY-MM-DD'), 2001, 1001, 1, 1, 2, 1);

INSERT INTO transaccion VALUES (3002, 200.00, 'A', TO_DATE('2023-04-21', 'YYYY-MM-DD'), 2002, 1002, 3, 2, 2, 2);

INSERT INTO transaccion VALUES (3003, 100.00, 'A', TO_DATE('2023-05-26', 'YYYY-MM-DD'), 2003, 1003, 1, 3, 3, 3);

-- Tabla Notificacion

-- (codigo, mensaje, fecha, usuario_id, grupo_id, factura_codigo, transaccion_codigo, transaccion_codigo_deuda, transaccion_codigo_factura, transaccion_usuario_id, transaccion_grupo_id, transaccion_usuario_id1, transaccion_grupo_id1)

INSERT INTO notificacion VALUES (4004, 'Nueva factura creada: Compra de alimentos', TO_DATE('2023-06-01', 'YYYY-MM-DD'), 1, 1, 1001, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO notificacion VALUES (4005, 'Nueva deuda creada entre Carlos Lopez y Maria Gomez en el Grupo A', TO_DATE('2023-06-01', 'YYYY-MM-DD'), 2, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO notificacion VALUES (4008, 'Has completado el pago de $200.00 a Carlos Lopez en la deuda 2002', TO_DATE('2023-06-20', 'YYYY-MM-DD'), 2, 2, 1002, 3002, 2002, 1002, 3, 2, 2, 2);

INSERT INTO notificacion VALUES (4006, 'Te has unido al Grupo C', TO_DATE('2023-06-15', 'YYYY-MM-DD'), 3, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO notificacion VALUES (4007, 'Has completado el pago de $150.00 a Juan Perez en la deuda 2001', TO_DATE('2023-06-05', 'YYYY-MM-DD'), 2, 1, 1001, 3001, 2001, 1001, 1, 1, 2, 1);

-- Tabla Preferencia

--(codigo_preferencia, p_notificacion, p_interface, p_paypal, p_gps, usuario_id)

INSERT INTO preferencia VALUES (1, 'Notificaciones por email', 'Oscuro', 'Activado', 'Desactivado', 1);

INSERT INTO preferencia VALUES (2, 'Notificaciones por SMS', 'Claro', 'Desactivado', 'Activado', 2);

INSERT INTO preferencia VALUES (3, 'Sin notificaciones', 'Oscuro', 'Activado', 'Activado', 3);

commit;

-- Consultas

--1. E-mails and/or notifications are sent out containing the following information:
-- The group’s name, members, and the user’s overall balance in that group
--  A list of group members who th euser owes and how much he/she owes to each person
-- A list of group members who owe the user and how much each person owes him/her

WITH user_balance AS (
    SELECT ug.usuario_id,
           ug.grupo_id,
           SUM(CASE WHEN d.miembrogrupo_usuario_id = ug.usuario_id THEN d.monto ELSE 0 END) - 
           SUM(CASE WHEN d.miembrogrupo_usuario_id2 = ug.usuario_id THEN d.monto ELSE 0 END) AS balance
    FROM miembrogrupo ug
    LEFT JOIN deuda d ON d.miembrogrupo_usuario_id = ug.usuario_id OR d.miembrogrupo_usuario_id2 = ug.usuario_id
    GROUP BY ug.usuario_id, ug.grupo_id
)
SELECT g.nombre AS grupo,
       u.display_nombre AS miembro,
       ub.balance,
       -- Subconsulta para los usuarios a quienes el usuario le debe dinero
       (SELECT LISTAGG(u2.display_nombre || ' ($' || d2.monto || ')', ', ') WITHIN GROUP (ORDER BY u2.display_nombre)
        FROM deuda d2
        JOIN usuario u2 ON u2.id = d2.miembrogrupo_usuario_id2
        WHERE d2.miembrogrupo_usuario_id = u.id
          AND d2.miembrogrupo_grupo_id = g.id
       ) AS debo_a,
       -- Subconsulta para los usuarios que le deben al usuario
       (SELECT LISTAGG(u3.display_nombre || ' ($' || d3.monto || ')', ', ') WITHIN GROUP (ORDER BY u3.display_nombre)
        FROM deuda d3
        JOIN usuario u3 ON u3.id = d3.miembrogrupo_usuario_id
        WHERE d3.miembrogrupo_usuario_id2 = u.id
          AND d3.miembrogrupo_grupo_id2 = g.id
       ) AS me_deben
FROM grupo g
JOIN miembrogrupo ug ON g.id = ug.grupo_id
JOIN usuario u ON u.id = ug.usuario_id
LEFT JOIN user_balance ub ON ub.usuario_id = ug.usuario_id AND ub.grupo_id = ug.grupo_id
WHERE g.id = 1 
ORDER BY u.display_nombre;

commit;

--2. The user is presented with a list of all transactions/bills posted to the current group. The user may sort these items by  date, amount, payer, etc.

SELECT unique t.codigo AS transaccion_codigo,
       t.monto AS monto,
       t.fecha AS fecha_transaccion,
       u.display_nombre AS pagador,
       f.descripcion AS factura,
       f.monto AS monto_factura,
       f.fecha AS fecha_factura
FROM transaccion t
JOIN deuda d ON d.codigo_deuda = t.deuda_codigo_deuda
JOIN factura f ON f.codigo = d.factura_codigo
JOIN miembrogrupo mg ON mg.grupo_id = d.miembrogrupo_grupo_id
JOIN usuario u ON u.id = t.deuda_usuario_id
ORDER BY t.fecha;

commit;

--3. The user can see his/her status in each of the groups he/she is a member of

SELECT g.nombre AS grupo,
       u.nombre as nombre,
       ug.rol AS rol,
       ug.estado AS estado_miembro
FROM grupo g
JOIN miembrogrupo ug ON g.id = ug.grupo_id
JOIN usuario u ON u.id = ug.usuario_id; 

commit;

/*4. The server pushes notifications to all members' phones. The pop-up notification will contain information about the new bill, similar to the following example:
-- New Bill: $50 gas
-- Paid by: Blake
-- Group: 'Trip to DC'
-- Your balance: $10 */

SELECT 
    f.monto AS "New Bill",
    u.display_nombre AS "Paid by",
    g.nombre AS "Group",
    u.balance AS "Your balance"
FROM 
    notificacion n
JOIN 
    factura f ON n.factura_codigo = f.codigo
LEFT JOIN 
    transaccion t ON n.transaccion_codigo = t.codigo
JOIN 
    grupo g ON n.grupo_id = g.id
JOIN 
    usuario u ON u.id = n.usuario_id
WHERE 
    n.factura_codigo IS NOT NULL
ORDER BY 
    n.fecha DESC;

commit;

-- 5. Prompts the user to enter an amount to pay the selected individual
SELECT 
    deudor.display_nombre AS deudor,
    acreedor.display_nombre AS acreedor,
    t.monto,
    'PayPal' AS metodo_pago,
    t.fecha
FROM 
    transaccion t
JOIN 
    usuario deudor ON t.deuda_usuario_id = deudor.id
JOIN 
    usuario acreedor ON t.deuda_usuario_id1 = acreedor.id
ORDER BY 
    t.fecha DESC;

commit;