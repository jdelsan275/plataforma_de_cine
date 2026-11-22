INSERT IGNORE INTO encargado (id_encargado, nombre, primer_apellido, segundo_apellido)
VALUES (1, 'Carlos', 'Ruiz', 'Molina'),
       (2, 'Lucía', 'Fernández', 'Soto'),
       (3, 'Miguel', 'Torres', 'Navarro'),
       (4, 'Elena', 'Jiménez', 'Ortiz');

INSERT IGNORE INTO proyector (id_proyector, modelo, marca)
VALUES (1, 'CP2220', 'Barco'),
       (2, 'NC-M303L', 'NEC'),
       (3, 'EB-L1075U', 'Epson'),
       (4, 'DLP-4K', 'Christie');

INSERT IGNORE INTO sala (id_sala, numero, capacidad, tiene_3d, id_encargado, id_proyector)
VALUES (1, 1, 120, TRUE, 1, 1),
       (2, 2, 90, FALSE, 2, 2),
       (3, 3, 150, TRUE, 3, 3),
       (4, 4, 60, FALSE, 4, 4);

INSERT IGNORE INTO pelicula (id_pelicula, titulo, sinopsis, fecha_estreno, mayor_edad)
VALUES (1, 'Origen',
        'Un ladrón especializado en robar secretos a través de los sueños recibe el encargo de implantar una idea en la mente de un empresario.',
        '2010-07-16', FALSE),
       (2, 'El Padrino',
        'La historia de la familia Corleone y de cómo el hijo menor acaba asumiendo el control del negocio criminal de su padre.',
        '1972-03-24', TRUE),
       (3, 'Titanic',
        'Un joven artista y una chica de clase alta se enamoran a bordo del transatlántico que se hundirá en su viaje inaugural.',
        '1997-12-19', FALSE),
       (4, 'Pulp Fiction',
        'Varias historias de criminales de Los Ángeles se cruzan entre sí con mucho humor negro y violencia.',
        '1994-10-14', TRUE),
       (5, 'Toy Story',
        'Un vaquero de juguete ve amenazado su puesto de favorito cuando llega un nuevo muñeco astronauta a la habitación de su dueño.',
        '1995-11-22', FALSE);

INSERT IGNORE INTO actor (id_actor, nombre, primer_apellido, segundo_apellido, fecha_nacimiento, nacionalidad)
VALUES (1, 'Leonardo', 'DiCaprio', NULL, '1974-11-11', 'Estadounidense'),
       (2, 'Joseph', 'Gordon-Levitt', NULL, '1981-02-17', 'Estadounidense'),
       (3, 'Kate', 'Winslet', NULL, '1975-10-05', 'Británica'),
       (4, 'Al', 'Pacino', NULL, '1940-04-25', 'Estadounidense'),
       (5, 'Marlon', 'Brando', NULL, '1924-04-03', 'Estadounidense'),
       (6, 'John', 'Travolta', NULL, '1954-02-18', 'Estadounidense'),
       (7, 'Uma', 'Thurman', NULL, '1970-04-29', 'Estadounidense'),
       (8, 'Tom', 'Hanks', NULL, '1956-07-09', 'Estadounidense');

INSERT IGNORE INTO pelicula_actor (id_pelicula, id_actor)
VALUES (1, 1),
       (1, 2),
       (2, 4),
       (2, 5),
       (3, 1),
       (3, 3),
       (4, 6),
       (4, 7),
       (5, 8);

INSERT IGNORE INTO sesion (id_sesion, fecha_sesion, hora, precio, sesion_am, id_pelicula, id_sala)
VALUES (1, '2026-11-20', '11:30:00', 6.50, TRUE, 5, 2),
       (2, '2026-11-20', '16:00:00', 8.00, FALSE, 1, 1),
       (3, '2026-11-20', '18:30:00', 8.50, FALSE, 3, 3),
       (4, '2026-11-20', '21:00:00', 9.00, FALSE, 4, 4),
       (5, '2026-11-21', '12:00:00', 6.50, FALSE, 5, 2),
       (6, '2026-11-21', '17:30:00', 8.00, FALSE, 2, 3),
       (7, '2026-11-21', '20:00:00', 9.00, FALSE, 1, 1),
       (8, '2026-11-21', '22:30:00', 9.00, FALSE, 4, 4);