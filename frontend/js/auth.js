const BASE_URL = "http://localhost:8080";

/* Verificar si el token existente es válido antes de redirigir */
async function verificarTokenYRedirigir() {
   const token = localStorage.getItem("accessToken");
   if (!token) return;

   try {
      const res = await fetch(`${BASE_URL}/api/productos?pagina=0&tamanio=1`, {
         headers: { Authorization: `Bearer ${token}` },
      });

      if (res.ok) {
         // Token válido → ir al dashboard
         window.location.href = "dashboard.html";
      } else {
         // Token inválido o expirado → limpiar y quedarse en login
         localStorage.clear();
      }
   } catch (error) {
      localStorage.clear();
   }
}

verificarTokenYRedirigir();

document.getElementById("verPassword").addEventListener("change", function () {
   const input = document.getElementById("password");
   input.type = this.checked ? "text" : "password";
});

document
   .getElementById("formLogin")
   .addEventListener("submit", async function (e) {
      e.preventDefault();

      const correo = document.getElementById("correo").value.trim();
      const password = document.getElementById("password").value.trim();
      const alerta = document.getElementById("alerta");
      const btn = document.getElementById("btnLogin");

      btn.disabled = true;
      btn.textContent = "Iniciando sesión...";
      alerta.classList.add("d-none");

      try {
         const response = await fetch(`${BASE_URL}/api/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ correo, password }),
         });

         const data = await response.json();

         if (!response.ok) {
            throw new Error(data.message || "Correo o contraseña incorrectos");
         }

         localStorage.setItem("accessToken", data.accessToken);
         localStorage.setItem("refreshToken", data.refreshToken);
         localStorage.setItem("nombre", data.nombre);
         localStorage.setItem("correo", data.correo);
         localStorage.setItem("rol", data.rol);

         window.location.href = "dashboard.html";
      } catch (error) {
         alerta.textContent = error.message;
         alerta.classList.remove("d-none");
      } finally {
         btn.disabled = false;
         btn.textContent = "Iniciar sesión";
      }
   });
