# Mundo limitado y economía de recursos

## Diseño inicial del mapa

- Área de juego: 4.000 × 4.000 bloques en X/Z.
- Coordenadas válidas: -2.000 inclusive a 2.000 exclusive en ambos ejes.
- El centro (0, 0) es la referencia para el diseño de la isla y la distribución de monumentos.
- El límite debe ser una frontera real del servidor, no solo una barrera visual: movimiento, teletransporte, portales y generación deben respetarlo.
- La primera generación procedural se organizará por sectores con semilla estable para que el mismo servidor produzca el mismo mapa tras reinicios.
- Los monumentos y depósitos se ubicarán mediante una planificación determinista y con distancias mínimas para evitar solapamientos.

La clase `WorldBounds` implementa y prueba la geometría exacta del área. Todavía no aplica la frontera al mundo: falta conectar las reglas a eventos Forge y a la configuración de world border del servidor.

## Evitar que cavar hacia abajo rompa la economía

No se deben entregar piedra, minerales ni materiales de construcción por minar bloques geológicos vanilla. Si no, cualquier jugador puede cavar una mina infinita y saltarse los puntos de interés, las herramientas y el riesgo PvP.

Reglas previstas:

1. Piedra, pizarra profunda, adoquín y variantes geológicas vanilla no entregan recursos Rustank.
2. Los minerales vanilla tampoco entregan sus drops normales; metal, azufre y componentes se obtienen mediante depósitos Rustank específicos.
3. La piedra útil se extrae de nodos/afloramientos visibles en la superficie, con herramienta adecuada, tiempo de extracción y cantidad limitada por nodo.
4. Los depósitos tienen cantidades finitas por jugador/servidor y un tiempo de reaparición configurable; no se generan en túneles ni debajo de bases.
5. El reciclaje de objetos y el loot son fuentes controladas adicionales, no duplicadores.
6. La generación del subsuelo no debe crear vetas vanilla aprovechables. La protección de drops es una segunda barrera, no un sustituto de controlar la generación.
7. Los bloques colocados por jugadores no deben convertirse en recursos al romperse: se aplicarán reglas separadas de construcción y devolución parcial.

`MiningRules` implementa ahora la política pura para identificar drops vanilla que deben suprimirse. Aún falta conectarla al evento de drops/break de Forge y crear los nodos de recursos; hasta entonces esta regla no cambia el juego.

## Siguiente implementación

- Conectar el límite al servidor y bloquear cruces por movimiento, teletransporte y portales.
- Añadir nodos de piedra, metal y azufre con reservas y respawn en el servidor.
- Desactivar la generación de minerales vanilla o sustituirla por la distribución controlada.
- Generar la isla, biomas, carreteras y monumentos por sectores con una semilla persistente.
