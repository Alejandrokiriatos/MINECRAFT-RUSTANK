# MINECRAFT-RUSTANK

Servidor público de Minecraft Java inspirado en Rust. El proyecto incluye un mod Forge obligatorio para el servidor y para los jugadores; no es un plugin Paper.

## Plataforma fijada
- Minecraft Java: 26.1.2
- Minecraft Forge: 64.1.5
- Java: 25
- Mod ID: `rustank`

## Objetivo
Construir por etapas una experiencia de supervivencia PvP: recursos controlados, hambre/sed, armas, construcción de bases, cerraduras, raideos, loot, monumentos, NPC y eventos.

## Estado actual
**Base Forge compilable + modelo de hidratación probado + primeras reglas puras para mapa y economía.** El mapa inicial se diseña con un área de 4.000 × 4.000 bloques. La política de recursos impide que la piedra, el adoquín y los minerales vanilla sean fuentes de materiales Rustank una vez conectada a los eventos Forge. Estas reglas de mundo todavía no están conectadas al juego y no constituyen una versión jugable completa.

## Estructura
- `src/main/java`: lógica del mod.
- `src/main/resources`: metadatos y recursos.
- `server/`: configuración y arranque del servidor dedicado.
- `docs/`: decisiones técnicas, supervivencia, mundo y despliegue.

## Compilar
Se requiere JDK 25 y Gradle 9.3.1:
```bash
gradle build
```
El JAR resultante se genera en `build/libs/`.

## Mundo y economía
Consulta [docs/WORLD_AND_RESOURCE_ECONOMY.md](docs/WORLD_AND_RESOURCE_ECONOMY.md) para el tamaño del mapa, la frontera y el diseño de extracción de recursos.

## Probar en servidor dedicado
Con Forge 26.1.2-64.1.5 instalado, inicia el servidor con Java 25, acepta la EULA en `eula.txt` y usa `server/` como base de configuración. No publiques el puerto ni abras el servidor a Internet hasta configurar whitelist/admins, firewall, copias de seguridad y protección contra ataques.

## Acceso público
El repositorio contiene el código; no proporciona alojamiento, IP pública ni protección DDoS. El despliegue público se realizará cuando exista una build jugable probada y se configure un host adecuado.
