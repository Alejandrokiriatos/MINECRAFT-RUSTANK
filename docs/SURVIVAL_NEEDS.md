# Necesidades de supervivencia: hambre e hidratación

## Modelo de hambre

com.rustank.survival.HungerState es un modelo de dominio sin dependencias de Minecraft:

- Rango de 0 a 100.
- Actividad normal: pierde 1 punto cada 1.800 ticks (90 segundos a 20 TPS).
- Esprintar: pierde 1 punto cada 900 ticks (45 segundos a 20 TPS).
- Comer restaura una cantidad configurable, limitada a 100 y reinicia el reloj.
- Creativo y espectador no consumen hambre ni avanzan el reloj.
- Estados: satisfecho (>30), hambriento (11–30), inanición (1–10) y vacío (0).
- Expone una fracción 0..1 para el HUD.
- Su snapshot conserva el nivel y el avance parcial del temporizador.

## Modelo de hidratación

com.rustank.survival.HydrationState mantiene un rango de 0 a 100:

- Actividad normal: pierde 1 punto cada 1.200 ticks (60 segundos a 20 TPS).
- Esprintar: pierde 1 punto cada 600 ticks (30 segundos a 20 TPS).
- Beber restaura una cantidad configurable, limitada a 100 y reinicia el reloj.
- Creativo y espectador no consumen hidratación ni avanzan el reloj.
- Estados: hidratado (>25), sediento (11–25), sed severa (1–10) y deshidratado (0).
- Expone una fracción 0..1 y conserva el avance parcial del temporizador en su snapshot.

## Estado combinado por jugador

com.rustank.survival.SurvivalNeedsState reúne hambre e hidratación en un único objeto por jugador. Su tick avanza ambos modelos y devuelve si cambió algún valor. Su snapshot guarda los dos niveles y los dos temporizadores parciales.

## Integración con Forge 26.1.2

SurvivalNeedsEvents procesa el tick Post del jugador en el servidor, usa el estado real de esprint y excluye creativo/espectador. Carga y guarda un archivo lateral por UUID a través de LoadFromFile/SaveToFile y también guarda al desconectarse. El archivo tiene firma, versión y validación estricta; no modifica el .dat vanilla. La escritura usa archivo temporal y reemplazo atómico.

## Sincronización y HUD

SurvivalNeedsNetwork registra un canal de juego versionado y exclusivamente clientbound. El servidor envía un snapshot al entrar y cuando cambia hambre o sed. El paquete contiene solo dos enteros acotados entre 0 y 100. No existe un paquete de cliente a servidor que permita modificar necesidades.

SurvivalNeedsClient registra una capa en el sistema de capas del HUD de Forge. Muestra barras para HAMBRE y SED. El HUD presenta valores recibidos del servidor; el drenaje y el guardado siguen siendo autoritativos en servidor.

## Pendiente para completar la supervivencia jugable

- Integrar comida y agua como objetos consumibles.
- Aplicar penalizaciones graduales al llegar a cero.
- Ajustar intervalos y umbrales con pruebas de juego reales.
