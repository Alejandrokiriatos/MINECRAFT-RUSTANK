# Mundo limitado y economía de recursos

## Diseño inicial del mapa
- Área de juego: 4.000 × 4.000 bloques en X/Z.
- Coordenadas válidas: -2.000 inclusive a 2.000 exclusive.
- Franja de 64 bloques reservada en el borde para evitar nodos pegados a la frontera.
- La semilla del mundo determina la ubicación de depósitos y puntos de interés de forma reproducible.
- La primera distribución usa separación mínima para evitar agrupaciones o solapamientos.

`WorldBounds` implementa la geometría del mapa. Aún falta aplicar el límite al movimiento, teletransporte, portales y generación real del terreno.

## Puntos de interés y sectores
El primer planificador ubica cinco sectores principales:
- Spawn de jugadores, cerca del centro.
- Aeródromo.
- Puerto.
- Base militar.
- Zona radiactiva.

Cada punto tiene un radio de exclusión y una distancia de seguridad adicional para que sus áreas no se solapen. Las ubicaciones son deterministas por semilla. Este planificador solo define coordenadas X/Z: todavía no crea terreno, carreteras, edificios, loot ni radiación dentro de Minecraft.

## Economía: excavar no debe ser la estrategia principal
- Piedra, adoquín, pizarra profunda y minerales vanilla no deben entregar materiales Rustank.
- Los recursos económicos se obtienen en depósitos especiales visibles en superficie.
- Los nodos tienen reservas finitas: piedra 900, metal 450 y azufre 350 unidades por nodo.
- Al agotarse, cada nodo inicia un temporizador de reaparición: piedra 30 min, metal 45 min y azufre 60 min (20 TPS).
- La extracción y reaparición se modelan en ticks del mundo; el servidor debe ser la autoridad para evitar duplicación y trampas.
- Los depósitos pueden generar snapshots validados; el nuevo registro gestiona todos los depósitos, aplica extracciones por coordenadas y produce snapshots ordenados y deterministas. Aún falta conectar estos snapshots al almacenamiento persistente real del mundo.
- Los nodos no deben aparecer bajo edificios, en túneles ni dentro de monumentos; el generador final deberá validar altura, bioma, distancia a monumentos y construcciones.
- Los bloques puestos por jugadores no se reciclan como depósitos: tendrán reglas de construcción y devolución parcial separadas.
- Los drops vanilla bloqueados son una barrera adicional; también habrá que revisar recetas, cofres, comercio, aldeanos, generadores y exploits de duplicación.

## Implementación de esta tanda
- `WorldFeaturePlanner`: distribución determinista de spawn, aeródromo, puerto, base militar y zona radiactiva, con radios de exclusión y separación.
- `ResourceFieldPlanner`: distribución determinista por semilla, dentro del mapa y con distancia mínima; valida límites de entrada y evita desbordamientos al calcular intentos.
- `ResourceDeposit`: reserva finita, reaparición temporizada y snapshot/restauración validada.
- `ResourceDepositRegistry`: registro central de nodos por coordenadas, extracción autoritativa de dominio, avance de reaparición y snapshots ordenados para persistencia futura.
- Pruebas unitarias de reservas, reaparición, restauración, determinismo, separación, duplicados y límites.
- Todavía no están conectados a bloques, eventos Forge, guardado real del mundo ni HUD; no cambian aún la generación ni la extracción dentro del juego.

## Próximos pasos
1. Adaptar el registro al sistema de datos guardados de Minecraft 26.1.2 / Forge 64.1.5 y verificarlo compilando contra esa versión exacta.
2. Aplicar la frontera al servidor y restringir teletransportes/portales.
3. Crear bloques/modelos de nodos y herramienta de extracción.
4. Generar isla, biomas, carreteras y estructuras con semilla estable.
5. Conectar y probar el sistema completo en un servidor dedicado.
