# Servidor dedicado Rustank (Windows)

1. Instala JDK 25 y comprueba `java -version`.
2. Ejecuta `install-forge.bat` para instalar Forge 26.1.2-64.1.5 desde el repositorio oficial.
3. Copia el JAR compilado de Rustank a `mods\`.
4. Copia `server.properties.example` como `server.properties` y ajusta el nombre y los límites.
5. Lee la EULA oficial. Solo si estás de acuerdo, cambia `eula=false` a `eula=true`.
6. Ejecuta `start-server.bat`.

**No abras el puerto 25565 a Internet todavía.** Primero hay que comprobar que el servidor arranca, que el mod carga sin errores y que los jugadores pueden entrar con la misma versión y mods. Para una apertura pública real también se necesita alojamiento, firewall, backups, moderación y protección DDoS.
