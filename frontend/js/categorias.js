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
const formCategoria = document.getElementById("formCategoria");

function abrirPanel(titulo) {
   panelTitulo.textContent = titulo;
   panelLateral.classList.add("activo");
   panelOverlay.classList.add("activo");
   alertaPanel.classList.add("d-none");
}

function cerrarPanel() {
   panelLateral.classList.remove("activo");
   panelOverlay.classList.remove("activo");
   formCategoria.reset();
   document.getElementById("categoriaId").value = "";
   alertaPanel.classList.add("d-none");
}

document.getElementById("panelClose").addEventListener("click", cerrarPanel);
document
   .getElementById("btnCancelarPanel")
   .addEventListener("click", cerrarPanel);
document.getElementById("panelOverlay").addEventListener("click", cerrarPanel);

/* Nueva categoría */
document.getElementById("btnNuevaCategoria").addEventListener("click", () => {
   document.getElementById("categoriaId").value = "";
   abrirPanel("Nueva categoría");
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

/* Cargar categorías con paginación */
async function cargarCategorias(pagina = 0) {
   try {
      const res = await apiFetch(
         `/api/categorias?pagina=${pagina}&tamanio=${TAMANIO}`,
      );
      const page = await res.json();

      document.getElementById("totalCategorias").textContent =
         `${page.totalElements} categorías`;

      const tbody = document.getElementById("tablaCategorias");

      if (page.content.length === 0) {
         tbody.innerHTML = `<tr><td colspan="3" class="text-center text-muted py-3">
            Sin categorías registradas</td></tr>`;
         renderPaginacion(page, cargarCategorias);
         return;
      }

      tbody.innerHTML = page.content
         .map(
            (c) => `
         <tr>
            <td><strong>${c.nombre}</strong></td>
            <td>${c.descripcion || '<span class="text-muted">Sin descripción</span>'}</td>
            <td>
               <button class="btn btn-outline-secondary btn-sm me-1"
                       onclick="editarCategoria(${c.id})">Editar</button>
               <button class="btn btn-outline-danger btn-sm"
                       onclick="confirmarEliminar(${c.id}, '${c.nombre}')">Eliminar</button>
            </td>
         </tr>
      `,
         )
         .join("");

      renderPaginacion(page, cargarCategorias);
   } catch (error) {
      console.error("Error cargando categorías:", error);
   }
}

/* Editar categoría */
async function editarCategoria(id) {
   try {
      const res = await apiFetch(`/api/categorias/${id}`);
      const c = await res.json();

      document.getElementById("categoriaId").value = c.id;
      document.getElementById("inputNombre").value = c.nombre;
      document.getElementById("inputDescripcion").value = c.descripcion || "";

      abrirPanel("Editar categoría");
   } catch (error) {
      console.error("Error cargando categoría:", error);
   }
}

/* Modal eliminar */
let categoriaIdEliminar = null;
const modalEliminar = new bootstrap.Modal(
   document.getElementById("modalEliminar"),
);

function confirmarEliminar(id, nombre) {
   categoriaIdEliminar = id;
   document.getElementById("modalEliminarNombre").textContent =
      `¿Seguro que deseas eliminar "${nombre}"? Esta acción no se puede deshacer.`;
   modalEliminar.show();
}

document
   .getElementById("btnConfirmarEliminar")
   .addEventListener("click", async () => {
      if (!categoriaIdEliminar) return;
      try {
         await apiFetch(`/api/categorias/${categoriaIdEliminar}`, {
            method: "DELETE",
         });
         modalEliminar.hide();
         cargarCategorias(0);
      } catch (error) {
         console.error("Error eliminando categoría:", error);
      } finally {
         categoriaIdEliminar = null;
      }
   });

/* Guardar categoría */
formCategoria.addEventListener("submit", async function (e) {
   e.preventDefault();

   const id = document.getElementById("categoriaId").value;
   const nombre = document.getElementById("inputNombre").value.trim();
   const descripcion = document.getElementById("inputDescripcion").value.trim();
   const btn = document.getElementById("btnGuardarCategoria");

   btn.disabled = true;
   btn.textContent = "Guardando...";
   alertaPanel.classList.add("d-none");

   try {
      const esEdicion = !!id;
      const res = await apiFetch(
         esEdicion ? `/api/categorias/${id}` : "/api/categorias",
         {
            method: esEdicion ? "PUT" : "POST",
            body: JSON.stringify({ nombre, descripcion }),
         },
      );

      const data = await res.json();
      if (!res.ok)
         throw new Error(data.message || "Error al guardar la categoría");

      cerrarPanel();
      cargarCategorias(0);
   } catch (error) {
      alertaPanel.textContent = error.message;
      alertaPanel.classList.remove("d-none");
   } finally {
      btn.disabled = false;
      btn.textContent = "Guardar";
   }
});

/* Iniciar */
cargarCategorias(0);
