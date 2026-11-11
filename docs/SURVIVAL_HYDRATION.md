# Sistema de supervivencia: hidratación

## Núcleo implementado

El primer componente del dominio de supervivencia es `com.rustank.survival.HydrationState`.

- Hidratación de 0 a 100.
- Actividad normal: pierde 1 punto cada 1.200 ticks de servidor (60 segundos a 20 TPS).
- Esprintar: pierde 1 punto cada 600 ticks (30 segundos a 20 TPS).
- Beber restaura una cantidad configurable, sin superar 100.
- La suma al beber está protegida frente a desbordamientos de enteros.
- Beber reinicia el reloj de drenaje para evitar perder hidratación inmediatamente después de consumir agua.
- Al cargar un valor persistido, se limita al rango válido.
- Creativo y espectador no consumen hidratación ni avanzan el reloj.
- La hidratación nunca baja de cero.
- El modelo clasifica el estado en hidratado (>25), sediento (11–25), sed severa (1–10) y deshidratado (0).
- Expone una fracción normalizada de 0 a 1 para que el futuro HUD no replique cálculos.
- El modelo no depende de clases Minecraft ni del cliente; puede probarse aisladamente.

## Verificación automatizada

`HydrationStateTest` comprueba drenaje normal y al esprintar, exclusión de creativo/espectador, límites al beber, importes extremos, reinicio del reloj al beber, límites al cargar, clasificación en los umbrales 25/10/0, fracción del HUD y el suelo de cero. GitHub Actions ejecuta estas pruebas con `gradle build`.

## Estado de integración en juego

Este es todavía el modelo de dominio, no una característica ya conectada al jugador. Falta integrar el ciclo de vida/tick del servidor, persistencia en datos del jugador, sincronización al cliente y HUD. Las penalizaciones por deshidratación se añadirán después de establecer esas bases.

## Balance provisional

- Normal: 1 punto por minuto.
- Esprintando: 2 puntos por minuto.
- Máximo: 100.

Son valores iniciales de desarrollo; se ajustarán durante pruebas reales.
