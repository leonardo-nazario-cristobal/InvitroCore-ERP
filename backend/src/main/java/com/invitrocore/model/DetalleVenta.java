package com.invitrocore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_venta", uniqueConstraints = @UniqueConstraint(columnNames = { "id_venta", "id_producto" }))
public class DetalleVenta {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "id_venta", nullable = false)
   private Venta venta;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "id_producto", nullable = false)
   private Producto producto;

   @Column(nullable = false)
   private Integer cantidad;

   @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
   private BigDecimal precioUnutario;

   protected DetalleVenta() {
   }

   public DetalleVenta(Venta venta, Producto producto,
         Integer cantidad, BigDecimal precioUnitario) {
      this.venta = venta;
      this.producto = producto;
      this.cantidad = cantidad;
      this.precioUnutario = precioUnitario;
   }

   public Long getId() {
      return id;
   }

   public Venta getVenta() {
      return venta;
   }

   public Producto getProducto() {
      return producto;
   }

   public Integer getCantidad() {
      return cantidad;
   }

   public BigDecimal getPrecioUnitario() {
      return precioUnutario;
   }

   public BigDecimal getSubtotal() {
      return precioUnutario.multiply(BigDecimal.valueOf(cantidad));
   }
}
