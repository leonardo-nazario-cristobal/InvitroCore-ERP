const BASE_URL = "http://localhost:8080";

async function apiFetch(endpoint, options = {}) {
   const token = localStorage.getItem("accessToken");

   const response = await fetch(`${BASE_URL}${endpoint}`, {
      ...options,
      headers: {
         "Content-Type": "application/json",
         Authorization: `Bearer ${token}`,
         ...options.headers,
      },
   });

   if (response.status === 401) {
      localStorage.clear();
      window.location.href = "index.html";
      return;
   }

   return response;
}
