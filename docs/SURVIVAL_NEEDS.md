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
- Su snapshot conserva tanto el nivel de hambre como el avance parcial del temporizador, para que un guardado futuro no reinicie gratis el drenaje.

## Modelo de hidratación

com.rustank.survival.HydrationState mantiene un rango de 0 a 100, con pérdida cada 60 segundos a ritmo normal y cada 30 segundos al esprintar. Sus estados son hidratado (>25), sediento (11–25), sed severa (1–10) y deshidratado (0). Su snapshot también conserva el avance parcial del temporizador.

## Estado combinado por jugador

com.rustank.survival.SurvivalNeedsState reúne hambre e hidratación en un único objeto de dominio por jugador. Su snapshot contiene ambos niveles y los dos temporizadores parciales, y su tick actualiza los dos modelos con una sola llamada. Así se reduce la posibilidad de que el guardado, la sincronización o el futuro HUD utilicen cálculos distintos.

## Integración inicial con Forge

El puente de servidor escucha el tick Post de cada jugador y avanza ambos modelos usando el estado de esprint y el modo de juego. El modo creativo y el espectador quedan excluidos.

Forge 26.1.2 ofrece los eventos PlayerEvent.LoadFromFile y SaveToFile para almacenar datos adicionales en un archivo por jugador. Rustank utiliza un archivo lateral con firma y versión propias, sin modificar el archivo vanilla .dat. La escritura se realiza mediante un archivo temporal y reemplazo atómico cuando el sistema de archivos lo permite. Un archivo ausente crea las necesidades iniciales; un archivo corrupto se registra en el log y utiliza valores seguros.

Esta integración aún no incluye el HUD ni sincronización de datos al cliente. Tampoco añade penalizaciones por llegar a cero.

## Estado de integración

Ambos modelos son lógica de dominio probada de forma independiente. Todavía no están conectados al ciclo de vida del jugador, guardado persistente, sincronización de red, consumo de objetos vanilla ni HUD. Tampoco aplican aún daño o penalizaciones al llegar a cero. Esos pasos deben implementarse y probarse antes de considerar hambre y sed como sistemas jugables.

## Balance provisional

Hambre se agota más lentamente que hidratación, para que el agua sea una necesidad más frecuente sin que la alimentación resulte irrelevante. Los intervalos y umbrales son valores iniciales que se ajustarán durante pruebas en servidor real.
