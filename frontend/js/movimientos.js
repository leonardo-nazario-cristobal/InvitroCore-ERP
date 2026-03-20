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
      const res = await apiFetch("/api/productos");
      const data = await res.json();
      const select = document.getElementById("inputProducto");
      data.forEach((p) => {
         select.innerHTML += `<option value="${p.id}">${p.nombre} (stock: ${p.stock})</option>`;
      });
   } catch (error) {
      console.error("Error cargando productos:", error);
   }
}

/* Filtro por tipo */
document
   .getElementById("filtroTipo")
   .addEventListener("change", cargarMovimientos);

/* Cargar movimientos */
async function cargarMovimientos() {
   try {
      const filtro = document.getElementById("filtroTipo").value;
      const endpoint = filtro
         ? `/api/movimientos/tipo?valor=${filtro}`
         : "/api/movimientos";

      const res = await apiFetch(endpoint);
      const data = await res.json();

      document.getElementById("totalMovimientos").textContent =
         `${data.length} movimientos`;

      const tbody = document.getElementById("tablaMovimientos");

      if (data.length === 0) {
         tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-3">
            Sin movimientos</td></tr>`;
         return;
      }

      tbody.innerHTML = data
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

      if (!res.ok) {
         throw new Error(data.message || "Error al registrar el movimiento");
      }

      cerrarPanel();
      cargarMovimientos();
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
cargarMovimientos();
