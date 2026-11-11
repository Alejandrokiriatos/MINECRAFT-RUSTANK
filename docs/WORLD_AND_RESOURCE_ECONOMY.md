# Economía del mundo y recursos

## Límites, costa y terreno
El mapa planeado mide 4.000 × 4.000 bloques: X/Z desde -2.000 inclusive hasta 2.000 exclusive. `IslandShape` define una costa irregular determinista. `TerrainProfile` calcula alturas y categorías ambientales: océano, playa, llanura, bosque, desierto y tierras altas rocosas.

`ChunkTerrainPlan` convierte esas muestras en un plano inmutable de 16 × 16 columnas por chunk. Cada columna contiene coordenadas absolutas, bioma lógico, altura del suelo, material de superficie, profundidad de subsuelo y nivel superior del agua. También ofrece una consulta de material por altura Y: superficie, capa de tierra/arenisca, roca profunda, lecho marino y columna de agua. El primer perfil usa un lecho marino provisional a Y=48 y agua hasta Y=63. Las columnas que caen fuera del límite de 4.000 × 4.000 quedan marcadas como OUT_OF_BOUNDS y sin material.

Este plano es una representación de datos intermedia: todavía no coloca bloques ni modifica chunks de Minecraft.

## Puntos de interés
La semilla determina cinco puntos de interés: spawn, aeródromo, puerto, base militar y zona radiactiva. Todos los centros se colocan en tierra; el puerto se reserva cerca de la costa. Los radios de exclusión mantienen separados los monumentos.

## Depósitos
Los depósitos de piedra, metal y azufre tienen reservas finitas y tiempos de reaparición propios. Se colocan en tierra, fuera de las zonas de exclusión de los puntos de interés y con separación mínima entre sí. El registro central valida snapshots, coordenadas y duplicados.

## Economía minera
`MiningRules` define los drops vanilla que deberán bloquearse para impedir que la geología normal alimente la economía. La política exige el namespace `minecraft` y aún no está conectada al evento real de rotura/drop de Forge.

## Estado de integración
- Implementados como lógica pura: límites, máscara de isla, perfil de terreno, planos de columnas por chunk, planificadores de puntos de interés y depósitos, reservas finitas y reglas mineras.
- Pendiente: conectar el plano a un generador Forge real, colocar bloques de terreno/agua, registrar biomas, aplicar la frontera a movimiento/teletransporte, persistir depósitos con la API exacta de Forge 64.1.5, crear bloques físicos y conectar eventos de extracción.
- Los monumentos son ubicaciones planificadas, no estructuras construidas.

## Pruebas
Las pruebas cubren determinismo, costa, clasificación de terreno, límites verticales, planos de 256 columnas, materiales, agua, recorte en bordes, distribución sobre tierra, separación, snapshots y reglas mineras.
