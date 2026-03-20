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
const formCompra = document.getElementById("formCompra");

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
   formCompra.reset();
   document.getElementById("listaDetalles").innerHTML = "";
   actualizarTotal();
   alertaPanel.classList.add("d-none");
}

document.getElementById("panelClose").addEventListener("click", cerrarPanel);
document
   .getElementById("btnCancelarPanel")
   .addEventListener("click", cerrarPanel);
document.getElementById("panelOverlay").addEventListener("click", cerrarPanel);

/* Nueva compra */
document.getElementById("btnNuevaCompra").addEventListener("click", abrirPanel);

/* Cargar proveedores en el select */
async function cargarProveedores() {
   try {
      const res = await apiFetch("/api/proveedores");
      const data = await res.json();
      const select = document.getElementById("inputProveedor");
      data.forEach((p) => {
         select.innerHTML += `<option value="${p.id}">${p.nombre}</option>`;
      });
   } catch (error) {
      console.error("Error cargando proveedores:", error);
   }
}

/* Cargar productos para los selects de detalles */
async function cargarProductos() {
   try {
      const res = await apiFetch("/api/productos");
      productos = await res.json();
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
            `<option value="${p.id}" data-precio="${p.precio}">${p.nombre}</option>`,
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
         <input type="number" class="form-control input-costo"
                placeholder="Costo" min="0.01" step="0.01" style="width: 110px;">
         <button type="button" class="btn-remove-detalle" onclick="this.closest('.detalle-row').remove(); actualizarTotal(); verificarDetalles();">✕</button>
      </div>
      <div class="subtotal-fila text-muted mt-1" style="font-size: 12px; text-align: right;">
         Subtotal: $0.00
      </div>
   `;

   // Actualizar subtotal al cambiar cantidad o costo
   fila
      .querySelector(".input-cantidad")
      .addEventListener("input", () => actualizarSubtotal(fila));
   fila
      .querySelector(".input-costo")
      .addEventListener("input", () => actualizarSubtotal(fila));

   document.getElementById("listaDetalles").appendChild(fila);
   actualizarTotal();
}

function actualizarSubtotal(fila) {
   const cantidad =
      parseFloat(fila.querySelector(".input-cantidad").value) || 0;
   const costo = parseFloat(fila.querySelector(".input-costo").value) || 0;
   const subtotal = cantidad * costo;
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
      const costo = parseFloat(fila.querySelector(".input-costo").value) || 0;
      total += cantidad * costo;
   });
   document.getElementById("totalCompra").textContent =
      `$${total.toLocaleString("es-MX", { minimumFractionDigits: 2 })}`;
}

function verificarDetalles() {
   const filas = document.querySelectorAll(".detalle-row");
   document.getElementById("sinDetalles").style.display =
      filas.length === 0 ? "block" : "none";
}

/* Cargar compras */
async function cargarCompras() {
   try {
      const res = await apiFetch("/api/compras");
      const data = await res.json();

      document.getElementById("totalCompras").textContent =
         `${data.length} compras`;

      const tbody = document.getElementById("tablaCompras");

      if (data.length === 0) {
         tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted py-3">
            Sin compras registradas</td></tr>`;
         return;
      }

      tbody.innerHTML = data
         .map(
            (c) => `
         <tr>
            <td>#${c.id}</td>
            <td>${new Date(c.fecha).toLocaleDateString("es-MX")}</td>
            <td>${c.nombreProveedor}</td>
            <td><strong>$${c.total.toLocaleString("es-MX")}</strong></td>
            <td>
               <button class="btn btn-outline-secondary btn-sm"
                       onclick="verDetalles(${c.id})">
                  Ver detalles
               </button>
            </td>
         </tr>
      `,
         )
         .join("");
   } catch (error) {
      console.error("Error cargando compras:", error);
   }
}

/* Ver detalles de una compra */
const modalDetalles = new bootstrap.Modal(
   document.getElementById("modalDetalles"),
);

async function verDetalles(id) {
   try {
      const res = await apiFetch(`/api/compras/${id}`);
      const c = await res.json();

      document.getElementById("modalDetallesTitulo").textContent =
         `Compra #${c.id} — ${c.nombreProveedor}`;

      document.getElementById("tablaDetallesModal").innerHTML = c.detalles
         .map(
            (d) => `
            <tr>
               <td>${d.nombreProducto}</td>
               <td><code>${d.codigoBarras || "—"}</code></td>
               <td>${d.cantidad}</td>
               <td>$${d.costoUnitario.toLocaleString("es-MX")}</td>
               <td>$${d.subtotal.toLocaleString("es-MX")}</td>
            </tr>
         `,
         )
         .join("");

      document.getElementById("totalDetallesModal").textContent =
         `Total: $${c.total.toLocaleString("es-MX")}`;

      modalDetalles.show();
   } catch (error) {
      console.error("Error cargando detalles:", error);
   }
}

/* Guardar compra */
formCompra.addEventListener("submit", async function (e) {
   e.preventDefault();

   const idProveedor = document.getElementById("inputProveedor").value;
   const filas = document.querySelectorAll(".detalle-row");
   const btn = document.getElementById("btnGuardarCompra");

   if (!idProveedor) {
      alertaPanel.textContent = "Selecciona un proveedor";
      alertaPanel.classList.remove("d-none");
      return;
   }

   if (filas.length === 0) {
      alertaPanel.textContent = "Agrega al menos un producto";
      alertaPanel.classList.remove("d-none");
      return;
   }

   // Construir detalles
   const detalles = [];
   let valido = true;

   filas.forEach((fila) => {
      const idProducto = parseInt(fila.querySelector(".select-producto").value);
      const cantidad = parseInt(fila.querySelector(".input-cantidad").value);
      const costoUnitario = parseFloat(
         fila.querySelector(".input-costo").value,
      );

      if (!idProducto || !cantidad || !costoUnitario) {
         valido = false;
         return;
      }
      detalles.push({ idProducto, cantidad, costoUnitario });
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
      const res = await apiFetch("/api/compras", {
         method: "POST",
         body: JSON.stringify({
            idProveedor: parseInt(idProveedor),
            detalles,
         }),
      });

      const data = await res.json();

      if (!res.ok) {
         throw new Error(data.message || "Error al registrar la compra");
      }

      cerrarPanel();
      cargarCompras();
   } catch (error) {
      alertaPanel.textContent = error.message;
      alertaPanel.classList.remove("d-none");
   } finally {
      btn.disabled = false;
      btn.textContent = "Registrar compra";
   }
});

/* Iniciar */
cargarProveedores();
cargarProductos();
cargarCompras();
