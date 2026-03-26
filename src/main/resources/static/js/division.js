let modalDivisionInstance;

async function openModalDivision(id = null) {
  try {
    if (!modalDivisionInstance) {
      modalDivisionInstance = new bootstrap.Modal(
        document.getElementById("modalDivision"),
      );
    }

    limpiarModal();

    if (id) {
      const response = await fetch(`/admin/division/api/${id}`);
      if (!response.ok) throw new Error("No se pudo cargar la división");

      const division = await response.json();
      document.getElementById("divisionId").value = division.id;
      document.getElementById("divisionNombre").value = division.nombreDivision;
      document.getElementById("divisionClave").value = division.clave;
      document.getElementById("activo").checked = division.activo;

      document.getElementById("modalDivisionLabel").textContent =
        "Editar División";
    } else {
      document.getElementById("modalDivisionLabel").textContent =
        "Nueva División";
    }

    modalDivisionInstance.show();
  } catch (error) {
    console.error("Error", error);
    alert("Error al abrir el formulario: " + error.message);
  }
}

function limpiarModal() {
  document.getElementById("divisionForm").classList.remove("was-validated");
  document.getElementById("divisionId").value = "";
  document.getElementById("divisionNombre").value = "";
  document.getElementById("divisionClave").value = "";
  document.getElementById("activo").checked = false;
}

async function guardarDivision() {
  const form = document.getElementById("divisionForm");
  form.classList.add("was-validated");

  if (!form.checkValidity()) return;

  try {
    const data = {
      id: document.getElementById("divisionId").value
        ? parseInt(document.getElementById("divisionId").value)
        : null,
      nombreDivision: document.getElementById("divisionNombre").value,
      clave: document.getElementById("divisionClave").value,
      activo: document.getElementById("activo").checked,
    };

    console.log("Enviando:", data);

    const response = await fetch("/admin/division/api/save", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    const result = await response.json();

    if (result.success) {
      modalDivisionInstance.hide();
      setTimeout(() => window.location.reload(), 500);
    } else {
      alert(result.message || "Error al guardar");
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Error al guardar: " + error.message);
  }
}
