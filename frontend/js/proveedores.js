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
const panelTitulo = document.getElementById("panelTitulo");
const alertaPanel = document.getElementById("alertaPanel");
const formProveedor = document.getElementById("formProveedor");

function abrirPanel(titulo) {
   panelTitulo.textContent = titulo;
   panelLateral.classList.add("activo");
   panelOverlay.classList.add("activo");
   alertaPanel.classList.add("d-none");
}

function cerrarPanel() {
   panelLateral.classList.remove("activo");
   panelOverlay.classList.remove("activo");
   formProveedor.reset();
   document.getElementById("proveedorId").value = "";
   alertaPanel.classList.add("d-none");
}

document.getElementById("panelClose").addEventListener("click", cerrarPanel);
document
   .getElementById("btnCancelarPanel")
   .addEventListener("click", cerrarPanel);
document.getElementById("panelOverlay").addEventListener("click", cerrarPanel);

/* Nuevo proveedor */
document.getElementById("btnNuevoProveedor").addEventListener("click", () => {
   document.getElementById("proveedorId").value = "";
   abrirPanel("Nuevo proveedor");
});

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

/* Cargar proveedores con paginación */
async function cargarProveedores(pagina = 0) {
   try {
      const res = await apiFetch(
         `/api/proveedores?pagina=${pagina}&tamanio=${TAMANIO}`,
      );
      const page = await res.json();

      document.getElementById("totalProveedores").textContent =
         `${page.totalElements} proveedores`;

      const tbody = document.getElementById("tablaProveedores");

      if (page.content.length === 0) {
         tbody.innerHTML = `<tr><td colspan="4" class="text-center text-muted py-3">
            Sin proveedores registrados</td></tr>`;
         renderPaginacion(page, cargarProveedores);
         return;
      }

      tbody.innerHTML = page.content
         .map(
            (p) => `
         <tr>
            <td><strong>${p.nombre}</strong></td>
            <td>${p.telefono || '<span class="text-muted">—</span>'}</td>
            <td>${p.correo || '<span class="text-muted">—</span>'}</td>
            <td>
               <button class="btn btn-outline-secondary btn-sm me-1"
                       onclick="editarProveedor(${p.id})">Editar</button>
               <button class="btn btn-outline-danger btn-sm"
                       onclick="confirmarEliminar(${p.id}, '${p.nombre}')">Eliminar</button>
            </td>
         </tr>
      `,
         )
         .join("");

      renderPaginacion(page, cargarProveedores);
   } catch (error) {
      console.error("Error cargando proveedores:", error);
   }
}

/* Editar proveedor */
async function editarProveedor(id) {
   try {
      const res = await apiFetch(`/api/proveedores/${id}`);
      const p = await res.json();

      document.getElementById("proveedorId").value = p.id;
      document.getElementById("inputNombre").value = p.nombre;
      document.getElementById("inputTelefono").value = p.telefono || "";
      document.getElementById("inputCorreo").value = p.correo || "";

      abrirPanel("Editar proveedor");
   } catch (error) {
      console.error("Error cargando proveedor:", error);
   }
}

/* Modal eliminar */
let proveedorIdEliminar = null;
const modalEliminar = new bootstrap.Modal(
   document.getElementById("modalEliminar"),
);

function confirmarEliminar(id, nombre) {
   proveedorIdEliminar = id;
   document.getElementById("modalEliminarNombre").textContent =
      `¿Seguro que deseas eliminar "${nombre}"? Esta acción no se puede deshacer.`;
   modalEliminar.show();
}

document
   .getElementById("btnConfirmarEliminar")
   .addEventListener("click", async () => {
      if (!proveedorIdEliminar) return;
      try {
         await apiFetch(`/api/proveedores/${proveedorIdEliminar}`, {
            method: "DELETE",
         });
         modalEliminar.hide();
         cargarProveedores(0);
      } catch (error) {
         console.error("Error eliminando proveedor:", error);
      } finally {
         proveedorIdEliminar = null;
      }
   });

/* Guardar proveedor */
formProveedor.addEventListener("submit", async function (e) {
   e.preventDefault();

   const id = document.getElementById("proveedorId").value;
   const nombre = document.getElementById("inputNombre").value.trim();
   const telefono = document.getElementById("inputTelefono").value.trim();
   const correo = document.getElementById("inputCorreo").value.trim();
   const btn = document.getElementById("btnGuardarProveedor");

   btn.disabled = true;
   btn.textContent = "Guardando...";
   alertaPanel.classList.add("d-none");

   try {
      const esEdicion = !!id;
      const res = await apiFetch(
         esEdicion ? `/api/proveedores/${id}` : "/api/proveedores",
         {
            method: esEdicion ? "PUT" : "POST",
            body: JSON.stringify({ nombre, telefono, correo }),
         },
      );

      const data = await res.json();
      if (!res.ok)
         throw new Error(data.message || "Error al guardar el proveedor");

      cerrarPanel();
      cargarProveedores(0);
   } catch (error) {
      alertaPanel.textContent = error.message;
      alertaPanel.classList.remove("d-none");
   } finally {
      btn.disabled = false;
      btn.textContent = "Guardar";
   }
});

/* Iniciar */
cargarProveedores(0);
