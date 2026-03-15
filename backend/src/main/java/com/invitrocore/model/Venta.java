package com.invitrocore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "ventas")
public class Venta {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private OffsetDateTime fecha;

   @Column(nullable = false, precision = 10, scale = 2)
   private BigDecimal total = BigDecimal.ZERO;

   @Column(name = "metodo_pago", nullable = false)
   private MetodoPago metodoPago;

   @Column(nullable = false)
   private EstadoVenta estado;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "id_usuario", nullable = false)
   private Usuario usuario;

   @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<DetalleVenta> detalles = new ArrayList<>();

   protected Venta() {
   }

   public Venta(MetodoPago metodoPago, Usuario usuario) {
      this.metodoPago = metodoPago;
      this.usuario = usuario;
      this.fecha = OffsetDateTime.now();
      this.total = BigDecimal.ZERO;
      this.estado = EstadoVenta.COMPLETADA;
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

   public MetodoPago getMetodoPago() {
      return metodoPago;
   }

   public EstadoVenta getEstado() {
      return estado;
   }

   public Usuario getUsuario() {
      return usuario;
   }

   public List<DetalleVenta> getDetalles() {
      return Collections.unmodifiableList(detalles);
   }

   public void agregarDetalle(DetalleVenta detalle) {
      detalles.add(detalle);
      recalcularTotal();
   }

   public void cancelar() {
      if (this.estado == EstadoVenta.CANCELADA) {
         throw new IllegalArgumentException("La venta ya está cancelada");
      }

      this.estado = EstadoVenta.CANCELADA;
   }

   public boolean estaCancelada() {
      return this.estado == EstadoVenta.CANCELADA;
   }

   private void recalcularTotal() {
      this.total = detalles.stream()
            .map(DetalleVenta::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
   }
}