document.addEventListener("DOMContentLoaded", () => {
    const popup = document.getElementById("delete-popup");
    const confirmBtn = document.getElementById("confirm-delete");
    const cancelBtn = document.getElementById("cancel-delete");
    const toast = document.getElementById("toast-notification");
    let selectedCarId = null;

    // Charger les voitures masquées depuis localStorage
    const carsMasquees = JSON.parse(localStorage.getItem("masquees")) || [];

    // Appliquer le masquage aux voitures déjà masquées
    carsMasquees.forEach(carId => {
        const row = document.getElementById("car-row-" + carId);
        if (row) {
            row.style.display = "none";
        }
    });

    // Ouvrir la popup de confirmation sur clic du bouton
    document.querySelectorAll(".btn-danger").forEach(button => {
        button.addEventListener("click", () => {
            selectedCarId = button.getAttribute("data-car-id");
            popup.classList.remove("hidden");
        });
    });

    // Annuler l'action et fermer la popup
    cancelBtn.addEventListener("click", () => {
        popup.classList.add("hidden");
        selectedCarId = null;
    });

    // Confirmer la suppression (masquer la voiture dans la table)
    confirmBtn.addEventListener("click", () => {
        if (selectedCarId) {
            const row = document.getElementById("car-row-" + selectedCarId);
            if (row) {
                row.style.transition = "opacity 0.4s ease";
                row.style.opacity = 0;
                setTimeout(() => row.style.display = "none", 400); // Masquer visuellement la ligne
                
                // Ajouter l'ID de la voiture au localStorage pour la garder masquée
                if (!carsMasquees.includes(selectedCarId)) {
                    carsMasquees.push(selectedCarId);
                    localStorage.setItem("masquees", JSON.stringify(carsMasquees));
                }

                showToast("Voiture masquée avec succès ✔️");
            }
        }
        popup.classList.add("hidden");
        selectedCarId = null;
    });

    // Fonction pour afficher un toast de notification
    function showToast(message) {
        toast.textContent = message;
        toast.classList.remove("hidden");
        toast.classList.add("show");
        setTimeout(() => {
            toast.classList.remove("show");
            setTimeout(() => toast.classList.add("hidden"), 500);
        }, 3000);
    }
});


