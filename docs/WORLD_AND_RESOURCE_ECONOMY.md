# Economía del mundo y recursos
## Límites y distribución
El mapa planeado mide 4.000 × 4.000 bloques: X/Z desde -2.000 inclusive hasta 2.000 exclusive. La semilla determina de forma reproducible la distribución de depósitos y puntos de interés.
## Depósitos
Los depósitos de piedra, metal y azufre tienen reservas finitas y tiempos de reaparición propios. La extracción se gestiona desde un registro central, con coordenadas únicas, stock individual y snapshots ordenados para persistencia.
El registro valida que los depósitos restaurados estén dentro de los límites del mapa. Esto evita que datos dañados introduzcan nodos fuera del área jugable. También rechaza coordenadas duplicadas y snapshots inválidos.
## Estado de integración
- WorldBounds, ResourceFieldPlanner, WorldFeaturePlanner, ResourceDeposit y ResourceDepositRegistry implementan lógica de dominio y planificación.
- MiningRules define los drops vanilla que deberán bloquearse.
- Falta conectar la frontera a movimiento/teletransporte, el registro a SavedData real de Forge, los depósitos a bloques visibles y los eventos de extracción.
- Aún no hay terreno/isla procedural ni monumentos construidos en el mundo. Las ubicaciones actuales son planes, no estructuras generadas.
## Pruebas
La suite cubre límites, determinismo, separación, extracción parcial/total, agotamiento, reaparición, snapshots, duplicados y coordenadas fuera del mapa.