const BASE_URL = "http://localhost:8080";

if (localStorage.getItem("accessToken")) {
   window.location.href = "dashboard.html";
}

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

      // Deshabilitar botón mientras carga
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

         // Guardar tokens y redirigir al dashboard
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
