# Necesidades de supervivencia: hambre e hidratación

## Modelo de hambre

com.rustank.survival.HungerState es un modelo de dominio sin dependencias de Minecraft. Su balance inicial es:

- Rango de 0 a 100.
- Actividad normal: pierde 1 punto cada 1.800 ticks (90 segundos a 20 TPS).
- Esprintar: pierde 1 punto cada 900 ticks (45 segundos a 20 TPS).
- Comer restaura una cantidad configurable, limitada a 100.
- Comer reinicia el reloj de drenaje.
- Creativo y espectador no consumen hambre ni avanzan el reloj.
- La carga de datos limita el valor al rango permitido.
- La suma al comer usa un valor de 64 bits para evitar desbordamiento.
- Estados: satisfecho (>30), hambriento (11–30), inanición (1–10) y vacío (0).
- Expone una fracción 0..1 para el futuro HUD.

## Modelo de hidratación

com.rustank.survival.HydrationState mantiene un rango de 0 a 100, con pérdida cada 60 segundos a ritmo normal y cada 30 segundos al esprintar. Sus estados son hidratado (>25), sediento (11–25), sed severa (1–10) y deshidratado (0).

## Estado de integración

Ambos modelos son lógica de dominio probada de forma independiente. Todavía no están conectados al ciclo de vida del jugador, guardado persistente, sincronización de red, consumo de objetos vanilla ni HUD. Tampoco aplican aún daño o penalizaciones al llegar a cero. Esos pasos deben implementarse y probarse antes de considerar hambre y sed como sistemas jugables.

## Balance provisional

Hambre se agota más lentamente que hidratación, para que el agua sea una necesidad más frecuente sin que la alimentación resulte irrelevante. Los intervalos y umbrales son valores iniciales que se ajustarán durante pruebas en servidor real.
