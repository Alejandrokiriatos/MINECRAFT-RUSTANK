# MINECRAFT-RUSTANK

Servidor público de Minecraft Java inspirado en Rust. El proyecto incluye un mod Forge obligatorio para el servidor y para los jugadores; no es un plugin Paper.

## Plataforma fijada
- Minecraft Java: 26.1.2
- Minecraft Forge: 64.1.5
- Java: 25
- Mod ID: `rustank`

## Objetivo
Construir por etapas una experiencia de supervivencia PvP: recursos, hambre/sed, armas, construcción de bases, cerraduras, raideos, loot, monumentos, NPC y eventos.

## Estado actual
**Base Forge compilable + primer modelo de hidratación probado automáticamente.** La compilación de GitHub Actions del commit `6eac470` terminó correctamente y publicó un JAR de desarrollo. El modelo de hidratación tiene pruebas unitarias; todavía no está conectado al jugador ni representa una versión jugable completa.

## Estructura
- `src/main/java`: lógica del mod.
- `src/main/resources`: metadatos y recursos.
- `server/`: configuración y arranque del servidor dedicado.
- `docs/`: decisiones técnicas, supervivencia y despliegue.

## Compilar
Se requiere JDK 25 y Gradle 9.3.1:
```bash
gradle build
```
El JAR resultante se genera en `build/libs/`.

## Probar en servidor dedicado
Con Forge 26.1.2-64.1.5 instalado, inicia el servidor con Java 25, acepta la EULA en `eula.txt` y usa `server/` como base de configuración. No publiques el puerto ni abras el servidor a Internet hasta configurar whitelist/admins, firewall, copias de seguridad y protección contra ataques.

## Acceso público
El repositorio contiene el código; no proporciona alojamiento, IP pública ni protección DDoS. El despliegue público se realizará cuando exista una build jugable probada y se configure un host adecuado.
