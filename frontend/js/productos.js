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

/* Cargar categorías en el select del panel y en el filtro */
async function cargarCategorias() {
   try {
      const res = await apiFetch("/api/categorias");
      const data = await res.json();

      const selectPanel = document.getElementById("inputCategoria");
      const selectFiltro = document.getElementById("filtroCategorias");

      data.forEach((c) => {
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
      if (termino.length > 0) {
         buscarProductos(termino);
      } else {
         cargarProductos();
      }
   }, 400);
});

/* Filtro por categoría */
document
   .getElementById("filtroCategorias")
   .addEventListener("change", function () {
      const idCategoria = this.value;
      if (idCategoria) {
         cargarProductosPorCategoria(idCategoria);
      } else {
         cargarProductos();
      }
   });

async function cargarProductos() {
   try {
      const filtro = document.getElementById("filtroEstado").value;

      const endpoints = {
         activos: "/api/productos",
         inactivos: "/api/productos/inactivos",
         todos: "/api/productos/todos",
      };

      const res = await apiFetch(endpoints[filtro]);
      const data = await res.json();
      renderTabla(data);
   } catch (error) {
      console.error("Error cargando productos:", error);
   }
}

/* Listener del filtro */
document
   .getElementById("filtroEstado")
   .addEventListener("change", cargarProductos);
/* Buscar por nombre */
async function buscarProductos(termino) {
   try {
      const res = await apiFetch(
         `/api/productos/buscar?nombre=${encodeURIComponent(termino)}`,
      );
      const data = await res.json();
      renderTabla(data);
   } catch (error) {
      console.error("Error buscando productos:", error);
   }
}

/* Filtrar por categoría */
async function cargarProductosPorCategoria(idCategoria) {
   try {
      const res = await apiFetch(`/api/productos/categoria/${idCategoria}`);
      const data = await res.json();
      renderTabla(data);
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
         renderTabla([producto]); // devuelve uno solo, lo metemos en array
      } catch (error) {
         console.error("Error buscando por código:", error);
      }
   });

/* Ver stock bajo */
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
            renderTabla(data);
         } catch (error) {
            console.error("Error cargando stock bajo:", error);
         }
      } else {
         this.classList.replace("btn-warning", "btn-outline-warning");
         this.textContent = "Stock bajo";
         cargarProductos();
      }
   });

/* Renderizar tabla */
function renderTabla(data) {
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
                        onclick="editarProducto(${p.id})">
                     Editar
                  </button>
                  <button class="btn btn-sm ${
                     p.activo ? "btn-outline-danger" : "btn-outline-success"
                  }"
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
      cargarProductos();
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

      if (!res.ok) {
         throw new Error(data.message || "Error al guardar el producto");
      }

      cerrarPanel();
      cargarProductos();
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
cargarProductos();
