# Roadmap de desarrollo

## Etapa 0 — infraestructura
- [x] Crear proyecto Forge para Minecraft 26.1.2.
- [x] Fijar Forge 64.1.5 y Java 25.
- [x] Validar la compilación base en CI.
- [x] Añadir smoke test automatizado de arranque del servidor dedicado en CI (pendiente validar el nuevo workflow).

## Etapa 1 — supervivencia
- [x] Modelo de dominio de hidratación con drenaje, estados y valores para HUD.
- [x] Modelo de dominio de hambre con drenaje, estados y valores para HUD.
- [ ] Conectar hambre y sed al ciclo de vida/tick de jugadores.
- [ ] Persistencia de hambre y sed entre desconexiones y reinicios.
- [ ] Sincronización servidor-cliente y HUD de supervivencia.
- [ ] Consumo de agua y alimentos dentro del juego.
- [ ] Penalizaciones graduales por hambre y deshidratación.
- [ ] Temperatura, heridas, sangrado y curación.

## Etapa 2 — mundo y economía de recursos
- [x] Definir el área inicial de mapa de 4.000 × 4.000 bloques.
- [x] Definir la máscara de costa irregular y el perfil lógico de altura/biomas.
- [x] Planificar monumentos y depósitos sobre tierra, con exclusiones.
- [x] Definir reglas para impedir que piedra/minerales vanilla alimenten la economía.
- [x] Planificar vegetación determinista por chunk según bioma y pendiente.
- [x] Exportar una vista previa PNG reproducible del mapa, puntos de interés y campos de recursos.
- [ ] Generar chunks automáticamente a partir del perfil de terreno.
- [x] Añadir comando administrativo /rustank generate para materializar terreno y vegetación en el chunk del jugador (puente manual; aún no es generación automática).
- [ ] Registrar biomas y configurar océano, playas y terreno en Minecraft.
- [ ] Reforzar los límites en teletransportes, portales y puntos de aparición.
- [ ] Nodos físicos de piedra, metal y azufre con reservas y reaparición.
- [ ] Monumentos, carreteras y zonas de riesgo construidos.
- [ ] Persistencia real de los depósitos con API Forge.
- [ ] Inventario, loot y contenedores.

## Etapa 3 — combate
- [ ] Armas, munición, recarga y retroceso.
- [ ] Daño, protección y balance PvP.

## Etapa 4 — bases y raideos
- [ ] Piezas de construcción y niveles de mejora.
- [ ] Armario de herramientas y privilegios de construcción.
- [ ] Cerraduras, códigos y permisos.
- [ ] Explosivos y daño estructural.

## Etapa 5 — contenido vivo
- [ ] NPC, científicos y animales.
- [ ] Drops y eventos dinámicos.

## Etapa 6 — servidor público
- [ ] Pruebas de carga y optimización.
- [ ] Protección contra griefing, exploits y abuso.
- [ ] Moderación, reglas, backups y recuperación.
- [ ] Despliegue público y guía de conexión.

## Estado actual del mundo

El mapa inicial está definido como un área de 4.000 × 4.000 bloques, con frontera vanilla aplicada al arrancar. El terreno, los monumentos y los recursos cuentan con planificadores deterministas y una vista previa PNG, pero la generación dentro del mundo sigue siendo un comando manual por chunk. El servidor no debe anunciarse como un Rust jugable hasta completar el generador real, los sistemas de recursos, la supervivencia y el combate.

## Plano vertical y vegetación

El modelo lógico de chunks calcula materiales por altura Y, costa, perfil de terreno y lecho submarino. El comando /rustank generate materializa el plano del chunk del jugador, junto con árboles, arbustos y cactus deterministas. Este puente sirve para pruebas controladas; no sustituye todavía al generador automático.

## Vista previa y economía

La tarea generateWorldPreview exporta un PNG cenital reproducible del mapa de 4.000 × 4.000 bloques. Incluye biomas, puntos de interés y marcadores de depósitos superficiales de piedra, metal y azufre. Estos marcadores son planificación visual: todavía no representan nodos minables persistidos en Minecraft.

## Frontera

Al iniciar, el servidor aplica una frontera de 4.000 × 4.000 bloques centrada en (0, 0) en todas las dimensiones. Faltan validaciones específicas para teletransportes, portales y puntos de aparición.
