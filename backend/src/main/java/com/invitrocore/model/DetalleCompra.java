package com.invitrocore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_compra", uniqueConstraints = @UniqueConstraint(columnNames = { "id_compra", "id_producto" }))
public class DetalleCompra {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "id_compra", nullable = false)
   private Compra compra;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "id_producto", nullable = false)
   private Producto producto;

   @Column(nullable = false)
   private Integer cantidad;

   @Column(name = "costo_unitario", nullable = false, precision = 10, scale = 2)
   private BigDecimal costoUnitario;

   protected DetalleCompra() {
   }

   public DetalleCompra(Compra compra, Producto producto,
         Integer cantidad, BigDecimal costoUnitario) {

      this.compra = compra;
      this.producto = producto;
      this.cantidad = cantidad;
      this.costoUnitario = costoUnitario;
   }

   public Long getId() {
      return id;
   }

   public Compra getCompra() {
      return compra;
   }

   public Producto getProducto() {
      return producto;
   }

   public Integer getCantidad() {
      return cantidad;
   }

   public BigDecimal getCostoUnitario() {
      return costoUnitario;
   }

   public BigDecimal getSubtotal() {
      return costoUnitario.multiply(BigDecimal.valueOf(cantidad));
   }
}
