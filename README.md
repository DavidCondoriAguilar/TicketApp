# Rave-Tix - Sistema de Gestión de Entradas para Eventos

## Descripción
Rave-Tix es una aplicación backend para la gestión de venta de entradas para eventos, con énfasis en eventos musicales y festivales. La aplicación permite a los usuarios registrarse, comprar entradas, realizar pagos y gestionar su historial de eventos.

## Características Principales

### 1. Base de Datos
- **BaseEntity**: Clase base abstracta que proporciona:
  - ID generado automáticamente (UUID)
  - Fecha de creación y actualización automáticas
  - Control de concurrencia optimista con campo `@Version`

### 2. Validaciones
- **Validaciones integradas** con Jakarta Bean Validation
- **Validaciones personalizadas**:
  - `@FutureAfterNow`: Valida fechas futuras con offset configurable
  - `@ValidPhoneNumber`: Valida formatos de números telefónicos internacionales
  - Restricciones de tamaño, formato y unicidad

### 3. Estructura del Proyecto
```
src/main/java/com/tickets/ravetix/
├── config/           # Configuraciones de la aplicación
├── controller/       # Controladores REST
├── dto/              # Objetos de Transferencia de Datos
│   ├── request/      # DTOs para peticiones
│   └── response/     # DTOs para respuestas
├── entity/           # Entidades JPA
├── enums/            # Enumeraciones
├── exception/        # Manejo de excepciones
├── repository/       # Repositorios de datos
├── service/          # Lógica de negocio
└── validation/       # Validaciones personalizadas
```

### 4. Entidades Principales

#### User (Usuario)
- Almacena información de los usuarios del sistema
- Validaciones para email, teléfono y nombre
- Relaciones con Tickets, Pagos e Historial

#### Event (Evento)
- Gestiona la información de los eventos
- Control de fechas y capacidad
- Relación con zonas y tickets

#### Ticket
- Representa una entrada para un evento
- Incluye información de precio y fecha de compra
- Relación con usuario, evento y zona

#### Zone (Zona)
- Define áreas específicas dentro de un evento
- Control de capacidad y precios
- Beneficios específicos por zona

## Mejoras Implementadas

### 1. BaseEntity
- Centraliza campos comunes (ID, fechas)
- Facilita auditoría y seguimiento
- Reduce duplicación de código

### 2. Validaciones
- **Validaciones de Dominio**:
  - Fechas futuras con `@FutureAfterNow`
  - Números de teléfono con `@ValidPhoneNumber`
  - Formatos de email y restricciones de tamaño

### 3. Documentación
- JavaDoc completo
- Comentarios explicativos
- Estructura clara del proyecto

## Próximos Pasos
1. Implementar autenticación JWT
2. Agregar pruebas unitarias y de integración
3. Implementar manejo de archivos para imágenes de eventos
4. Desarrollar endpoints para búsquedas avanzadas
5. Implementar sistema de notificaciones

## Tecnologías Utilizadas
- Java 17
- Spring Boot 3.x
- Hibernate/JPA
- H2 Database (desarrollo) / PostgreSQL (producción)
- Maven
- Lombok
- Jakarta Bean Validation

## Configuración
1. Clonar el repositorio
2. Configurar las propiedades de la base de datos en `application.properties`
3. Ejecutar con Maven: `mvn spring-boot:run`

## Contribución
Las contribuciones son bienvenidas. Por favor, lea las guías de contribución antes de enviar pull requests.
