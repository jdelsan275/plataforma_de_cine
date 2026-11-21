
CREATE TABLE IF NOT EXISTS encargado
(
    id_encargado BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL,
    primer_apellido VARCHAR(30) NOT NULL,
    segundo_apellido VARCHAR(30)
    );

CREATE TABLE IF NOT EXISTS proyector
(
    id_proyector BIGINT AUTO_INCREMENT PRIMARY KEY,
    modelo VARCHAR(30) NOT NULL,
    marca VARCHAR(30) NOT NULL
    );

CREATE TABLE IF NOT EXISTS sala
(
    id_sala BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL,
    capacidad INT NOT NULL,
    tiene_3D BOOLEAN NOT NULL,
    id_encargado BIGINT NOT NULL UNIQUE,
    id_proyector BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (id_encargado) REFERENCES encargado(id_encargado),
    FOREIGN KEY (id_proyector) REFERENCES proyector(id_proyector)
    );

CREATE TABLE IF NOT EXISTS pelicula
(
    id_pelicula BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(30) NOT NULL,
    sinopsis VARCHAR(300) NOT NULL,
    fecha_estreno DATE NOT NULL,
    mayorEdad BOOLEAN NOT NULL
    );

CREATE TABLE IF NOT EXISTS actor
(
    id_actor BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL,
    primerApellido VARCHAR(50) NOT NULL,
    segundoApellido VARCHAR(50),
    fecha_nacimiento DATE NOT NULL,
    nacionalidad VARCHAR(30) NOT NULL
    );

CREATE TABLE IF NOT EXISTS sesion
(
    id_sesion BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_sesion DATE NOT NULL,
    hora TIME NOT NULL,
    precio DECIMAL(6,2) NOT NULL,
    sesion_AM BOOLEAN NOT NULL,
    id_pelicula BIGINT NOT NULL,
    id_sala BIGINT NOT NULL,
    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula),
    FOREIGN KEY (id_sala) REFERENCES sala(id_sala)
    );

CREATE TABLE IF NOT EXISTS pelicula_actor
(
    id_pelicula BIGINT NOT NULL,
    id_actor BIGINT NOT NULL,
    PRIMARY KEY (id_pelicula, id_actor),
    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula),
    FOREIGN KEY (id_actor)
    REFERENCES actor(id_actor)
    );
