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

/* Cargar categorías */
async function cargarCategorias() {
   try {
      const res = await apiFetch("/api/categorias");
      const data = await res.json();

      document.getElementById("totalCategorias").textContent =
         `${data.length} categorías`;

      const tbody = document.getElementById("tablaCategorias");

      if (data.length === 0) {
         tbody.innerHTML = `<tr><td colspan="3" class="text-center text-muted py-3">
            Sin categorías registradas</td></tr>`;
         return;
      }

      tbody.innerHTML = data
         .map(
            (c) => `
         <tr>
            <td><strong>${c.nombre}</strong></td>
            <td>${c.descripcion || '<span class="text-muted">Sin descripción</span>'}</td>
            <td>
               <button class="btn btn-outline-secondary btn-sm me-1"
                       onclick="editarCategoria(${c.id})">
                  Editar
               </button>
               <button class="btn btn-outline-danger btn-sm"
                       onclick="confirmarEliminar(${c.id}, '${c.nombre}')">
                  Eliminar
               </button>
            </td>
         </tr>
      `,
         )
         .join("");
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
         cargarCategorias();
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

      if (!res.ok) {
         throw new Error(data.message || "Error al guardar la categoría");
      }

      cerrarPanel();
      cargarCategorias();
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
