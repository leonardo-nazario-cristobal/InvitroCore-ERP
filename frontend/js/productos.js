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
const formProducto = document.getElementById("formProducto");

function abrirPanel(titulo) {
   panelTitulo.textContent = titulo;
   panelLateral.classList.add("activo");
   panelOverlay.classList.add("activo");
   alertaPanel.classList.add("d-none");
}

function cerrarPanel() {
   panelLateral.classList.remove("activo");
   panelOverlay.classList.remove("activo");
   formProducto.reset();
   document.getElementById("productoId").value = "";
   alertaPanel.classList.add("d-none");
}

document.getElementById("panelClose").addEventListener("click", cerrarPanel);
document
   .getElementById("btnCancelarPanel")
   .addEventListener("click", cerrarPanel);
document.getElementById("panelOverlay").addEventListener("click", cerrarPanel);

/* Nuevo producto */
document.getElementById("btnNuevoProducto").addEventListener("click", () => {
   document.getElementById("productoId").value = "";
   abrirPanel("Nuevo producto");
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

/* Cargar categorías */
async function cargarCategorias() {
   try {
      const res = await apiFetch("/api/categorias?pagina=0&tamanio=100");
      const page = await res.json();
      const selectPanel = document.getElementById("inputCategoria");
      const selectFiltro = document.getElementById("filtroCategorias");
      page.content.forEach((c) => {
         selectPanel.innerHTML += `<option value="${c.id}">${c.nombre}</option>`;
         selectFiltro.innerHTML += `<option value="${c.id}">${c.nombre}</option>`;
      });
   } catch (error) {
      console.error("Error cargando categorías:", error);
   }
}

/* Buscador con debounce */
let timeoutBusqueda;
document.getElementById("buscador").addEventListener("input", function () {
   clearTimeout(timeoutBusqueda);
   timeoutBusqueda = setTimeout(() => {
      const termino = this.value.trim();
      // Limpiar filtros cruzados
      document.getElementById("filtroCategorias").value = "";
      if (termino.length > 0) {
         buscarProductos(termino, 0);
      } else {
         cargarProductos(0);
      }
   }, 400);
});

/* Filtro por categoría */
document
   .getElementById("filtroCategorias")
   .addEventListener("change", function () {
      const idCategoria = this.value;
      // Limpiar buscador al filtrar por categoría
      document.getElementById("buscador").value = "";
      if (idCategoria) {
         cargarProductosPorCategoria(idCategoria, 0);
      } else {
         cargarProductos(0);
      }
   });

/* Filtro estado */
document.getElementById("filtroEstado").addEventListener("change", () => {
   // Limpiar filtros cruzados al cambiar estado
   document.getElementById("filtroCategorias").value = "";
   document.getElementById("buscador").value = "";
   cargarProductos(0);
});

/* Cargar productos con paginación */
async function cargarProductos(pagina = 0) {
   const filtro = document.getElementById("filtroEstado").value;
   const endpoints = {
      activos: `/api/productos?pagina=${pagina}&tamanio=${TAMANIO}`,
      inactivos: `/api/productos/inactivos?pagina=${pagina}&tamanio=${TAMANIO}`,
      todos: `/api/productos/todos?pagina=${pagina}&tamanio=${TAMANIO}`,
   };
   try {
      const res = await apiFetch(endpoints[filtro]);
      const page = await res.json();

      if (!page || !page.content) {
         console.warn("Respuesta inesperada:", page);
         return;
      }

      renderTabla(page.content);
      renderPaginacion(page, cargarProductos);
   } catch (error) {
      console.error("Error cargando productos:", error);
   }
}

/* Buscar por nombre con paginación */
async function buscarProductos(termino, pagina = 0) {
   try {
      const res = await apiFetch(
         `/api/productos/buscar?nombre=${encodeURIComponent(termino)}&pagina=${pagina}&tamanio=${TAMANIO}`,
      );
      const page = await res.json();

      if (!page || !page.content) return;

      renderTabla(page.content);
      renderPaginacion(page, (p) => buscarProductos(termino, p));
   } catch (error) {
      console.error("Error buscando productos:", error);
   }
}

/* Filtrar por categoría con paginación */
async function cargarProductosPorCategoria(idCategoria, pagina = 0) {
   try {
      const res = await apiFetch(
         `/api/productos/categoria/${idCategoria}?pagina=${pagina}&tamanio=${TAMANIO}`,
      );
      const page = await res.json();

      if (!page || !page.content) return;

      renderTabla(page.content);
      renderPaginacion(page, (p) =>
         cargarProductosPorCategoria(idCategoria, p),
      );
   } catch (error) {
      console.error("Error filtrando por categoría:", error);
   }
}

/* Mostrar botón stock bajo solo para admin y compras */
if (["admin", "compras"].includes(rolUsuario)) {
   document.getElementById("btnStockBajo").style.display = "block";
}

/* Buscar por código de barras */
document
   .getElementById("buscadorCodigo")
   .addEventListener("keydown", async function (e) {
      if (e.key !== "Enter") return;
      const codigo = this.value.trim();
      if (!codigo) return;
      try {
         const res = await apiFetch(
            `/api/productos/codigo/${encodeURIComponent(codigo)}`,
         );
         if (res.status === 404) {
            renderTabla([]);
            return;
         }
         const producto = await res.json();
         renderTabla([producto]);
         const contenedor = document.getElementById("paginacion");
         if (contenedor) contenedor.innerHTML = "";
      } catch (error) {
         console.error("Error buscando por código:", error);
      }
   });

/* Stock bajo */
let viendoStockBajo = false;
document
   .getElementById("btnStockBajo")
   .addEventListener("click", async function () {
      viendoStockBajo = !viendoStockBajo;
      if (viendoStockBajo) {
         this.classList.replace("btn-outline-warning", "btn-warning");
         this.textContent = "Ver todos";
         try {
            const res = await apiFetch("/api/productos/stock-bajo");
            const data = await res.json();
            renderTablaStockBajo(data);
            const contenedor = document.getElementById("paginacion");
            if (contenedor) contenedor.innerHTML = "";
         } catch (error) {
            console.error("Error cargando stock bajo:", error);
         }
      } else {
         this.classList.replace("btn-warning", "btn-outline-warning");
         this.textContent = "Stock bajo";
         cargarProductos(0);
      }
   });

/* Renderizar tabla stock bajo — campos distintos al ProductoResponseDTO */
function renderTablaStockBajo(data) {
   if (!data || !Array.isArray(data)) return;

   document.getElementById("totalProductos").textContent =
      `${data.length} productos`;
   const tbody = document.getElementById("tablaProductos");

   if (data.length === 0) {
      tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-3">
         Sin productos con stock bajo</td></tr>`;
      return;
   }

   tbody.innerHTML = data
      .map(
         (p) => `
      <tr>
         <td><code>${p.codigoBarras || "—"}</code></td>
         <td><strong>${p.nombre}</strong></td>
         <td><span class="text-muted">—</span></td>
         <td><span class="text-muted">—</span></td>
         <td class="stock-low">${p.stock}</td>
         <td><span class="badge-activo">Activo</span></td>
         <td>
            <span class="text-muted" style="font-size:12px;">
               Mínimo: ${p.stockMinimo}
            </span>
         </td>
      </tr>
   `,
      )
      .join("");
}

/* Renderizar tabla */
function renderTabla(data) {
   if (!data || !Array.isArray(data)) {
      console.warn("renderTabla recibió datos inválidos:", data);
      return;
   }

   document.getElementById("totalProductos").textContent =
      `${data.length} productos`;
   const tbody = document.getElementById("tablaProductos");

   if (data.length === 0) {
      tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-3">
         Sin productos</td></tr>`;
      return;
   }

   tbody.innerHTML = data
      .map((p) => {
         const stockClass =
            p.stock === 0
               ? "stock-zero"
               : p.stockBajo
                 ? "stock-low"
                 : "stock-ok";
         const puedeEditar = ["admin", "compras"].includes(rolUsuario);
         return `
         <tr>
            <td><code>${p.codigoBarras || "—"}</code></td>
            <td><strong>${p.nombre}</strong></td>
            <td>${p.nombreCategoria || '<span class="text-muted">—</span>'}</td>
            <td>$${p.precio.toLocaleString("es-MX")}</td>
            <td class="${stockClass}">${p.stock}</td>
            <td>
               <span class="${p.activo ? "badge-activo" : "badge-inactivo"}">
                  ${p.activo ? "Activo" : "Inactivo"}
               </span>
            </td>
            <td>
               ${
                  puedeEditar
                     ? `
                  <button class="btn btn-outline-secondary btn-sm me-1"
                          onclick="editarProducto(${p.id})">Editar</button>
                  <button class="btn btn-sm ${p.activo ? "btn-outline-danger" : "btn-outline-success"}"
                          onclick="toggleEstado(${p.id}, ${p.activo})">
                     ${p.activo ? "Desactivar" : "Activar"}
                  </button>
               `
                     : '<span class="text-muted" style="font-size:12px;">Solo lectura</span>'
               }
            </td>
         </tr>
      `;
      })
      .join("");
}

/* Editar producto */
async function editarProducto(id) {
   try {
      const res = await apiFetch(`/api/productos/${id}`);
      const p = await res.json();
      document.getElementById("productoId").value = p.id;
      document.getElementById("inputNombre").value = p.nombre;
      document.getElementById("inputDescripcion").value = p.descripcion || "";
      document.getElementById("inputPrecio").value = p.precio;
      document.getElementById("inputStockMinimo").value = p.stockMinimo;
      document.getElementById("inputCategoria").value = p.idCategoria || "";
      document.getElementById("inputCodigoBarras").value = p.codigoBarras || "";
      abrirPanel("Editar producto");
   } catch (error) {
      console.error("Error cargando producto:", error);
   }
}

/* Activar / Desactivar */
async function toggleEstado(id, activo) {
   const accion = activo ? "desactivar" : "activar";
   const confirmar = confirm(`¿Seguro que deseas ${accion} este producto?`);
   if (!confirmar) return;
   try {
      await apiFetch(`/api/productos/${id}/${accion}`, { method: "PATCH" });
      cargarProductos(0);
   } catch (error) {
      console.error(`Error al ${accion} producto:`, error);
   }
}

/* Guardar producto */
formProducto.addEventListener("submit", async function (e) {
   e.preventDefault();
   const id = document.getElementById("productoId").value;
   const nombre = document.getElementById("inputNombre").value.trim();
   const descripcion = document.getElementById("inputDescripcion").value.trim();
   const precio = parseFloat(document.getElementById("inputPrecio").value);
   const stockMinimo =
      parseInt(document.getElementById("inputStockMinimo").value) || 5;
   const idCategoria = document.getElementById("inputCategoria").value || null;
   const codigoBarras =
      document.getElementById("inputCodigoBarras").value.trim() || null;
   const btn = document.getElementById("btnGuardarProducto");

   btn.disabled = true;
   btn.textContent = "Guardando...";
   alertaPanel.classList.add("d-none");

   try {
      const esEdicion = !!id;
      const res = await apiFetch(
         esEdicion ? `/api/productos/${id}` : "/api/productos",
         {
            method: esEdicion ? "PUT" : "POST",
            body: JSON.stringify({
               nombre,
               descripcion,
               precio,
               stockMinimo,
               idCategoria,
               codigoBarras,
            }),
         },
      );
      const data = await res.json();
      if (!res.ok)
         throw new Error(data.message || "Error al guardar el producto");
      cerrarPanel();
      cargarProductos(0);
   } catch (error) {
      alertaPanel.textContent = error.message;
      alertaPanel.classList.remove("d-none");
   } finally {
      btn.disabled = false;
      btn.textContent = "Guardar";
   }
});

/* Iniciar */
cargarCategorias();
cargarProductos(0);
