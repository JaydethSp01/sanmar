-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 13-12-2024 a las 06:35:30
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `videojuegodb`
--

DELIMITER $$
--
-- Procedimientos
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `actualizar_partida` (IN `id_jugador_ganador` INT, IN `id_jugador_perdedor` INT, IN `puntuacion_ganador` INT, IN `puntuacion_perdedor` INT, IN `resultado_partida` VARCHAR(255))   BEGIN
    DECLARE mensaje VARCHAR(255);
    DECLARE nuevo_nivel INT;

    -- Actualizar la puntuación del jugador ganador y verificar si sube de nivel
    UPDATE jugadores
    SET puntuacion = puntuacion + puntuacion_ganador
    WHERE id = id_jugador_ganador;

    -- Verificar si el jugador ha subido de nivel (por ejemplo, si su puntuación supera un umbral)
    SET nuevo_nivel = (SELECT FLOOR(puntuacion / 100) + 1 FROM jugadores WHERE id = id_jugador_ganador);
    UPDATE jugadores
    SET nivel = nuevo_nivel
    WHERE id = id_jugador_ganador;

    -- Insertar o actualizar ranking para el jugador ganador
    INSERT INTO ranking (id_jugador, puntuacion, resultado)
    VALUES (id_jugador_ganador, puntuacion_ganador, resultado_partida)
    ON DUPLICATE KEY UPDATE
        puntuacion = puntuacion + puntuacion_ganador;

    -- Insertar o mantener ranking para el jugador perdedor (sin cambio en puntuación)
    INSERT INTO ranking (id_jugador, puntuacion, resultado)
    VALUES (id_jugador_perdedor, puntuacion_perdedor, resultado_partida)
    ON DUPLICATE KEY UPDATE
        puntuacion = puntuacion_perdedor;

    -- Actualizar las posiciones de los jugadores en el ranking según la puntuación
    SET @rank = 0;
    UPDATE ranking
    SET posicion = (@rank := @rank + 1)
    ORDER BY puntuacion DESC;

    -- Generar mensaje de consola para el ganador
    SET mensaje = CONCAT('Jugador ', id_jugador_ganador, ' ha ganado. Tu puntuación es ', puntuacion_ganador, '. ¡Subiste a nivel ', nuevo_nivel, '!');
    SELECT mensaje AS resultado;

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ObtenerTop10Jugadores` ()   BEGIN
    SELECT 
        id, 
        nombre, 
        nivel, 
        puntuacion 
    FROM 
        jugadores
	WHERE puntuacion>100
    ORDER BY 
        puntuacion DESC
    LIMIT 10;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `registrarResultadoYActualizarRanking` (IN `idGanador` INT, IN `idPerdedor` INT, IN `puntosGanador` INT, IN `puntosPerdedor` INT)   BEGIN

    DECLARE nuevo_nivel INT;
    DECLARE mensaje varchar(500);

    -- Actualizar la puntuación del jugador ganador y verificar si sube de nivel
    UPDATE jugadores
    SET puntuacion = puntuacion + puntosGanador
    WHERE id = idGanador;

    -- Verificar si el jugador ha subido de nivel (por ejemplo, si su puntuación supera un umbral)
    SET nuevo_nivel = (SELECT FLOOR(puntuacion / 100) + 1 FROM jugadores WHERE id = idGanador);
    UPDATE jugadores
    SET nivel = nuevo_nivel
    WHERE id = idGanador;

    -- Insertar o actualizar el ranking para el jugador ganador
    INSERT INTO ranking (id_jugador, puntuacion)
    VALUES (idGanador, puntosGanador)
    ON DUPLICATE KEY UPDATE
        puntuacion = puntuacion + puntosGanador;

    -- Insertar o actualizar el ranking para el jugador perdedor
    INSERT INTO ranking (id_jugador, puntuacion)
    VALUES (idPerdedor, puntosPerdedor)
    ON DUPLICATE KEY UPDATE
        puntuacion = puntosPerdedor;

    -- Actualizar las posiciones de los jugadores en el ranking según la puntuación
    SET @rank = 0;
    UPDATE ranking
    SET posicion = (@rank := @rank + 1)
    ORDER BY puntuacion DESC;
 
    SET mensaje = CONCAT('Jugador ', idGanador, ' ha ganado. Tu puntuación es ', puntosGanador, '. ¡Subiste a nivel ', nuevo_nivel, '!');
    SELECT mensaje AS resultado;

END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `inventario`
--

CREATE TABLE `inventario` (
  `jugador_id` int(11) NOT NULL,
  `cantidad` int(11) DEFAULT NULL,
  `nombre_articulo` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `inventario`
--

INSERT INTO `inventario` (`jugador_id`, `cantidad`, `nombre_articulo`) VALUES
(5, 1, 'espada de fuego');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `jugadores`
--

CREATE TABLE `jugadores` (
  `id` int(11) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `nivel` int(11) NOT NULL DEFAULT 1,
  `puntuacion` int(11) NOT NULL DEFAULT 0,
  `inventario` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`inventario`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `jugadores`
--

INSERT INTO `jugadores` (`id`, `nombre`, `nivel`, `puntuacion`, `inventario`) VALUES
(2, 'JUANAA', 61, 6036, NULL),
(3, 'juan', 1, 5, NULL),
(4, 'lopo', 2, 150, NULL),
(5, 'MESSI', 10, 900, NULL),
(6, 'ROBERTO', 1, 0, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mundos`
--

CREATE TABLE `mundos` (
  `id` int(11) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `grafo_serializado` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `mundos`
--

INSERT INTO `mundos` (`id`, `nombre`, `grafo_serializado`) VALUES
(1, 'Test Mundo', '{\"nodes\": [\"Nodo1\", \"Nodo2\"], \"edges\": [{\"from\": \"Nodo1\", \"to\": \"Nodo2\"}]}'),
(2, 'op', '{\"nodos\":[\"A\",\"B\"],\"aristas\":[{\"peso\":12.0,\"origen\":\"A\",\"destino\":\"B\"},{\"peso\":10.0,\"origen\":\"A\",\"destino\":\"A\"},{\"peso\":10.0,\"origen\":\"B\",\"destino\":\"A\"}]}'),
(3, 'WARZON', '{\"nodos\":[\"A\",\"B\"],\"aristas\":[{\"peso\":2.0,\"origen\":\"A\",\"destino\":\"B\"},{\"peso\":4.0,\"origen\":\"A\",\"destino\":\"A\"},{\"peso\":10.0,\"origen\":\"B\",\"destino\":\"A\"}]}'),
(4, 'PORWADF', '{\"nodos\":[\"A\",\"B\",\"C\",\"D\",\"E\"],\"aristas\":[{\"peso\":1.0,\"origen\":\"A\",\"destino\":\"A\"},{\"peso\":45.0,\"origen\":\"A\",\"destino\":\"D\"},{\"peso\":23.0,\"origen\":\"A\",\"destino\":\"C\"},{\"peso\":5555.0,\"origen\":\"B\",\"destino\":\"D\"},{\"peso\":12.0,\"origen\":\"B\",\"destino\":\"E\"}]}');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `partidas`
--

CREATE TABLE `partidas` (
  `id` int(11) NOT NULL,
  `fecha` date DEFAULT NULL,
  `equipo1_idjugador` int(11) NOT NULL,
  `equipo2_idjugador` int(11) NOT NULL,
  `resultado` longtext DEFAULT NULL,
  `equipo1_nombre` varchar(255) DEFAULT NULL,
  `equipo2_nombre` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `partidas`
--

INSERT INTO `partidas` (`id`, `fecha`, `equipo1_idjugador`, `equipo2_idjugador`, `resultado`, `equipo1_nombre`, `equipo2_nombre`) VALUES
(2, '2024-12-12', 2, 5, 'Simulación de partida:\nJugador hhd ataca con 12. Vida restante de ffdg: 88\nJugador ffdg ataca con 14. Vida restante de hhd: 86\nJugador hhd ataca con 19. Vida restante de ffdg: 69\nJugador ffdg ataca con 6. Vida restante de hhd: 80\nJugador hhd ataca con 18. Vida restante de ffdg: 51\nJugador ffdg ataca con 6. Vida restante de hhd: 74\nJugador hhd ataca con 20. Vida restante de ffdg: 31\nJugador ffdg ataca con 15. Vida restante de hhd: 59\nJugador hhd ataca con 17. Vida restante de ffdg: 14\nJugador ffdg ataca con 6. Vida restante de hhd: 53\nJugador hhd ataca con 23. Vida restante de ffdg: 0\nJugador ffdg ataca con 10. Vida restante de hhd: 43\n¡Ganador: hhd!\n', 'hhd', 'ffdg'),
(3, '2024-12-12', 2, 5, 'Simulación de partida:\nJugador poo ataca con 10. Vida restante de saramelo: 90\nJugador saramelo ataca con 9. Vida restante de poo: 91\nJugador poo ataca con 20. Vida restante de saramelo: 70\nJugador saramelo ataca con 19. Vida restante de poo: 72\nJugador poo ataca con 22. Vida restante de saramelo: 48\nJugador saramelo ataca con 12. Vida restante de poo: 60\nJugador poo ataca con 17. Vida restante de saramelo: 31\nJugador saramelo ataca con 12. Vida restante de poo: 48\nJugador poo ataca con 13. Vida restante de saramelo: 18\nJugador saramelo ataca con 20. Vida restante de poo: 28\nJugador poo ataca con 6. Vida restante de saramelo: 12\nJugador saramelo ataca con 22. Vida restante de poo: 6\nJugador poo ataca con 6. Vida restante de saramelo: 6\nJugador saramelo ataca con 13. Vida restante de poo: 0\n¡Ganador: saramelo!\n', 'poo', 'saramelo'),
(4, '2024-12-12', 3, 5, 'Simulación de partida:\nJugador pola ataca con 20. Vida restante de guaro: 80\nJugador guaro ataca con 7. Vida restante de pola: 93\nJugador pola ataca con 14. Vida restante de guaro: 66\nJugador guaro ataca con 23. Vida restante de pola: 70\nJugador pola ataca con 19. Vida restante de guaro: 47\nJugador guaro ataca con 5. Vida restante de pola: 65\nJugador pola ataca con 24. Vida restante de guaro: 23\nJugador guaro ataca con 15. Vida restante de pola: 50\nJugador pola ataca con 12. Vida restante de guaro: 11\nJugador guaro ataca con 20. Vida restante de pola: 30\nJugador pola ataca con 17. Vida restante de guaro: 0\nJugador guaro ataca con 22. Vida restante de pola: 8\n¡Ganador: pola!\n', 'pola', 'guaro'),
(5, '2024-12-12', 2, 5, 'Simulación de partida:\nJugador ron ataca con 5. Vida restante de whisky: 95\nJugador whisky ataca con 24. Vida restante de ron: 76\nJugador ron ataca con 7. Vida restante de whisky: 88\nJugador whisky ataca con 11. Vida restante de ron: 65\nJugador ron ataca con 5. Vida restante de whisky: 83\nJugador whisky ataca con 14. Vida restante de ron: 51\nJugador ron ataca con 14. Vida restante de whisky: 69\nJugador whisky ataca con 22. Vida restante de ron: 29\nJugador ron ataca con 10. Vida restante de whisky: 59\nJugador whisky ataca con 23. Vida restante de ron: 6\nJugador ron ataca con 9. Vida restante de whisky: 50\nJugador whisky ataca con 9. Vida restante de ron: 0\n¡Ganador: whisky!\n', 'ron', 'whisky'),
(6, '2024-12-12', 2, 5, 'Simulación de partida:\nJugador ron ataca con 12. Vida restante de whisky: 88\nJugador whisky ataca con 13. Vida restante de ron: 87\nJugador ron ataca con 12. Vida restante de whisky: 76\nJugador whisky ataca con 5. Vida restante de ron: 82\nJugador ron ataca con 23. Vida restante de whisky: 53\nJugador whisky ataca con 23. Vida restante de ron: 59\nJugador ron ataca con 8. Vida restante de whisky: 45\nJugador whisky ataca con 8. Vida restante de ron: 51\nJugador ron ataca con 22. Vida restante de whisky: 23\nJugador whisky ataca con 17. Vida restante de ron: 34\nJugador ron ataca con 18. Vida restante de whisky: 5\nJugador whisky ataca con 12. Vida restante de ron: 22\nJugador ron ataca con 6. Vida restante de whisky: 0\nJugador whisky ataca con 20. Vida restante de ron: 2\n¡Ganador: ron!\n', 'ron', 'whisky'),
(7, '2024-12-12', 2, 5, 'Simulación de partida:\nJugador ron ataca con 24. Vida restante de whisky: 76\nJugador whisky ataca con 6. Vida restante de ron: 94\nJugador ron ataca con 12. Vida restante de whisky: 64\nJugador whisky ataca con 23. Vida restante de ron: 71\nJugador ron ataca con 8. Vida restante de whisky: 56\nJugador whisky ataca con 18. Vida restante de ron: 53\nJugador ron ataca con 17. Vida restante de whisky: 39\nJugador whisky ataca con 24. Vida restante de ron: 29\nJugador ron ataca con 18. Vida restante de whisky: 21\nJugador whisky ataca con 6. Vida restante de ron: 23\nJugador ron ataca con 9. Vida restante de whisky: 12\nJugador whisky ataca con 23. Vida restante de ron: 0\n¡Ganador: whisky!\n', 'ron', 'whisky'),
(8, '2024-12-12', 3, 5, 'Simulación de partida:\nJugador ron ataca con 12. Vida restante de whisky: 88\nJugador whisky ataca con 17. Vida restante de ron: 83\nJugador ron ataca con 20. Vida restante de whisky: 68\nJugador whisky ataca con 21. Vida restante de ron: 62\nJugador ron ataca con 19. Vida restante de whisky: 49\nJugador whisky ataca con 17. Vida restante de ron: 45\nJugador ron ataca con 17. Vida restante de whisky: 32\nJugador whisky ataca con 21. Vida restante de ron: 24\nJugador ron ataca con 18. Vida restante de whisky: 14\nJugador whisky ataca con 20. Vida restante de ron: 4\nJugador ron ataca con 23. Vida restante de whisky: 0\nJugador whisky ataca con 20. Vida restante de ron: 0\n¡Ganador: whisky!\n', 'ron', 'whisky'),
(9, '2024-12-12', 3, 5, 'Simulación de partida:\nJugador pola ataca con 24. Vida restante de whisky: 76\nJugador whisky ataca con 10. Vida restante de pola: 90\nJugador pola ataca con 22. Vida restante de whisky: 54\nJugador whisky ataca con 12. Vida restante de pola: 78\nJugador pola ataca con 10. Vida restante de whisky: 44\nJugador whisky ataca con 17. Vida restante de pola: 61\nJugador pola ataca con 15. Vida restante de whisky: 29\nJugador whisky ataca con 13. Vida restante de pola: 48\nJugador pola ataca con 21. Vida restante de whisky: 8\nJugador whisky ataca con 7. Vida restante de pola: 41\nJugador pola ataca con 16. Vida restante de whisky: 0\nJugador whisky ataca con 19. Vida restante de pola: 22\n¡Ganador: pola!\n', 'pola', 'whisky'),
(10, '2024-12-12', 2, 5, 'Simulación de partida:\nJugador po ataca con 6. Vida restante de csww: 94\nJugador csww ataca con 22. Vida restante de po: 78\nJugador po ataca con 9. Vida restante de csww: 85\nJugador csww ataca con 17. Vida restante de po: 61\nJugador po ataca con 9. Vida restante de csww: 76\nJugador csww ataca con 14. Vida restante de po: 47\nJugador po ataca con 20. Vida restante de csww: 56\nJugador csww ataca con 24. Vida restante de po: 23\nJugador po ataca con 16. Vida restante de csww: 40\nJugador csww ataca con 13. Vida restante de po: 10\nJugador po ataca con 24. Vida restante de csww: 16\nJugador csww ataca con 6. Vida restante de po: 4\nJugador po ataca con 6. Vida restante de csww: 10\nJugador csww ataca con 11. Vida restante de po: 0\n¡Ganador: csww!\n', 'po', 'csww'),
(11, '2024-12-12', 2, 5, 'Simulación de partida:\nJugador ndsnhd ataca con 5. Vida restante de fff: 95\nJugador fff ataca con 10. Vida restante de ndsnhd: 90\nJugador ndsnhd ataca con 14. Vida restante de fff: 81\nJugador fff ataca con 7. Vida restante de ndsnhd: 83\nJugador ndsnhd ataca con 5. Vida restante de fff: 76\nJugador fff ataca con 22. Vida restante de ndsnhd: 61\nJugador ndsnhd ataca con 12. Vida restante de fff: 64\nJugador fff ataca con 20. Vida restante de ndsnhd: 41\nJugador ndsnhd ataca con 21. Vida restante de fff: 43\nJugador fff ataca con 11. Vida restante de ndsnhd: 30\nJugador ndsnhd ataca con 14. Vida restante de fff: 29\nJugador fff ataca con 19. Vida restante de ndsnhd: 11\nJugador ndsnhd ataca con 8. Vida restante de fff: 21\nJugador fff ataca con 13. Vida restante de ndsnhd: 0\n¡Ganador: fff!\n', 'ndsnhd', 'fff'),
(12, '2024-12-12', 2, 5, 'Simulación de partida:\nJugador bhghg ataca con 12. Vida restante de ygg: 88\nJugador ygg ataca con 23. Vida restante de bhghg: 77\nJugador bhghg ataca con 22. Vida restante de ygg: 66\nJugador ygg ataca con 6. Vida restante de bhghg: 71\nJugador bhghg ataca con 23. Vida restante de ygg: 43\nJugador ygg ataca con 19. Vida restante de bhghg: 52\nJugador bhghg ataca con 23. Vida restante de ygg: 20\nJugador ygg ataca con 8. Vida restante de bhghg: 44\nJugador bhghg ataca con 6. Vida restante de ygg: 14\nJugador ygg ataca con 8. Vida restante de bhghg: 36\nJugador bhghg ataca con 18. Vida restante de ygg: 0\nJugador ygg ataca con 14. Vida restante de bhghg: 22\n¡Ganador: bhghg!\n', 'bhghg', 'ygg'),
(13, '2024-12-12', 2, 5, 'Simulación de partida:\nJugador fff ataca con 9. Vida restante de fff: 91\nJugador fff ataca con 9. Vida restante de fff: 91\nJugador fff ataca con 11. Vida restante de fff: 80\nJugador fff ataca con 8. Vida restante de fff: 83\nJugador fff ataca con 9. Vida restante de fff: 71\nJugador fff ataca con 20. Vida restante de fff: 63\nJugador fff ataca con 19. Vida restante de fff: 52\nJugador fff ataca con 7. Vida restante de fff: 56\nJugador fff ataca con 10. Vida restante de fff: 42\nJugador fff ataca con 11. Vida restante de fff: 45\nJugador fff ataca con 16. Vida restante de fff: 26\nJugador fff ataca con 20. Vida restante de fff: 25\nJugador fff ataca con 10. Vida restante de fff: 16\nJugador fff ataca con 13. Vida restante de fff: 12\nJugador fff ataca con 15. Vida restante de fff: 1\nJugador fff ataca con 11. Vida restante de fff: 1\nJugador fff ataca con 10. Vida restante de fff: 0\nJugador fff ataca con 19. Vida restante de fff: 0\n¡Ganador: fff!\n', 'fff', 'fff'),
(14, '2024-12-12', 2, 5, 'Simulación de partida:\nJugador ggdgdgdg ataca con 13. Vida restante de fefwfw: 87\nJugador fefwfw ataca con 18. Vida restante de ggdgdgdg: 82\nJugador ggdgdgdg ataca con 23. Vida restante de fefwfw: 64\nJugador fefwfw ataca con 8. Vida restante de ggdgdgdg: 74\nJugador ggdgdgdg ataca con 20. Vida restante de fefwfw: 44\nJugador fefwfw ataca con 9. Vida restante de ggdgdgdg: 65\nJugador ggdgdgdg ataca con 6. Vida restante de fefwfw: 38\nJugador fefwfw ataca con 11. Vida restante de ggdgdgdg: 54\nJugador ggdgdgdg ataca con 20. Vida restante de fefwfw: 18\nJugador fefwfw ataca con 7. Vida restante de ggdgdgdg: 47\nJugador ggdgdgdg ataca con 5. Vida restante de fefwfw: 13\nJugador fefwfw ataca con 10. Vida restante de ggdgdgdg: 37\nJugador ggdgdgdg ataca con 9. Vida restante de fefwfw: 4\nJugador fefwfw ataca con 14. Vida restante de ggdgdgdg: 23\nJugador ggdgdgdg ataca con 24. Vida restante de fefwfw: 0\nJugador fefwfw ataca con 17. Vida restante de ggdgdgdg: 6\n¡Ganador: ggdgdgdg!\n', 'ggdgdgdg', 'fefwfw'),
(15, '2024-12-12', 3, 4, 'Simulación de partida:\nJugador dff ataca con 15. Vida restante de dgsf: 85\nJugador dgsf ataca con 7. Vida restante de dff: 93\nJugador dff ataca con 20. Vida restante de dgsf: 65\nJugador dgsf ataca con 16. Vida restante de dff: 77\nJugador dff ataca con 10. Vida restante de dgsf: 55\nJugador dgsf ataca con 18. Vida restante de dff: 59\nJugador dff ataca con 7. Vida restante de dgsf: 48\nJugador dgsf ataca con 16. Vida restante de dff: 43\nJugador dff ataca con 8. Vida restante de dgsf: 40\nJugador dgsf ataca con 13. Vida restante de dff: 30\nJugador dff ataca con 12. Vida restante de dgsf: 28\nJugador dgsf ataca con 6. Vida restante de dff: 24\nJugador dff ataca con 18. Vida restante de dgsf: 10\nJugador dgsf ataca con 11. Vida restante de dff: 13\nJugador dff ataca con 21. Vida restante de dgsf: 0\nJugador dgsf ataca con 18. Vida restante de dff: 0\n¡Ganador: dgsf!\n', 'dff', 'dgsf'),
(16, '2024-12-12', 2, 5, 'Simulación de partida:\nJugador HHD ataca con 5. Vida restante de WW: 95\nJugador WW ataca con 5. Vida restante de HHD: 95\nJugador HHD ataca con 21. Vida restante de WW: 74\nJugador WW ataca con 13. Vida restante de HHD: 82\nJugador HHD ataca con 24. Vida restante de WW: 50\nJugador WW ataca con 21. Vida restante de HHD: 61\nJugador HHD ataca con 5. Vida restante de WW: 45\nJugador WW ataca con 21. Vida restante de HHD: 40\nJugador HHD ataca con 21. Vida restante de WW: 24\nJugador WW ataca con 6. Vida restante de HHD: 34\nJugador HHD ataca con 16. Vida restante de WW: 8\nJugador WW ataca con 20. Vida restante de HHD: 14\nJugador HHD ataca con 21. Vida restante de WW: 0\nJugador WW ataca con 20. Vida restante de HHD: 0\n¡Ganador: WW!\n', 'HHD', 'WW'),
(17, '2024-12-12', 2, 5, 'Simulación de partida:\nJugador hhh ataca con 23. Vida restante de ss: 77\nJugador ss ataca con 5. Vida restante de hhh: 95\nJugador hhh ataca con 22. Vida restante de ss: 55\nJugador ss ataca con 12. Vida restante de hhh: 83\nJugador hhh ataca con 15. Vida restante de ss: 40\nJugador ss ataca con 15. Vida restante de hhh: 68\nJugador hhh ataca con 7. Vida restante de ss: 33\nJugador ss ataca con 12. Vida restante de hhh: 56\nJugador hhh ataca con 16. Vida restante de ss: 17\nJugador ss ataca con 8. Vida restante de hhh: 48\nJugador hhh ataca con 7. Vida restante de ss: 10\nJugador ss ataca con 13. Vida restante de hhh: 35\nJugador hhh ataca con 23. Vida restante de ss: 0\nJugador ss ataca con 17. Vida restante de hhh: 18\n¡Ganador: hhh!\n', 'hhh', 'ss'),
(18, '2024-12-12', 2, 5, 'Simulación de partida:\nJugador RON ataca con 13. Vida restante de Whisky: 87\nJugador Whisky ataca con 15. Vida restante de RON: 85\nJugador RON ataca con 16. Vida restante de Whisky: 71\nJugador Whisky ataca con 21. Vida restante de RON: 64\nJugador RON ataca con 10. Vida restante de Whisky: 61\nJugador Whisky ataca con 24. Vida restante de RON: 40\nJugador RON ataca con 9. Vida restante de Whisky: 52\nJugador Whisky ataca con 22. Vida restante de RON: 18\nJugador RON ataca con 10. Vida restante de Whisky: 42\nJugador Whisky ataca con 10. Vida restante de RON: 8\nJugador RON ataca con 5. Vida restante de Whisky: 37\nJugador Whisky ataca con 11. Vida restante de RON: 0\n¡Ganador: Whisky!\n', 'RON', 'Whisky'),
(19, '2024-12-12', 2, 6, 'Simulación de partida:\nJugador POLA ataca con 19. Vida restante de GUARO: 81\nJugador GUARO ataca con 22. Vida restante de POLA: 78\nJugador POLA ataca con 21. Vida restante de GUARO: 60\nJugador GUARO ataca con 10. Vida restante de POLA: 68\nJugador POLA ataca con 18. Vida restante de GUARO: 42\nJugador GUARO ataca con 17. Vida restante de POLA: 51\nJugador POLA ataca con 14. Vida restante de GUARO: 28\nJugador GUARO ataca con 19. Vida restante de POLA: 32\nJugador POLA ataca con 18. Vida restante de GUARO: 10\nJugador GUARO ataca con 13. Vida restante de POLA: 19\nJugador POLA ataca con 15. Vida restante de GUARO: 0\nJugador GUARO ataca con 14. Vida restante de POLA: 5\n¡Ganador: POLA!\n', 'POLA', 'GUARO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ranking`
--

CREATE TABLE `ranking` (
  `id` int(11) NOT NULL,
  `id_jugador` int(11) NOT NULL,
  `puntuacion` int(11) NOT NULL DEFAULT 0,
  `posicion` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `ranking`
--

INSERT INTO `ranking` (`id`, `id_jugador`, `puntuacion`, `posicion`) VALUES
(10, 2, 225, 2),
(11, 5, 225, 3),
(20, 4, 555, 1),
(21, 3, 80, 4),
(35, 6, 75, 5);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `inventario`
--
ALTER TABLE `inventario`
  ADD PRIMARY KEY (`jugador_id`,`nombre_articulo`);

--
-- Indices de la tabla `jugadores`
--
ALTER TABLE `jugadores`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `mundos`
--
ALTER TABLE `mundos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `partidas`
--
ALTER TABLE `partidas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fkjugador1` (`equipo1_idjugador`),
  ADD KEY `fkjugador2` (`equipo2_idjugador`);

--
-- Indices de la tabla `ranking`
--
ALTER TABLE `ranking`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `id_jugador` (`id_jugador`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `jugadores`
--
ALTER TABLE `jugadores`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `mundos`
--
ALTER TABLE `mundos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `partidas`
--
ALTER TABLE `partidas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT de la tabla `ranking`
--
ALTER TABLE `ranking`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `inventario`
--
ALTER TABLE `inventario`
  ADD CONSTRAINT `FKnpe6hqlqagr68o480ldjcc6lj` FOREIGN KEY (`jugador_id`) REFERENCES `jugadores` (`id`);

--
-- Filtros para la tabla `partidas`
--
ALTER TABLE `partidas`
  ADD CONSTRAINT `fkjugador1` FOREIGN KEY (`equipo1_idjugador`) REFERENCES `jugadores` (`id`),
  ADD CONSTRAINT `fkjugador2` FOREIGN KEY (`equipo2_idjugador`) REFERENCES `jugadores` (`id`);

--
-- Filtros para la tabla `ranking`
--
ALTER TABLE `ranking`
  ADD CONSTRAINT `ranking_ibfk_1` FOREIGN KEY (`id_jugador`) REFERENCES `jugadores` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
