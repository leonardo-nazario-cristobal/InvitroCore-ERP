package com.invitrocore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "compras")
public class Compra {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private OffsetDateTime fecha;

   @Column(nullable = false, precision = 10, scale = 2)
   private BigDecimal total = BigDecimal.ZERO;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "id_proveedor", nullable = false)
   private Proveedor proveedor;

   @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<DetalleCompra> detalles = new ArrayList<>();

   protected Compra() {
   }

   public Compra(Proveedor proveedor) {
      this.proveedor = proveedor;
      this.fecha = OffsetDateTime.now();
      this.total = BigDecimal.ZERO;
   }

   public Long getId() {
      return id;
   }

   public OffsetDateTime getFecha() {
      return fecha;
   }

   public BigDecimal getTotal() {
      return total;
   }

   public Proveedor getProveedor() {
      return proveedor;
   }

   public List<DetalleCompra> getDetalles() {
      return Collections.unmodifiableList(detalles);
   }

   public void agregarDetalle(DetalleCompra detalle) {
      detalles.add(detalle);
      recalcularTotal();
   }

   private void recalcularTotal() {
      this.total = detalles.stream()
            .map(DetalleCompra::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
   }
}
