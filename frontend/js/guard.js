const token = localStorage.getItem("accessToken");

if (!token) {
   window.location.href = "index.html";
}

/* Páginas permitidas por rol */

const paginaActual = window.location.pathname.split("/").pop();
const rolGuard = localStorage.getItem("rol");

const permisos = {
   "dashboard.html": ["admin", "cajero", "ventas", "compras"],
   "usuarios.html": ["admin"],
   "categorias.html": ["admin", "compras"],
   "proveedores.html": ["admin", "compras"],
   "productos.html": ["admin", "cajero", "ventas", "compras"],
   "compras.html": ["admin", "compras"],
   "ventas.html": ["admin", "cajero", "ventas"],
   "movimientos.html": ["admin", "compras"],
};

const rolesPermitidos = permisos[paginaActual];

if (rolesPermitidos && !rolesPermitidos.includes(rolGuard)) {
   window.location.href = "dashboard.html";
}
