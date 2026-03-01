-- =====================================================
-- 1. LIMPIEZA
-- =====================================================

DROP TABLE IF EXISTS movimientos_inventario CASCADE;
DROP TABLE IF EXISTS detalle_venta CASCADE;
DROP TABLE IF EXISTS ventas CASCADE;
DROP TABLE IF EXISTS detalle_compra CASCADE;
DROP TABLE IF EXISTS compras CASCADE;
DROP TABLE IF EXISTS productos CASCADE;
DROP TABLE IF EXISTS proveedores CASCADE;
DROP TABLE IF EXISTS categorias CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

DROP TYPE IF EXISTS tipo_rol CASCADE;
DROP TYPE IF EXISTS tipo_movimiento CASCADE;
DROP TYPE IF EXISTS tipo_pago CASCADE;
DROP TYPE IF EXISTS estado_venta CASCADE;

-- =====================================================
-- 2. ENUMS
-- =====================================================

CREATE TYPE tipo_rol AS ENUM ('admin','cajero','compras','ventas');
CREATE TYPE tipo_movimiento AS ENUM ('entrada','salida','ajuste');
CREATE TYPE tipo_pago AS ENUM ('efectivo','tarjeta','transferencia','credito');
CREATE TYPE estado_venta AS ENUM ('pendiente','completada','cancelada');

-- =====================================================
-- 3. FUNCION GLOBAL ACTUALIZAR TIMESTAMP
-- =====================================================

CREATE OR REPLACE FUNCTION actualizar_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.actualizado_en = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- 4. USUARIOS
-- =====================================================

CREATE TABLE usuarios (
   id BIGSERIAL PRIMARY KEY,
   nombre VARCHAR(100) NOT NULL,
   correo VARCHAR(120) UNIQUE NOT NULL
      CHECK (correo ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
   password TEXT NOT NULL CHECK (length(password) >= 60),
   rol tipo_rol NOT NULL DEFAULT 'cajero',
   activo BOOLEAN NOT NULL DEFAULT TRUE,
   creado_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),
   actualizado_en TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER trg_update_usuario
BEFORE UPDATE ON usuarios
FOR EACH ROW
EXECUTE FUNCTION actualizar_timestamp();

-- =====================================================
-- 5. CATEGORIAS
-- =====================================================

CREATE TABLE categorias (
   id BIGSERIAL PRIMARY KEY,
   nombre VARCHAR(100) NOT NULL UNIQUE,
   descripcion TEXT,
   creado_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),
   actualizado_en TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER trg_update_categoria
BEFORE UPDATE ON categorias
FOR EACH ROW
EXECUTE FUNCTION actualizar_timestamp();

-- =====================================================
-- 6. PROVEEDORES
-- =====================================================

CREATE TABLE proveedores (
   id BIGSERIAL PRIMARY KEY,
   nombre VARCHAR(150) NOT NULL,
   telefono VARCHAR(30),
   correo VARCHAR(120)
      CHECK (correo IS NULL OR
            correo ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
   creado_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),
   actualizado_en TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER trg_update_proveedor
BEFORE UPDATE ON proveedores
FOR EACH ROW
EXECUTE FUNCTION actualizar_timestamp();

-- =====================================================
-- 7. PRODUCTOS
-- =====================================================

CREATE TABLE productos (
   id BIGSERIAL PRIMARY KEY,
   codigo_barras VARCHAR(50) UNIQUE,
   nombre VARCHAR(150) NOT NULL UNIQUE,
   descripcion TEXT,
   precio NUMERIC(10,2) NOT NULL CHECK (precio > 0),
   costo_promedio NUMERIC(10,2) DEFAULT 0 CHECK (costo_promedio >= 0),
   stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
   stock_minimo INT NOT NULL DEFAULT 5 CHECK (stock_minimo >= 0),
   id_categoria BIGINT,
   activo BOOLEAN NOT NULL DEFAULT TRUE,
   creado_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),
   actualizado_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),

   CONSTRAINT fk_producto_categoria
      FOREIGN KEY (id_categoria)
      REFERENCES categorias(id)
      ON DELETE SET NULL
);

CREATE TRIGGER trg_update_producto
BEFORE UPDATE ON productos
FOR EACH ROW
EXECUTE FUNCTION actualizar_timestamp();

-- =====================================================
-- 8. COMPRAS
-- =====================================================

CREATE TABLE compras (
   id BIGSERIAL PRIMARY KEY,
   fecha TIMESTAMPTZ NOT NULL DEFAULT NOW(),
   total NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (total >= 0),
   id_proveedor BIGINT NOT NULL,
   creado_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),

   CONSTRAINT fk_compra_proveedor
      FOREIGN KEY (id_proveedor)
      REFERENCES proveedores(id)
);

-- =====================================================
-- 9. DETALLE_COMPRA
-- =====================================================

CREATE TABLE detalle_compra (
   id BIGSERIAL PRIMARY KEY,
   id_compra BIGINT NOT NULL,
   id_producto BIGINT NOT NULL,
   cantidad INT NOT NULL CHECK (cantidad > 0),
   costo_unitario NUMERIC(10,2) NOT NULL CHECK (costo_unitario >= 0),

   CONSTRAINT fk_detalle_compra_compra
      FOREIGN KEY (id_compra)
      REFERENCES compras(id)
      ON DELETE CASCADE,

   CONSTRAINT fk_detalle_compra_producto
      FOREIGN KEY (id_producto)
      REFERENCES productos(id),

   CONSTRAINT uq_detalle_compra UNIQUE (id_compra, id_producto)
);

-- =====================================================
-- 10. VENTAS
-- =====================================================

CREATE TABLE ventas (
   id BIGSERIAL PRIMARY KEY,
   fecha TIMESTAMPTZ NOT NULL DEFAULT NOW(),
   total NUMERIC(10,2) NOT NULL CHECK (total >= 0),
   metodo_pago tipo_pago NOT NULL DEFAULT 'efectivo',
   estado estado_venta NOT NULL DEFAULT 'pendiente',
   id_usuario BIGINT NOT NULL,
   creado_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),

   CONSTRAINT fk_venta_usuario
      FOREIGN KEY (id_usuario)
      REFERENCES usuarios(id)
);

-- =====================================================
-- 11. DETALLE_VENTA
-- =====================================================

CREATE TABLE detalle_venta (
   id BIGSERIAL PRIMARY KEY,
   id_venta BIGINT NOT NULL,
   id_producto BIGINT NOT NULL,
   cantidad INT NOT NULL CHECK (cantidad > 0),
   precio_unitario NUMERIC(10,2) NOT NULL CHECK (precio_unitario >= 0),

   subtotal NUMERIC(10,2)
        GENERATED ALWAYS AS (cantidad * precio_unitario) STORED,

   CONSTRAINT fk_detalle_venta_venta
      FOREIGN KEY (id_venta)
      REFERENCES ventas(id)
      ON DELETE CASCADE,

   CONSTRAINT fk_detalle_venta_producto
      FOREIGN KEY (id_producto)
      REFERENCES productos(id),

   CONSTRAINT uq_detalle_venta UNIQUE (id_venta, id_producto)
);

-- =====================================================
-- 12. MOVIMIENTOS INVENTARIO
-- =====================================================

CREATE TABLE movimientos_inventario (
   id BIGSERIAL PRIMARY KEY,
   id_producto BIGINT NOT NULL,
   tipo tipo_movimiento NOT NULL,
   cantidad INT NOT NULL CHECK (cantidad > 0),
   motivo TEXT,
   id_usuario BIGINT,
   fecha TIMESTAMPTZ NOT NULL DEFAULT NOW(),

   CONSTRAINT fk_mov_producto
      FOREIGN KEY (id_producto)
      REFERENCES productos(id)
      ON DELETE CASCADE,

   CONSTRAINT fk_mov_usuario
      FOREIGN KEY (id_usuario)
      REFERENCES usuarios(id)
      ON DELETE SET NULL
);

-- =====================================================
-- 13. INDICES PROFESIONALES
-- =====================================================

CREATE INDEX idx_usuario_correo ON usuarios(correo);
CREATE INDEX idx_producto_categoria ON productos(id_categoria);
CREATE INDEX idx_producto_codigo ON productos(codigo_barras);
CREATE INDEX idx_producto_nombre ON productos(nombre);
CREATE INDEX idx_mov_producto ON movimientos_inventario(id_producto);
CREATE INDEX idx_mov_fecha ON movimientos_inventario(fecha);
CREATE INDEX idx_mov_usuario ON movimientos_inventario(id_usuario);
CREATE INDEX idx_venta_fecha ON ventas(fecha);
CREATE INDEX idx_venta_usuario ON ventas(id_usuario);
CREATE INDEX idx_ventas_fecha_usuario ON ventas(fecha, id_usuario);
CREATE INDEX idx_compra_fecha ON compras(fecha);
CREATE INDEX idx_compra_proveedor ON compras(id_proveedor);
CREATE INDEX idx_detalle_compra_compra ON detalle_compra(id_compra);
CREATE INDEX idx_detalle_compra_producto ON detalle_compra(id_producto);
CREATE INDEX idx_detalle_venta_venta ON detalle_venta(id_venta);
CREATE INDEX idx_detalle_venta_producto ON detalle_venta(id_producto);