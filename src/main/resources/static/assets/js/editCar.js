document.getElementById('submitEditCar').addEventListener('click', function() {
    const carId = document.getElementById('editCarId').value;
    const updatedCar = {
        id: carId,
        marque: document.getElementById('editCarMake').value,
        modele: document.getElementById('editCarModel').value,
        immatriculation: document.getElementById('editCarPlate').value,
        annee: document.getElementById('editCarYear').value,
        proprietaireId: document.getElementById('editCarOwner').value,
        prixParJour: document.getElementById('editCarPrice').value,
        description: document.getElementById('editCarDescription').value,
        statutApprobation: document.getElementById('editCarApprovalStatus').value,
        statutDisponibilite: document.getElementById('editCarAvailabilityStatus').value
    };

    fetch(`/api/cars/${carId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updatedCar)
    })
    .then(response => {
        if (response.ok) {
            alert('Modifications enregistrées avec succès.');
            const modal = bootstrap.Modal.getInstance(document.getElementById('editCarModal'));
            modal.hide();
            refreshTable();
        } else {
            throw new Error('Échec de la mise à jour.');
        }
    })
    .catch(error => {
        document.getElementById('modalError').textContent = 'Erreur lors de la mise à jour des données.';
        document.getElementById('modalError').style.display = 'block';
    });
	
	
	
});