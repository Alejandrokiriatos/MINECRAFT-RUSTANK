# Roadmap de desarrollo

## Etapa 0 — infraestructura
- [x] Crear proyecto Forge para Minecraft 26.1.2.
- [x] Fijar Forge 64.1.5 y Java 25.
- [x] Validar la compilación base en CI.
- [ ] Ejecutar un servidor dedicado de prueba.

## Etapa 1 — supervivencia
- [ ] Sed, hambre y temperatura.
- [ ] Estados de heridas, sangrado y curación.
- [ ] Persistencia de atributos entre reinicios.
- [ ] HUD de supervivencia.

## Etapa 2 — mundo y economía de recursos
- [x] Definir el área inicial de mapa de 4.000 × 4.000 bloques.
- [x] Definir la máscara de costa irregular y el perfil lógico de altura/biomas.
- [x] Planificar monumentos y depósitos sobre tierra, con exclusiones.
- [x] Definir reglas para impedir que piedra/minerales vanilla alimenten la economía.
- [x] Planificar vegetación determinista por chunk según bioma y pendiente.
- [x] Exportar una vista previa PNG reproducible del mapa y sus puntos de interés.
- [ ] Generar chunks reales a partir del perfil de terreno.
- [ ] Registrar biomas y configurar océano, playas y terreno en Minecraft.
- [ ] Aplicar la frontera en el servidor, teletransportes y portales.
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

## Estado añadido: plano vertical de chunks

El modelo lógico de chunks ya puede describir materiales por altura Y: superficie, 3–5 capas de tierra o arenisca según el terreno, roca profunda, lecho marino y agua hasta el nivel del mar. Los chunks se recortan en los límites de la isla. La implementación sigue siendo un plano de datos y todavía no coloca bloques en Minecraft; la integración con el generador Forge 64.1.5 sigue pendiente.

## Vegetación lógica por chunk

Se añadió un planificador determinista de árboles, arbustos y cactus. La distribución se ancla a una cuadrícula global para evitar duplicados al consultar chunks vecinos. Respeta biomas, límites de mapa y pendientes. Falta convertir estas ubicaciones en estructuras/bloques reales durante la generación de chunks.

## Vista previa reproducible del mundo

El proyecto puede exportar un PNG cenital de 1.000 × 1.000 píxeles que representa el mapa de 4.000 × 4.000 bloques, con biomas y círculos de exclusión/ubicaciones de los cinco puntos de interés. La tarea `generateWorldPreview` genera el archivo en `build/reports/rustank-world-preview.png`; CI lo publica como artefacto descargable. Es una herramienta de inspección del diseño, no un mapa Minecraft jugable.
