# Necesidades de supervivencia: hambre e hidratación

## Modelo de hambre

com.rustank.survival.HungerState es un modelo de dominio sin dependencias de Minecraft:

- Rango de 0 a 100.
- Actividad normal: pierde 1 punto cada 1.800 ticks (90 segundos a 20 TPS).
- Esprintar: pierde 1 punto cada 900 ticks (45 segundos a 20 TPS).
- Comer restaura una cantidad configurable, limitada a 100 y reinicia el reloj.
- Creativo y espectador no consumen hambre ni avanzan el reloj.
- Estados: satisfecho (>30), hambriento (11–30), inanición (1–10) y vacío (0).
- Expone una fracción 0..1 para el futuro HUD.
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

El puente SurvivalNeedsEvents ya está registrado desde RustankMod:

- Escucha el tick Post del jugador en el lado servidor.
- Utiliza el estado real de esprint y excluye creativo/espectador.
- Carga datos de PlayerEvent.LoadFromFile cuando existe un registro vanilla anterior.
- Para jugadores nuevos, carga también al entrar mediante la ruta de datos del servidor.
- Guarda durante PlayerEvent.SaveToFile y vuelve a guardar directamente durante PlayerLoggedOutEvent, antes de que Forge retire al jugador de la lista.
- Conserva la caché en memoria si la escritura de desconexión falla, en lugar de descartarla silenciosamente.

Los datos se guardan en un archivo lateral por jugador con firma y versión propias (<uuid>.rustank-needs). No se modifica el archivo vanilla .dat. La escritura usa un archivo temporal y reemplazo atómico cuando el sistema de archivos lo permite. Un archivo ausente crea los valores iniciales; uno corrupto se registra en el log y utiliza valores seguros.

## Pendiente para completar la supervivencia jugable

La simulación y el guardado son autoritativos en servidor, pero todavía faltan la sincronización al cliente, el HUD, los objetos consumibles de agua y comida y las penalizaciones al llegar a cero. Los umbrales e intervalos son valores de balance inicial y se ajustarán con pruebas reales.
