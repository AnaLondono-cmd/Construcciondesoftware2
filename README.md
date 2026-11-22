# Operación Alquimia — Protocolo Savitar

API REST (Java 17 + Spring Boot 3) que reconstruye los mensajes interceptados de Alchemy y localiza su laboratorio
mediante trilateración, usando **DDD + Arquitectura Hexagonal**. Todo se guarda **en memoria** (sin BD ni servicios externos).

## Requisitos
- JDK 17 o superior
- Maven 3.8+ (`mvn -v`)

Sin rutas absolutas ni dependencias del sistema operativo: funciona igual en Windows, macOS y Linux.

## Ejecutar
```bash
mvn spring-boot:run        # arranca en http://localhost:8080
mvn test                   # pruebas unitarias y de API
mvn package && java -jar target/operacion-alquimia-1.0.0.jar
```
Otro puerto: `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9090`

## Arquitectura
```
domain/            (Java puro, sin Spring ni HTTP)
  model/           SurveillanceDevice, Position, Distance, MessageFragment, DeviceReading, AnalysisResult
  service/         Trilateration, MessageReconstructor, SignalAnalyzer
  exception/       errores de negocio
application/
  port/in/         casos de uso F1..F6 (puertos de entrada)
  port/out/        DeviceReadingRepository (puerto de salida)
  service/         implementaciones de los casos de uso
infrastructure/
  adapter/in/web/  controlador REST, DTOs, manejo de errores -> HTTP
  adapter/out/memory/ repositorio en memoria (ConcurrentHashMap)
  config/          BeanConfig (ensambla dominio/aplicación)
```
Dependencias: `infrastructure → application → domain`. El dominio y la aplicación no tienen anotaciones de Spring.

## Endpoints
| Req | Método y ruta | Descripción |
|---|---|---|
| F1 | `POST /api/v1/analysis` | Procesa los 3 dispositivos recibidos en el cuerpo (no almacena) |
| F2 | `POST /api/v1/readings` | Registra un dispositivo (409 si ya existe) |
| F3 | `GET /api/v1/readings`, `GET /api/v1/readings/{device}` | Consulta lo registrado |
| F3 | `GET /api/v1/analysis` | Procesa lo acumulado; 422 si faltan datos |
| F4 | `PUT /api/v1/readings/{device}` | Reemplazo completo (distance y message obligatorios) |
| F5 | `PATCH /api/v1/readings/{device}` | Actualización parcial (al menos un campo) |
| F6 | `DELETE /api/v1/readings/{device}` | Elimina solo ese dispositivo |

Dispositivos: `star-labs` (-500,-200), `ccpd` (100,-100), `star-satellite` (500,100).

### Ejemplos
```bash
curl -X POST localhost:8080/api/v1/readings -H "Content-Type: application/json" \
  -d '{"device":"ccpd","distance":300,"message":["","es","un",""]}'

curl -X PATCH localhost:8080/api/v1/readings/ccpd -H "Content-Type: application/json" -d '{"distance":299.5}'

curl localhost:8080/api/v1/analysis
```
Respuesta de análisis: `{"position":{"x":100.0,"y":200.0},"message":"este es un mensaje"}`

Errores: `400` datos inválidos · `404` dispositivo desconocido/sin registro · `409` ya registrado ·
`422` datos insuficientes o mensaje irreconstruible. Formato: `{status, error, message, timestamp}`.

## Decisiones y supuestos
- **Trilateración**: se restan las ecuaciones de los círculos para obtener un sistema lineal 2x2 (Cramer); resultado redondeado a 2 decimales.
- **Mensaje**: cada fragmento es una lista de palabras; `""` = palabra no recibida y se ignora. Los fragmentos más largos se recortan por el inicio hasta igualar al más corto (desfase) y por cada posición se toma la palabra no vacía. Si una posición no la recibió nadie o dos fragmentos discrepan → 422.
- Distancia ≥ 0 y finita; fragmento no nulo ni vacío (sus elementos tampoco pueden ser `null`).
- Cada dispositivo tiene como máximo una lectura; los datos registrados siempre están completos (distancia + fragmento), por lo que en F5 los campos omitidos conservan su valor.
- Consistencia: las operaciones de escritura son atómicas (`synchronized`) y el repositorio es thread-safe.

## Subir a GitHub
```bash
git init && git add . && git commit -m "Operación Alquimia - solución DDD + hexagonal"
git branch -M main
git remote add origin https://github.com/<tu-usuario>/operacion-alquimia.git
git push -u origin main
```
