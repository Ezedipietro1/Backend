DROP TABLE IF EXISTS empleado;
DROP TABLE IF EXISTS departamento;
DROP TABLE IF EXISTS puesto;

CREATE TABLE departamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE puesto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE empleado (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    edad INT NOT NULL,
    fecha_ingreso DATE NOT NULL,
    salario DECIMAL(10,2) NOT NULL,
    empleado_fijo BOOLEAN NOT NULL,
    departamento_id INT,
    puesto_id INT,
    CONSTRAINT fk_departamento FOREIGN KEY (departamento_id) REFERENCES departamento(id),
    CONSTRAINT fk_puesto FOREIGN KEY (puesto_id) REFERENCES puesto(id)
);
