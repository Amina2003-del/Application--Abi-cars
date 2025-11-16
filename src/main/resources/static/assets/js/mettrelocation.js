document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('locationForm');

    form.addEventListener('submit', function(e) {
        const marque = document.getElementById('marque').value.trim();
        const modele = document.getElementById('modele').value.trim();
        const prix = document.getElementById('prix').value.trim();

        if (marque.length < 2 || modele.length < 2) {
            alert('Veuillez saisir des informations valides pour la marque et le modèle.');
            e.preventDefault();
        } else if (prix <= 0) {
            alert('Le prix doit être supérieur à zéro.');
            e.preventDefault();
        }
    });
});
