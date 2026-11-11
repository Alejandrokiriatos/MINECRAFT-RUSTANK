# Economía del mundo y recursos

## Límites y forma de la isla
El mapa planeado mide 4.000 × 4.000 bloques: X/Z desde -2.000 inclusive hasta 2.000 exclusive. `IslandShape` define una costa irregular determinista con un radio base de 1.750 bloques y variaciones angulares. Las esquinas exteriores son océano y el centro es tierra.

La máscara de isla es todavía una planificación matemática: no genera bloques de agua, altura, playas, terreno ni biomas.

## Puntos de interés
La semilla determina de forma reproducible cinco puntos de interés: spawn, aeródromo, puerto, base militar y zona radiactiva. Todos los centros se colocan en tierra; el puerto se reserva cerca de la costa. Los radios de exclusión mantienen separados los monumentos.

## Depósitos
Los depósitos de piedra, metal y azufre tienen reservas finitas y tiempos de reaparición propios. Se colocan en tierra, fuera de las zonas de exclusión de los puntos de interés y con separación mínima entre sí.

El registro valida que los depósitos restaurados estén dentro de los límites del mapa. Esto evita que datos dañados introduzcan nodos fuera del área jugable. También rechaza coordenadas duplicadas y snapshots inválidos.

## Economía minera
La política `MiningRules` bloquea los drops normales de piedra, adoquín, deepslate y minerales vanilla, incluidos los minerales del Nether y la ancient debris. La comprobación exige el namespace `minecraft`: los bloques de otros mods no se bloquean automáticamente.

Esta política todavía es una regla de dominio. Hasta que se conecte al evento real de rotura/drop de Forge, no modifica el comportamiento dentro del juego.

## Estado de integración
- IslandShape, WorldBounds, ResourceFieldPlanner y WorldFeaturePlanner implementan máscaras y planificación deterministas.
- ResourceDeposit y ResourceDepositRegistry implementan reservas finitas, extracción y snapshots en memoria.
- MiningRules define qué drops vanilla deberán bloquearse y tiene pruebas para piedra, minerales, namespaces ajenos e identificadores desconocidos.
- Falta generar el terreno físico a partir de la máscara, conectar la frontera a movimiento/teletransporte, persistir el registro con SavedData real de Forge, crear bloques visibles y conectar eventos de extracción.
- Los monumentos actuales son ubicaciones planificadas, no estructuras construidas.

## Pruebas
La suite cubre forma de costa y determinismo, ubicación de monumentos y recursos en tierra, separación, límites, extracción parcial/total, agotamiento, reaparición, snapshots y reglas de drops vanilla.
