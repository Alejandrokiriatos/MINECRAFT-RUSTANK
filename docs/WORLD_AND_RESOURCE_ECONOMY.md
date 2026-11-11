# Mundo limitado y economía de recursos

## Diseño inicial del mapa
- Área de juego: 4.000 × 4.000 bloques en X/Z.
- Coordenadas válidas: -2.000 inclusive a 2.000 exclusive.
- Franja de 64 bloques reservada en el borde para evitar nodos pegados a la frontera.
- La semilla del mundo determina la ubicación de los depósitos de forma reproducible.
- La primera distribución usa separación mínima para evitar agrupaciones o solapamientos.

`WorldBounds` implementa la geometría del mapa. Aún falta aplicar el límite a movimiento, teletransporte, portales y generación real del terreno.

## Economía: excavar no debe ser la estrategia principal
- Piedra, adoquín, pizarra profunda y minerales vanilla no deben entregar materiales Rustank.
- Los recursos económicos se obtienen en depósitos especiales visibles en superficie.
- Los nodos tienen reservas finitas: piedra 900, metal 450 y azufre 350 unidades por nodo.
- Al agotarse, cada nodo inicia un temporizador de reaparición: piedra 30 min, metal 45 min y azufre 60 min (20 TPS).
- La extracción y reaparición se modelan en ticks del mundo; el servidor debe ser la autoridad para evitar duplicación y trampas.
- Los depósitos ya pueden crear snapshots validados para guardar stock y temporizador. Aún falta conectar esos snapshots al almacenamiento persistente del mundo.
- Los nodos no deben aparecer bajo edificios, en túneles ni dentro de monumentos; el generador final deberá validar altura, bioma, distancia a monumentos y construcciones.
- Los bloques puestos por jugadores no se reciclan como depósitos: tendrán reglas de construcción y devolución parcial separadas.
- Los drops vanilla bloqueados son una barrera adicional; también habrá que revisar recetas, cofres, comercio, aldeanos, generadores y exploits de duplicación.

## Implementación de esta tanda
- `ResourceDeposit`: reserva finita, reaparición temporizada y snapshot/restauración validada.
- `ResourceFieldPlanner`: distribución determinista por semilla, dentro del mapa y con distancia mínima.
- Pruebas unitarias de reservas, reaparición, restauración, determinismo y separación.
- Todavía no están conectados a bloques, eventos Forge, guardado real del mundo ni HUD; no cambian aún la extracción dentro del juego.

## Próximos pasos
1. Conectar la frontera al servidor y restringir teletransportes/portales.
2. Persistir los snapshots de depósitos en datos del mundo y validar ubicación contra terreno, monumentos y bases.
3. Crear bloques/modelos de nodos y herramienta de extracción.
4. Generar isla, biomas, carreteras y monumentos con semilla estable.
5. Conectar y probar el sistema completo en un servidor dedicado.
