# MINECRAFT-RUSTANK

Servidor público de Minecraft Java inspirado en Rust. El proyecto incluye un mod Forge obligatorio para el servidor y para los jugadores; no es un plugin Paper.

## Plataforma fijada
- Minecraft Java: 26.1.2
- Minecraft Forge: 64.1.5 (build recomendado)
- Java: 25
- Mod ID: `rustank`

## Objetivo
Construir por etapas una experiencia de supervivencia PvP: recursos, hambre/sed, armas, construcción de bases, cerraduras, raideos, loot, monumentos, NPC y eventos.

## Estado actual
**Etapa 0 — base técnica.** Se está creando el proyecto Forge y su configuración de compilación. Todavía no es un servidor público desplegado y aún no hay un paquete instalable para jugadores.

## Estructura prevista
- `src/main/java`: lógica del mod del servidor.
- `src/main/resources`: metadatos y recursos.
- `server/`: archivos de configuración y arranque para el servidor dedicado.
- `docs/`: decisiones técnicas y guía de despliegue.

## Compilar
Se requiere JDK 25 y Gradle 9.3.1:
```bash
gradle build
```
El JAR resultante se genera en `build/libs/`.

## Probar en servidor dedicado
Con el entorno Forge 26.1.2-64.1.5 instalado, inicia el servidor con Java 25, acepta la EULA en `eula.txt` y usa el directorio `server/` como base de configuración. No publiques el puerto ni abras el servidor a Internet hasta configurar whitelist/admins, firewall, backups y protección contra ataques.

## Acceso público
El repositorio contiene el código; no proporciona alojamiento, IP pública ni protección DDoS. El despliegue público se realizará cuando exista una build probada y se configure un host adecuado.
