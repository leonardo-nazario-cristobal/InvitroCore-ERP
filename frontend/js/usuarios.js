/* perfil en el sidebar */

const nombre = localStorage.getItem("nombre") || "Usuario";
const rolUsuario = localStorage.getItem("rol") || "";

document.getElementById("footerNombre").textContent = nombre;
document.getElementById("footerRol").textContent = rolUsuario;

const iniciales = nombre
   .split(" ")
   .map((p) => p[0])
   .join("")
   .substring(0, 2)
   .toLocaleUpperCase();

/* logout */

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

/* panel lateral */

const panelLateral = document.getElementById("panelLateral");
const panelOverlay = document.getElementById("panelOverlay");
const panelTitulo = document.getElementById("panelTitulo");
const alertaPanel = document.getElementById("alertaPanel");
const formUsuario = document.getElementById("formUsuario");
const campoPassword = document.getElementById("campoPassword");

function abrirPanel(titulo) {
   panelTitulo.textContent = titulo;
   panelLateral.classList.add("activo");
   panelOverlay.classList.add("activo");
   alertaPanel.classList.add("d-none");
}

function cerrarPanel() {
   panelLateral.classList.remove("activo");
   panelOverlay.classList.remove("activo");
   formUsuario.reset();
   document.getElementById("usuarioId").value = "";
   alertaPanel.classList.add("d-none");
}

document.getElementById("panelClose").addEventListener("click", cerrarPanel);
document
   .getElementById("btnCancelarPanel")
   .addEventListener("click", cerrarPanel);
document.getElementById("panelOverlay").addEventListener("click", cerrarPanel);

/* Nuevo Usuario */

document.getElementById("btnNuevoUsuario").addEventListener("click", () => {
   document.getElementById("usuarioId").value = "";
   campoPassword.style.display = "block";
   document.getElementById("inputPassword").required = true;
   abrirPanel("Nuevo Usuario");
});

/* filtro de estado */

document.getElementById("filtroEstado").addEventListener("change", function () {
   actualizarColorFiltro(this.value);
   cargarUsuarios();
});

function actualizarColorFiltro(valor) {
   const select = document.getElementById("filtroEstado");
   const colores = {
      activos: { bg: "#f0faf0", color: "#2e7d32", border: "#c8e6c9" },
      inactivos: { bg: "#ffeaea", color: "#c62828", border: "#ffcdd2" },
      todos: { bg: "#f5f5f5", color: "#555", border: "#e0e0e0" },
   };
   const c = colores[valor];
   select.style.backgroundColor = c.bg;
   select.style.color = c.color;
   select.style.borderColor = c.border;
}

actualizarColorFiltro("activos");

/* Cargar usuarios */

async function cargarUsuarios() {
   try {
      const filtro = document.getElementById("filtroEstado").value;

      /* Filtrar según selección */

      const endpoints = {
         activos: "/api/usuarios",
         inactivos: "/api/usuarios/inactivos",
         todos: "/api/usuarios/todos",
      };

      const res = await apiFetch(endpoints[filtro]);
      const data = await res.json();

      document.getElementById("totalUsuarios").textContent =
         `${data.length} usuarios`;

      const tbody = document.getElementById("tablaUsuarios");

      if (data.length === 0) {
         tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted py-3">
            Sin usuarios</td></tr>`;
         return;
      }

      tbody.innerHTML = data
         .map(
            (u) => `
         <tr>
            <td>${u.nombre}</td>
            <td>${u.correo}</td>
            <td>
               <!-- Select de rol directo en la tabla -->
               <select class="form-select form-select-sm rol-select"
                     style="width: auto; min-width: 110px;"
                     data-id="${u.id}"
                     data-rol="${u.rol}"
                     onchange="cambiarRol(${u.id}, this.value)">
                  <option value="admin"   ${u.rol === "admin" ? "selected" : ""}>admin</option>
                  <option value="cajero"  ${u.rol === "cajero" ? "selected" : ""}>cajero</option>
                  <option value="ventas"  ${u.rol === "ventas" ? "selected" : ""}>ventas</option>
                  <option value="compras" ${u.rol === "compras" ? "selected" : ""}>compras</option>
               </select>
            </td>
            <td>
               <span class="${u.activo ? "badge-activo" : "badge-inactivo"}">
                  ${u.activo ? "Activo" : "Inactivo"}
               </span>
            </td>
            <td>
               <button class="btn btn-outline-secondary btn-sm me-1"
                     onclick="editarUsuario(${u.id})">
                  Editar
               </button>
               <button class="btn btn-sm ${
                  u.activo ? "btn-outline-danger" : "btn-outline-success"
               }"
                     onclick="toggleEstado(${u.id}, ${u.activo})">
                  ${u.activo ? "Desactivar" : "Activar"}
               </button>
            </td>
         </tr>
      `,
         )
         .join("");
   } catch (error) {
      console.error("Error cargando usuarios:", error);
   }
}

/* Cambiar rol */

async function cambiarRol(id, nuevoRol) {
   try {
      const res = await apiFetch(
         `/api/usuarios/${id}/rol?nuevoRol=${nuevoRol}`,
         {
            method: "PATCH",
         },
      );
      if (!res.ok) {
         const data = await res.json();
         alert(data.message || "Error al cambiar el rol");
         cargarUsuarios();
         return;
      }

      const select = document.querySelector(`select[data-id="${id}"]`);
      if (select) select.setAttribute("data-rol", nuevoRol);
   } catch (error) {
      console.error("Error cambiando rol:", error);
      cargarUsuarios();
   }
}

/* Editar usuario */

async function editarUsuario(id) {
   try {
      const res = await apiFetch(`/api/usuarios/${id}`);
      const u = await res.json();

      document.getElementById("usuarioId").value = u.id;
      document.getElementById("inputNombre").value = u.nombre;
      document.getElementById("inputCorreo").value = u.correo;
      document.getElementById("inputRol").value = u.rol;

      /* ocultar contraseña */

      campoPassword.style.display = "none";
      document.getElementById("inputPassword").required = false;

      abrirPanel("Editar usuario");
   } catch (error) {
      console.error("Error cargando usuario: ", error);
   }

   /* Activar / Desactivar  */
}

async function toggleEstado(id, activo) {
   const action = activo ? "desactivar" : "activar";
   const confirmar = confirm(`¿Seguro que deseas ${action} este usuario?`);
   if (!confirmar) return;

   try {
      await apiFetch(`/api/usuarios/${id}/${action}`, { method: "PATCH" });
      cargarUsuarios();
   } catch (error) {
      console.error(`Error al ${action} usuario:`, error);
   }
}

/* Guardar usuario (crear o editar) */

formUsuario.addEventListener("submit", async function (e) {
   e.preventDefault();

   const id = document.getElementById("usuarioId").value;
   const nombre = document.getElementById("inputNombre").value.trim();
   const correo = document.getElementById("inputCorreo").value.trim();
   const password = document.getElementById("inputPassword").value.trim();
   const rol = document.getElementById("inputRol").value;
   const btn = document.getElementById("btnGuardarUsuario");

   btn.disabled = true;
   btn.textContent = "Guardando...";
   alertaPanel.classList.add("d-none");

   try {
      const body = { nombre, correo, rol };
      if (password) body.password = password;

      const esEdicion = !!id;
      const res = await apiFetch(
         esEdicion ? `/api/usuarios/${id}` : "/api/usuarios",
         {
            method: esEdicion ? "PUT" : "POST",
            body: JSON.stringify(body),
         },
      );

      const data = await res.json();

      if (!res.ok) {
         throw new Error(data.message || "Error al guardar el usuario");
      }

      cerrarPanel();
      cargarUsuarios();
   } catch (error) {
      alertaPanel.textContent = error.message;
      alertaPanel.classList.remove("d-none");
   } finally {
      btn.disabled = false;
      btn.textContent = "Guardar";
   }
});

/* iniciar */

cargarUsuarios();
