# NexusMarket – API REST demostrable (Spring Boot + H2)

Versión ampliada del proyecto original para ejecutar y demostrar los flujos funcionales del PDF de especificación. Mantiene Java 17, Spring Boot 3.3.4, Spring Data JPA y H2, y añade API REST, autenticación HTTP Basic, autorización por roles, persistencia en archivo, devoluciones por artículo, reportes y colección Postman.

## 1. Requisitos
- JDK 17 o superior.
- Apache Maven 3.8+ instalado (`mvn -v`).
- Postman.

## 2. Ejecutar
Desde la carpeta `nexusmarket-service`:

```bash
mvn clean test
mvn spring-boot:run
```

La API queda en `http://localhost:8080`. La base H2 se guarda en `./data/nexusmarket.mv.db`; **no se borra al detener ni reiniciar Spring Boot**. No uses `ddl-auto=create` ni elimines la carpeta `data` si necesitas conservar la evidencia.

Consola H2: `http://localhost:8080/h2-console` (solo ADMIN). JDBC URL: `jdbc:h2:file:./data/nexusmarket`; usuario `sa`; contraseña vacía.

## 3. Usuarios de demostración
Autenticación HTTP Basic. Credenciales de demostración (no usar en producción):

| Rol | Usuario | Contraseña |
|---|---|---|
| Administrador | `admin` | `admin123` |
| Comprador 1 | `buyer1` | `buyer123` |
| Comprador 2 | `buyer2` | `buyer123` |
| Comprador 3 | `buyer3` | `buyer123` |
| Vendedor 1 | `seller1` | `seller123` |
| Vendedor 2 | `seller2` | `seller123` |
| Operador logístico | `logistics` | `log123` |
| Supervisor | `supervisor` | `super123` |

**Aviso:** los usuarios técnicos y sus contraseñas están definidos en memoria para facilitar una demostración académica; compradores, vendedores, productos, inventario, carritos, pedidos, envíos, devoluciones y movimientos sí se guardan en H2. No es una solución de identidad para producción.

## 4. Datos iniciales
Al iniciar por primera vez se crean tres compradores, dos vendedores y seis productos (tres físicos y tres digitales). El cargador solo inserta datos cuando la tabla de compradores está vacía.

| ID inicial | Producto | Tipo | Vendedor | Stock inicial |
|---:|---|---|---|---:|
| 1 | Portátil Nexus 14 | PHYSICAL | seller1 | 8 |
| 2 | Mouse inalámbrico | PHYSICAL | seller1 | 12 |
| 3 | Teclado mecánico | PHYSICAL | seller1 | 6 |
| 4 | Licencia Office | DIGITAL | seller2 | No aplica |
| 5 | Curso Java | DIGITAL | seller2 | No aplica |
| 6 | Antivirus 1 año | DIGITAL | seller2 | No aplica |

Los IDs anteriores son los esperados en una base nueva. Si ya hay datos, consulta primero `GET /api/products`.

## 5. Flujos de demostración en Postman
Importa `postman/NexusMarket.postman_collection.json`. La colección está ordenada para una base recién creada. Incluye credenciales por petición, captura automática de IDs, GET de verificación, prueba de stock insuficiente, compra, despacho, devolución de un solo artículo, reembolso, reportes y pruebas negativas de autorización.

Secuencia resumida:
1. `GET /api/buyers` como admin: confirmar los tres compradores.
2. `GET /api/products` como comprador: identificar los seis productos y sus vendedores.
3. Como buyer1: `GET /api/cart`; intentar agregar 99 portátiles (debe responder **400**); luego agregar portátil, mouse, licencia y curso; consultar el carrito por GET.
4. Como buyer1: `POST /api/checkout`; captura el ID del pedido y el ID de la primera línea física. `GET /api/orders/{id}` confirma pago y los cuatro artículos.
5. Como logistics: `POST /api/orders/{id}/dispatch` y `POST /api/orders/{id}/deliver`; después `GET /api/shipments/{id}`.
6. Como buyer1: `POST /api/orders/{id}/returns` para devolver solo el portátil. Como admin: aprobar y procesar reembolso. Consultar `GET /api/my/returns` (antes de cambiar de rol), `GET /api/returns`, `GET /api/orders/{id}` e inventario como seller1.
7. Como seller1 y seller2: consultar reportes. Los vendedores solo reciben sus propios datos. Supervisor/admin pueden consultar vistas consolidadas.
8. Reiniciar la aplicación sin borrar `data/`. Repetir GET de pedidos, devoluciones, inventario y reportes; deben conservarse.

**Compra/pago:** el endpoint `/api/checkout` realiza una confirmación de pago simulada (`SIMULATED-PAID...`) dentro de la aplicación. No integra una pasarela bancaria ni mueve dinero real. El PDF requiere gestionar el pago, pero no identifica proveedor de pagos; por eso esta versión demuestra el flujo interno sin afirmar que procesa una transacción financiera real.

## 6. Endpoints principales

| Método | Ruta | Rol permitido |
|---|---|---|
| GET | `/api/health` | Cualquier usuario autenticado |
| GET / POST | `/api/buyers` | ADMIN/SUPERVISOR (GET); ADMIN (POST) |
| GET / POST | `/api/products` | Autenticado (GET); SELLER (POST) |
| GET | `/api/inventory/{productId}` | SELLER, ADMIN, SUPERVISOR, LOGISTICS_OPERATOR |
| POST | `/api/inventory/{productId}/restock` | SELLER propietario |
| GET / POST | `/api/cart`, `/api/cart/items` | BUYER |
| POST | `/api/checkout` | BUYER |
| GET | `/api/invoices/{orderId}` | BUYER (solo propio), ADMIN, SUPERVISOR |
| GET | `/api/orders`, `/api/orders/{id}` | BUYER (solo propios), ADMIN, SUPERVISOR, LOGISTICS_OPERATOR |
| POST | `/api/orders/{id}/dispatch`, `/deliver` | LOGISTICS_OPERATOR |
| GET | `/api/shipments/{orderId}` | BUYER (solo propio), ADMIN, SUPERVISOR, LOGISTICS_OPERATOR |
| POST | `/api/orders/{id}/returns` | BUYER propietario del pedido |
| GET | `/api/my/returns` | BUYER (solo propias) |
| GET | `/api/returns` | ADMIN/SUPERVISOR |
| POST | `/api/returns/{id}/approve`, `/refund` | ADMIN |
| GET | `/api/reports/sales`, `/inventory`, `/movements`, `/dashboard` | SELLER (solo propio), ADMIN, SUPERVISOR |

## 7. Reglas y resultados esperados
- No se aceptan cantidades cero/negativas ni cantidades físicas superiores al stock disponible.
- Los productos digitales no reservan stock ni generan envío físico.
- El checkout valida de nuevo el inventario, descuenta las unidades físicas y registra el movimiento `SALE`.
- La devolución se asocia a una línea de pedido y vendedor específicos. Al aprobarla, cambia únicamente el estado de esa línea, repone únicamente esa cantidad y registra `RETURN`.
- El reembolso corresponde al precio unitario por la cantidad aprobada, no al total del pedido. Los demás artículos no se alteran.
- Los vendedores solo pueden consultar/modificar inventario de productos propios y sus reportes se filtran por su usuario.
- Acceso sin autenticación: HTTP 401. Rol incorrecto: HTTP 403. Regla de negocio/datos inválidos: HTTP 400. Recurso inexistente: HTTP 404.

## 8. Matriz de seguridad para evidencias

| Prueba | Usuario | Resultado esperado |
|---|---|---|
| Crear comprador | admin | 201/200 exitoso |
| Crear producto | seller1 | 200 exitoso |
| Crear producto | buyer1 | 403 |
| Agregar producto al carrito | buyer1 | 200 exitoso |
| Consultar compradores | buyer1 | 403 |
| Despachar pedido | logistics | 200 exitoso |
| Despachar pedido | seller1 | 403 |
| Aprobar devolución | admin | 200 exitoso |
| Aprobar devolución | buyer1 | 403 |
| Ver reportes propios | seller1 | 200, solo datos seller1 |
| Consultar reportes | buyer1 | 403 |

## 9. Reinicio y persistencia
1. Detén el proceso (`Ctrl+C`).
2. No elimines `data/` ni los archivos `*.mv.db`.
3. Ejecuta nuevamente `mvn spring-boot:run`.
4. Autentícate y repite los GET de pedidos, inventario, devoluciones y reportes.

Los datos de negocio permanecen en H2 archivo. Las credenciales HTTP Basic permanecen disponibles porque se configuran al iniciar la aplicación. Para limpiar la demostración, detén la app y elimina manualmente `data/`; al volver a iniciar se cargará el escenario inicial.

## 10. Alcance y consideraciones
- La API y la autenticación por roles son una ampliación técnica para hacer demostrable el proyecto; el PDF original excluye tecnologías y autenticación técnica de su alcance funcional.
- El modelo de muestra asocia cada producto a un vendedor, tal como corresponde al modelo comercial; no asigna productos a compradores. Los tres compradores se precargan y cualquiera puede comprar productos publicados.
- En esta versión el pedido conserva cuatro líneas independientes. La devolución modifica solo la línea devuelta, no el resto del pedido.
- Los métodos de pago y credenciales son simulados para fines académicos. No se recomienda exponer H2 Console ni credenciales de ejemplo fuera del entorno local.
