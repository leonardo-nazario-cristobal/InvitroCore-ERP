/* Perfil en el sidebar */
const nombre = localStorage.getItem("nombre") || "Usuario";
const rolUsuario = localStorage.getItem("rol") || "";

document.getElementById("footerNombre").textContent = nombre;
document.getElementById("footerRol").textContent = rolUsuario;

const iniciales = nombre
   .split(" ")
   .map((p) => p[0])
   .join("")
   .substring(0, 2)
   .toUpperCase();
document.getElementById("avatarInicial").textContent = iniciales;

/* Logout */
document.getElementById("btnLogout").addEventListener("click", async () => {
   const refreshToken = localStorage.getItem("refreshToken");
   try {
      await apiFetch("/api/auth/logout", {
         method: "POST",
         body: JSON.stringify({ refreshToken }),
      });
   } finally {
      localStorage.clear();
      window.location.href = "index.html";
   }
});

/* Panel lateral */
const panelLateral = document.getElementById("panelLateral");
const panelOverlay = document.getElementById("panelOverlay");
const alertaPanel = document.getElementById("alertaPanel");
const formVenta = document.getElementById("formVenta");

let productos = [];

function abrirPanel() {
   panelLateral.classList.add("activo");
   panelOverlay.classList.add("activo");
   alertaPanel.classList.add("d-none");
   document.getElementById("listaDetalles").innerHTML = "";
   document.getElementById("sinDetalles").style.display = "block";
   actualizarTotal();
}

function cerrarPanel() {
   panelLateral.classList.remove("activo");
   panelOverlay.classList.remove("activo");
   formVenta.reset();
   document.getElementById("listaDetalles").innerHTML = "";
   actualizarTotal();
   alertaPanel.classList.add("d-none");
}

document.getElementById("panelClose").addEventListener("click", cerrarPanel);
document
   .getElementById("btnCancelarPanel")
   .addEventListener("click", cerrarPanel);
document.getElementById("panelOverlay").addEventListener("click", cerrarPanel);

/* Nueva venta */
document.getElementById("btnNuevaVenta").addEventListener("click", abrirPanel);

/* Cargar productos */
async function cargarProductos() {
   try {
      const res = await apiFetch("/api/productos?pagina=0&tamanio=500");
      const page = await res.json();
      productos = page.content;
   } catch (error) {
      console.error("Error cargando productos:", error);
   }
}

/* Agregar fila de detalle */
document
   .getElementById("btnAgregarDetalle")
   .addEventListener("click", agregarDetalle);

function agregarDetalle() {
   document.getElementById("sinDetalles").style.display = "none";

   const opciones = productos
      .map(
         (p) =>
            `<option value="${p.id}" data-precio="${p.precio}">${p.nombre} (stock: ${p.stock})</option>`,
      )
      .join("");

   const fila = document.createElement("div");
   fila.className = "detalle-row";
   fila.innerHTML = `
      <div class="d-flex align-items-center gap-2">
         <select class="form-select select-producto" style="flex: 2;">
            <option value="">Selecciona producto</option>
            ${opciones}
         </select>
         <input type="number" class="form-control input-cantidad"
                placeholder="Cant." min="1" value="1" style="width: 80px;">
         <input type="number" class="form-control input-precio"
                placeholder="Precio" min="0.01" step="0.01" style="width: 110px;">
         <button type="button" class="btn-remove-detalle"
                 onclick="this.closest('.detalle-row').remove(); actualizarTotal(); verificarDetalles();">✕</button>
      </div>
      <div class="subtotal-fila text-muted mt-1" style="font-size: 12px; text-align: right;">
         Subtotal: $0.00
      </div>
   `;

   fila
      .querySelector(".select-producto")
      .addEventListener("change", function () {
         const option = this.options[this.selectedIndex];
         const precio = option.getAttribute("data-precio");
         if (precio) fila.querySelector(".input-precio").value = precio;
         actualizarSubtotal(fila);
      });

   fila
      .querySelector(".input-cantidad")
      .addEventListener("input", () => actualizarSubtotal(fila));
   fila
      .querySelector(".input-precio")
      .addEventListener("input", () => actualizarSubtotal(fila));

   document.getElementById("listaDetalles").appendChild(fila);
   actualizarTotal();
}

function actualizarSubtotal(fila) {
   const cantidad =
      parseFloat(fila.querySelector(".input-cantidad").value) || 0;
   const precio = parseFloat(fila.querySelector(".input-precio").value) || 0;
   const subtotal = cantidad * precio;
   fila.querySelector(".subtotal-fila").textContent =
      `Subtotal: $${subtotal.toLocaleString("es-MX", { minimumFractionDigits: 2 })}`;
   actualizarTotal();
}

function actualizarTotal() {
   const filas = document.querySelectorAll(".detalle-row");
   let total = 0;
   filas.forEach((fila) => {
      const cantidad =
         parseFloat(fila.querySelector(".input-cantidad").value) || 0;
      const precio = parseFloat(fila.querySelector(".input-precio").value) || 0;
      total += cantidad * precio;
   });
   document.getElementById("totalVenta").textContent =
      `$${total.toLocaleString("es-MX", { minimumFractionDigits: 2 })}`;
}

function verificarDetalles() {
   const filas = document.querySelectorAll(".detalle-row");
   document.getElementById("sinDetalles").style.display =
      filas.length === 0 ? "block" : "none";
}

/* ── Paginación ──────────────────────────────── */
const TAMANIO = 15;
let callbackPaginaActual = null;

function renderPaginacion(page, callbackPagina) {
   callbackPaginaActual = callbackPagina;

   let contenedor = document.getElementById("paginacion");
   if (!contenedor) {
      contenedor = document.createElement("div");
      contenedor.id = "paginacion";
      contenedor.className =
         "d-flex justify-content-between align-items-center px-3 py-2 border-top";
      document.querySelector(".table-card").appendChild(contenedor);
   }

   const { number, totalPages, totalElements, size } = page;
   const desde = totalElements === 0 ? 0 : number * size + 1;
   const hasta = Math.min((number + 1) * size, totalElements);

   if (totalPages <= 1) {
      contenedor.innerHTML = `
         <span class="text-muted" style="font-size: 12px;">
            ${totalElements} registros
         </span>`;
      return;
   }

   const rango = 2;
   const inicio = Math.max(0, number - rango);
   const fin = Math.min(totalPages - 1, number + rango);

   let botonesPaginas = "";
   if (inicio > 0)
      botonesPaginas += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
   for (let i = inicio; i <= fin; i++) {
      botonesPaginas += `
         <li class="page-item ${i === number ? "active" : ""}">
            <button class="page-link" onclick="irAPagina(${i})">${i + 1}</button>
         </li>`;
   }
   if (fin < totalPages - 1)
      botonesPaginas += `<li class="page-item disabled"><span class="page-link">...</span></li>`;

   contenedor.innerHTML = `
      <span class="text-muted" style="font-size: 12px;">
         Mostrando ${desde}–${hasta} de ${totalElements}
      </span>
      <nav>
         <ul class="pagination pagination-sm mb-0">
            <li class="page-item ${number === 0 ? "disabled" : ""}">
               <button class="page-link" onclick="irAPagina(${number - 1})">←</button>
            </li>
            ${botonesPaginas}
            <li class="page-item ${number === totalPages - 1 ? "disabled" : ""}">
               <button class="page-link" onclick="irAPagina(${number + 1})">→</button>
            </li>
         </ul>
      </nav>
   `;
}

function irAPagina(pagina) {
   if (callbackPaginaActual) callbackPaginaActual(pagina);
}

/* Filtro estado */
document
   .getElementById("filtroEstado")
   .addEventListener("change", () => cargarVentas(0));

/* Cargar ventas con paginación */
async function cargarVentas(pagina = 0) {
   try {
      const filtro = document.getElementById("filtroEstado").value;
      const endpoint = filtro
         ? `/api/ventas/estado?valor=${filtro}&pagina=${pagina}&tamanio=${TAMANIO}`
         : `/api/ventas?pagina=${pagina}&tamanio=${TAMANIO}`;

      const res = await apiFetch(endpoint);
      const page = await res.json();

      document.getElementById("totalVentas").textContent =
         `${page.totalElements} ventas`;

      const tbody = document.getElementById("tablaVentas");

      if (page.content.length === 0) {
         tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-3">
            Sin ventas</td></tr>`;
         renderPaginacion(page, cargarVentas);
         return;
      }

      const puedeCancel = ["admin", "ventas"].includes(rolUsuario);

      tbody.innerHTML = page.content
         .map(
            (v) => `
         <tr>
            <td>#${v.id}</td>
            <td>${new Date(v.fecha).toLocaleDateString("es-MX")}</td>
            <td>${v.nombreUsuario}</td>
            <td><span class="badge-${v.metodoPago}">${v.metodoPago}</span></td>
            <td><strong>$${v.total.toLocaleString("es-MX")}</strong></td>
            <td><span class="badge-${v.estado}">${v.estado}</span></td>
            <td class="d-flex gap-1">
               <button class="btn btn-outline-secondary btn-sm"
                       onclick="verDetalles(${v.id})">Ver</button>
               ${
                  puedeCancel && v.estado !== "cancelada"
                     ? `
                  <button class="btn btn-outline-danger btn-sm"
                          onclick="cancelarVenta(${v.id})">Cancelar</button>
               `
                     : ""
               }
            </td>
         </tr>
      `,
         )
         .join("");

      renderPaginacion(page, cargarVentas);
   } catch (error) {
      console.error("Error cargando ventas:", error);
   }
}

/* Ver detalles */
const modalDetalles = new bootstrap.Modal(
   document.getElementById("modalDetalles"),
);

async function verDetalles(id) {
   try {
      const res = await apiFetch(`/api/ventas/${id}`);
      const v = await res.json();

      document.getElementById("modalDetallesTitulo").textContent =
         `Venta #${v.id} — ${v.nombreUsuario}`;

      document.getElementById("tablaDetallesModal").innerHTML = v.detalles
         .map(
            (d) => `
            <tr>
               <td>${d.nombreProducto}</td>
               <td><code>${d.codigoBarras || "—"}</code></td>
               <td>${d.cantidad}</td>
               <td>$${d.precioUnitario.toLocaleString("es-MX")}</td>
               <td>$${d.subtotal.toLocaleString("es-MX")}</td>
            </tr>
         `,
         )
         .join("");

      document.getElementById("totalDetallesModal").textContent =
         `Total: $${v.total.toLocaleString("es-MX")}`;

      modalDetalles.show();
   } catch (error) {
      console.error("Error cargando detalles:", error);
   }
}

/* Cancelar venta */
async function cancelarVenta(id) {
   const confirmar = confirm(
      "¿Seguro que deseas cancelar esta venta? El stock se revertirá.",
   );
   if (!confirmar) return;

   try {
      const res = await apiFetch(`/api/ventas/${id}/cancelar`, {
         method: "PATCH",
      });
      if (!res.ok) {
         const data = await res.json();
         alert(data.message || "Error al cancelar la venta");
         return;
      }
      cargarVentas(0);
   } catch (error) {
      console.error("Error cancelando venta:", error);
   }
}

/* Guardar venta */
formVenta.addEventListener("submit", async function (e) {
   e.preventDefault();

   const metodoPago = document.getElementById("inputMetodoPago").value;
   const filas = document.querySelectorAll(".detalle-row");
   const btn = document.getElementById("btnGuardarVenta");

   if (filas.length === 0) {
      alertaPanel.textContent = "Agrega al menos un producto";
      alertaPanel.classList.remove("d-none");
      return;
   }

   const detalles = [];
   let valido = true;

   filas.forEach((fila) => {
      const idProducto = parseInt(fila.querySelector(".select-producto").value);
      const cantidad = parseInt(fila.querySelector(".input-cantidad").value);
      const precioUnitario = parseFloat(
         fila.querySelector(".input-precio").value,
      );

      if (!idProducto || !cantidad || !precioUnitario) {
         valido = false;
         return;
      }
      detalles.push({ idProducto, cantidad, precioUnitario });
   });

   if (!valido) {
      alertaPanel.textContent = "Completa todos los campos de los productos";
      alertaPanel.classList.remove("d-none");
      return;
   }

   btn.disabled = true;
   btn.textContent = "Registrando...";
   alertaPanel.classList.add("d-none");

   try {
      const res = await apiFetch("/api/ventas", {
         method: "POST",
         body: JSON.stringify({ metodoPago, detalles }),
      });

      const data = await res.json();
      if (!res.ok)
         throw new Error(data.message || "Error al registrar la venta");

      cerrarPanel();
      cargarVentas(0);
   } catch (error) {
      alertaPanel.textContent = error.message;
      alertaPanel.classList.remove("d-none");
   } finally {
      btn.disabled = false;
      btn.textContent = "Registrar venta";
   }
});

/* Iniciar */
cargarProductos();
cargarVentas(0);
