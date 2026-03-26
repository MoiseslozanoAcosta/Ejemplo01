let modalOfertaInstance;

async function openModalOferta(id = null) {
  try {
    if (!modalOfertaInstance) {
      modalOfertaInstance = new bootstrap.Modal(
        document.getElementById("modalOferta"),
      );
    }

    limpiarModalOferta();

    if (id) {
      const response = await fetch(`/admin/oferta/api/${id}`);
      if (!response.ok) throw new Error("No se pudo cargar la oferta");

      const oferta = await response.json();

      document.getElementById("ofertaId").value = oferta.id;
      document.getElementById("ofertaNombre").value = oferta.nombreOferta;
      document.getElementById("ofertaModalidad").value = oferta.modalidad;
      document.getElementById("ofertaImagen").value = oferta.imagen;
      document.getElementById("ofertaDivision").value = oferta.division?.id ?? "";

      document.getElementById("modalOfertaLabel").textContent = "Editar Oferta";
    } else {
      document.getElementById("modalOfertaLabel").textContent = "Nueva Oferta";
    }

    modalOfertaInstance.show();
  } catch (error) {
    console.error("Error:", error);
    alert("Error al abrir el formulario: " + error.message);
  }
}

function limpiarModalOferta() {
  document.getElementById("ofertaForm").classList.remove("was-validated");
  document.getElementById("ofertaId").value = "";
  document.getElementById("ofertaNombre").value = "";
  document.getElementById("ofertaModalidad").value = "Presencial";
  document.getElementById("ofertaImagen").value = "";
  document.getElementById("ofertaDivision").value = "";
}

async function guardarOferta() {
  const form = document.getElementById("ofertaForm");
  form.classList.add("was-validated");

  if (!form.checkValidity()) return;

  try {
    const data = {
      id: document.getElementById("ofertaId").value
        ? parseInt(document.getElementById("ofertaId").value)
        : null,
      nombreOferta: document.getElementById("ofertaNombre").value,
      modalidad: document.getElementById("ofertaModalidad").value,
      imagen: document.getElementById("ofertaImagen").value,
      division: {
        id: parseInt(document.getElementById("ofertaDivision").value),
      },
    };

    console.log("Enviando:", data);

    const response = await fetch("/admin/oferta/api/save", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    const result = await response.json();

    if (result.success) {
      modalOfertaInstance.hide();
      actualizarTarjetaOferta(data, result.id);
    } else {
      alert(result.message || "Error al guardar");
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Error al guardar: " + error.message);
  }
}

function actualizarTarjetaOferta(data, id) {
  let tarjeta = document.querySelector(`[data-oferta-id="${id}"]`);

  if (tarjeta) {
    // ✅ Usando las clases correctas que ahora tiene el HTML
    const titulo = tarjeta.querySelector(".card-title");
    const modalidad = tarjeta.querySelector(".card-text");
    const img = tarjeta.querySelector(".card-img");

    if (titulo) titulo.textContent = data.nombreOferta;
    if (modalidad) modalidad.textContent = data.modalidad;
    if (img) img.src = data.imagen;

  } else {
    // ✅ Nueva tarjeta con las mismas clases
    const grid = document.getElementById("ofertasGrid");
    const col = document.createElement("div");
    col.className = "col";
    col.innerHTML = `
      <div class="card h-100 shadow-sm" data-oferta-id="${id}">
        <div class="card-body text-center p-4">
          <div class="icon-box mb-3">
            <img src="${data.imagen}" class="card-img"
                 style="width: 50px; height: 50px; object-fit: contain;" alt="Icono">
          </div>
          <h5 class="fw-bold mb-1 card-title">${data.nombreOferta}</h5>
          <p class="badge bg-primary-subtle text-primary mb-3 card-text">${data.modalidad}</p>
          <div class="d-flex justify-content-center gap-2 mt-2">
            <button class="btn btn-sm btn-info w-50" onclick="openModalOferta(${id})">Editar JS</button>
            <a href="/admin/oferta/edit/${id}" class="btn btn-sm btn-warning w-50">Editar Type</a>
          </div>
        </div>
      </div>
    `;
    grid.appendChild(col);
  }
}