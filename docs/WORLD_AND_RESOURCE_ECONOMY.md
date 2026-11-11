# Economía del mundo y recursos

## Límites, costa y terreno
El mapa planeado mide 4.000 × 4.000 bloques: X/Z desde -2.000 inclusive hasta 2.000 exclusive. `IslandShape` define una costa irregular determinista. `TerrainProfile` añade una primera muestra de altura y clasificación ambiental por coordenada:
- Océano al nivel del mar 63.
- Playas de transición junto a la costa.
- Llanuras, bosques, desierto y tierras altas rocosas en el interior.
- Altura de tierra acotada entre 64 y 160.

La semilla controla el resultado. Este perfil es todavía una función matemática: no coloca bloques, no genera chunks ni registra biomas de Minecraft.

## Puntos de interés
La semilla determina cinco puntos de interés: spawn, aeródromo, puerto, base militar y zona radiactiva. Todos los centros se colocan en tierra; el puerto se reserva cerca de la costa. Los radios de exclusión mantienen separados los monumentos.

## Depósitos
Los depósitos de piedra, metal y azufre tienen reservas finitas y tiempos de reaparición propios. Se colocan en tierra, fuera de las zonas de exclusión de los puntos de interés y con separación mínima entre sí. El registro central valida snapshots, coordenadas y duplicados.

## Economía minera
`MiningRules` define los drops vanilla que deberán bloquearse para impedir que la geología normal alimente la economía. La política exige el namespace `minecraft` y aún no está conectada al evento real de rotura/drop de Forge.

## Estado de integración
- Implementados: límites, máscara de isla, perfil determinista de altura/biomas, planificadores de puntos de interés y depósitos, reservas finitas y reglas mineras.
- Pendiente: generación física de chunks, bloques de agua/terreno, biomas registrados en Minecraft, frontera de movimiento/teletransporte, persistencia real con API Forge 64.1.5, bloques de depósitos y eventos de extracción.
- Los monumentos son ubicaciones planificadas, no estructuras construidas.

## Pruebas
Las pruebas cubren determinismo, costa, clasificación de terreno, límites verticales, distribución sobre tierra, separación de recursos, exclusión de monumentos, snapshots y reglas mineras.
