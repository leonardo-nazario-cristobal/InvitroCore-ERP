package com.invitrocore.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "id_producto", nullable = false)
   private Producto producto;

   @Column(nullable = false)
   private TipoMovimiento tipo;

   @Column(nullable = false)
   private Integer cantidad;

   @Column(length = 255)
   private String motivo;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "id_usuario")
   private Usuario usuario;

   @Column(name = "creado_en", nullable = false, updatable = false)
   private OffsetDateTime creadoEn;

   protected MovimientoInventario() {
   }

   public MovimientoInventario(Producto producto, TipoMovimiento tipo,
         Integer cantidad, String motivo, Usuario usuario) {

      validarCantidad(cantidad);
      this.producto = producto;
      this.tipo = tipo;
      this.cantidad = cantidad;
      this.motivo = motivo;
      this.usuario = usuario;
   }

   @PrePersist
   protected void onCreate() {
      this.creadoEn = OffsetDateTime.now();
   }

   public Long getId() {
      return id;
   }

   public Producto getProducto() {
      return producto;
   }

   public TipoMovimiento getTipo() {
      return tipo;
   }

   public Integer getCantidad() {
      return cantidad;
   }

   public String getMotivo() {
      return motivo;
   }

   public Usuario getUsuario() {
      return usuario;
   }

   public OffsetDateTime getCreadoEn() {
      return creadoEn;
   }

   public boolean esEntrada() {
      return this.tipo == TipoMovimiento.ENTRADA;
   }

   public boolean esSalida() {
      return this.tipo == TipoMovimiento.SALIDA;
   }

   public boolean esAjuste() {
      return this.tipo == TipoMovimiento.AJUSTE;
   }

   private void validarCantidad(Integer cantidad) {
      if (cantidad == null || cantidad <= 0) {
         throw new IllegalArgumentException(
               "La cantidad del movimiento debe ser mayor a 0");
      }
   }
}
