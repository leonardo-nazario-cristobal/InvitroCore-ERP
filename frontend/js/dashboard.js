/* Datos del usuario desde localStorage */
const nombre = localStorage.getItem("nombre") || "Usuario";
const rol = localStorage.getItem("rol") || "";

/* Permisos del menú */
function aplicarPermisosMenu() {
   const menuPermisos = {
      "menu-usuarios": ["admin"],
      "menu-categorias": ["admin", "compras"],
      "menu-proveedores": ["admin", "compras"],
      "menu-compras": ["admin", "compras"],
      "menu-movimientos": ["admin", "compras"],
      "menu-ventas": ["admin", "cajero", "ventas"],
   };

   Object.entries(menuPermisos).forEach(([id, roles]) => {
      const el = document.getElementById(id);
      if (el && !roles.includes(rol)) {
         el.style.display = "none";
      }
   });
}

aplicarPermisosMenu();

/* Perfil en el sidebar */
document.getElementById("footerNombre").textContent = nombre;
document.getElementById("footerRol").textContent = rol;

const iniciales = nombre
   .split(" ")
   .map((p) => p[0])
   .join("")
   .substring(0, 2)
   .toUpperCase();
document.getElementById("avatarInicial").textContent = iniciales;

/* Bienvenida en el topbar */
document.getElementById("bienvenida").textContent = `Bienvenido, ${nombre}`;

/* Modal de bienvenida solo para admin */
if (rol === "admin" && !sessionStorage.getItem("modalMostrado")) {
   sessionStorage.setItem("modalMostrado", "true");
   document.getElementById("modalBienvenidaNombre").textContent = nombre;
   const modal = new bootstrap.Modal(
      document.getElementById("modalBienvenida"),
   );
   modal.show();
}

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

/* Cargar datos del dashboard */
async function cargarDashboard() {
   try {
      const puedeVerStockBajo = ["admin", "compras"].includes(rol);

      const promesas = [
         apiFetch("/api/productos?pagina=0&tamanio=1").then((r) => r.json()),
         apiFetch("/api/proveedores?pagina=0&tamanio=1").then((r) => r.json()),
         puedeVerStockBajo
            ? apiFetch("/api/productos/stock-bajo").then((r) => r.json())
            : Promise.resolve([]),
         apiFetch("/api/ventas?pagina=0&tamanio=25").then((r) => r.json()),
         apiFetch("/api/compras?pagina=0&tamanio=25").then((r) => r.json()),
      ];

      const [
         productosPage,
         proveedoresPage,
         stockBajo,
         ventasPage,
         comprasPage,
      ] = await Promise.all(promesas);

      /* Ocultar tabla stock bajo si no tiene permiso */
      if (!puedeVerStockBajo) {
         document.querySelector(".table-card").style.display = "none";
      }

      /* Productos activos — usar totalElements del Page */
      document.getElementById("totalProductos").textContent =
         productosPage.totalElements;
      document.getElementById("stockBajoSub").textContent =
         `${Array.isArray(stockBajo) ? stockBajo.length : 0} con stock bajo`;

      /* Proveedores — usar totalElements del Page */
      document.getElementById("totalProveedores").textContent =
         proveedoresPage.totalElements;

      /* Ventas de hoy — filtrar sobre content */
      const hoy = new Date().toISOString().split("T")[0];
      const ventasHoy = ventasPage.content.filter(
         (v) => v.fecha.startsWith(hoy) && v.estado === "completada",
      );
      const totalHoy = ventasHoy.reduce((sum, v) => sum + v.total, 0);
      document.getElementById("ventasHoy").textContent =
         `$${totalHoy.toLocaleString("es-MX")}`;
      document.getElementById("ventasHoySub").textContent =
         `${ventasHoy.length} transacciones`;

      /* Compras del mes — usar totalElements del Page */
      document.getElementById("comprasMes").textContent =
         `$${comprasPage.content.reduce((sum, c) => sum + c.total, 0).toLocaleString("es-MX")}`;
      document.getElementById("comprasMesSub").textContent =
         `${comprasPage.totalElements} órdenes`;

      /* Tabla stock bajo */
      if (puedeVerStockBajo) {
         const tbody = document.getElementById("tablaStockBajo");
         document.getElementById("badgeStockBajo").textContent =
            `${stockBajo.length} alertas`;

         if (stockBajo.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" class="text-center text-muted py-3">
               Sin alertas de stock bajo</td></tr>`;
         } else {
            tbody.innerHTML = stockBajo
               .map(
                  (p) => `
               <tr>
                  <td>${p.nombre}</td>
                  <td><code>${p.codigoBarras}</code></td>
                  <td class="stock-low">${p.stock}</td>
                  <td>${p.stockMinimo}</td>
               </tr>
            `,
               )
               .join("");
         }
      }
   } catch (error) {
      console.error("Error cargando dashboard:", error);
   }
}

cargarDashboard();
