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
const formMovimiento = document.getElementById("formMovimiento");

function abrirPanel() {
   panelLateral.classList.add("activo");
   panelOverlay.classList.add("activo");
   alertaPanel.classList.add("d-none");
}

function cerrarPanel() {
   panelLateral.classList.remove("activo");
   panelOverlay.classList.remove("activo");
   formMovimiento.reset();
   alertaPanel.classList.add("d-none");
}

document.getElementById("panelClose").addEventListener("click", cerrarPanel);
document
   .getElementById("btnCancelarPanel")
   .addEventListener("click", cerrarPanel);
document.getElementById("panelOverlay").addEventListener("click", cerrarPanel);

/* Nuevo movimiento */
document
   .getElementById("btnNuevoMovimiento")
   .addEventListener("click", abrirPanel);

/* Cargar productos en el select */
async function cargarProductos() {
   try {
      const res = await apiFetch("/api/productos?pagina=0&tamanio=500");
      const page = await res.json();
      const select = document.getElementById("inputProducto");
      page.content.forEach((p) => {
         select.innerHTML += `<option value="${p.id}">${p.nombre} (stock: ${p.stock})</option>`;
      });
   } catch (error) {
      console.error("Error cargando productos:", error);
   }
}

/* ── Paginación ──────────────────────────────── */
const TAMANIO = 25;
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

/* Filtro por tipo */
document
   .getElementById("filtroTipo")
   .addEventListener("change", () => cargarMovimientos(0));

/* Cargar movimientos con paginación */
async function cargarMovimientos(pagina = 0) {
   try {
      const filtro = document.getElementById("filtroTipo").value;
      const endpoint = filtro
         ? `/api/movimientos/tipo?valor=${filtro}&pagina=${pagina}&tamanio=${TAMANIO}`
         : `/api/movimientos?pagina=${pagina}&tamanio=${TAMANIO}`;

      const res = await apiFetch(endpoint);
      const page = await res.json();

      document.getElementById("totalMovimientos").textContent =
         `${page.totalElements} movimientos`;

      const tbody = document.getElementById("tablaMovimientos");

      if (page.content.length === 0) {
         tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-3">
            Sin movimientos</td></tr>`;
         renderPaginacion(page, cargarMovimientos);
         return;
      }

      tbody.innerHTML = page.content
         .map(
            (m) => `
         <tr>
            <td>#${m.id}</td>
            <td>${new Date(m.creadoEn).toLocaleDateString("es-MX")}</td>
            <td><strong>${m.nombreProducto}</strong></td>
            <td><span class="badge-${m.tipo}">${m.tipo}</span></td>
            <td>${m.cantidad}</td>
            <td style="max-width: 200px; white-space: nowrap; overflow: hidden;
                text-overflow: ellipsis;" title="${m.motivo}">
               ${m.motivo}
            </td>
            <td>${m.nombreUsuario || '<span class="text-muted">—</span>'}</td>
         </tr>
      `,
         )
         .join("");

      renderPaginacion(page, cargarMovimientos);
   } catch (error) {
      console.error("Error cargando movimientos:", error);
   }
}

/* Guardar movimiento */
formMovimiento.addEventListener("submit", async function (e) {
   e.preventDefault();

   const idProducto = document.getElementById("inputProducto").value;
   const tipo = document.getElementById("inputTipo").value;
   const cantidad = parseInt(document.getElementById("inputCantidad").value);
   const motivo = document.getElementById("inputMotivo").value.trim();
   const btn = document.getElementById("btnGuardarMovimiento");

   btn.disabled = true;
   btn.textContent = "Registrando...";
   alertaPanel.classList.add("d-none");

   try {
      const res = await apiFetch("/api/movimientos", {
         method: "POST",
         body: JSON.stringify({
            idProducto: parseInt(idProducto),
            tipo,
            cantidad,
            motivo,
         }),
      });

      const data = await res.json();
      if (!res.ok)
         throw new Error(data.message || "Error al registrar el movimiento");

      cerrarPanel();
      cargarMovimientos(0);
   } catch (error) {
      alertaPanel.textContent = error.message;
      alertaPanel.classList.remove("d-none");
   } finally {
      btn.disabled = false;
      btn.textContent = "Registrar";
   }
});

/* Iniciar */
cargarProductos();
cargarMovimientos(0);
