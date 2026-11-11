# Roadmap de desarrollo

## Etapa 0 — infraestructura
- [x] Crear proyecto Forge para Minecraft 26.1.2.
- [x] Fijar Forge 64.1.5 y Java 25.
- [x] Validar la compilación base en CI.
- [x] Añadir smoke test automatizado de arranque del servidor dedicado en CI.

## Etapa 1 — supervivencia
- [x] Modelo de dominio de hidratación con drenaje, estados y valores para HUD.
- [x] Modelo de dominio de hambre con drenaje, estados y valores para HUD.
- [x] Conectar hambre y sed al tick de jugadores del servidor (incluye sprint y exclusión de creativo/espectador).
- [x] Persistencia por jugador mediante archivo lateral versionado, sin alterar el .dat vanilla.
- [x] Sincronización servidor-cliente de hambre y sed mediante canal Forge versionado.
- [x] HUD de hambre y sed mediante capa del HUD de Forge.
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

## Integración de supervivencia con el servidor

El servidor avanza hambre y sed una vez por tick de jugador. El esprint utiliza los intervalos rápidos, y creativo/espectador quedan excluidos. Los niveles y temporizadores parciales se guardan en archivos laterales por jugador, con formato versionado. Un canal Forge de juego, exclusivamente clientbound y versionado, sincroniza los niveles al entrar y cada vez que cambia una necesidad. El cliente muestra dos barras de HAMBRE y SED en una capa del HUD. Los objetos consumibles y las penalizaciones por llegar a cero siguen pendientes.

## Plano de terreno

El modelo lógico de chunks calcula materiales por altura Y, costa, perfil de terreno y lecho submarino. El comando administrativo /rustank generate materializa el plano del chunk del jugador junto con árboles, arbustos y cactus deterministas. La tarea generateWorldPreview exporta un PNG cenital reproducible del mapa y sus marcadores de monumentos y recursos. La generación automática de chunks, los depósitos minables dentro del mundo y los monumentos construidos siguen pendientes.
