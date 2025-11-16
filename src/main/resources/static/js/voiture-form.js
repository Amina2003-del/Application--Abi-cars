document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("addCarForm");
    const alertContainer = document.getElementById("alertContainer");
    const modal = document.getElementById("addCarModal");
    const addCarBtn = document.getElementById("addCarBtn");
    const closeModal = document.querySelector(".close-modal");
    const imageInput = document.getElementById("carImages");
    const imagePreview = document.getElementById("imagePreviewContainer");

    // Vérifier si les éléments existent
    console.log("Form:", form);
    console.log("Alert container:", alertContainer);
    console.log("Modal:", modal);
    console.log("Image input:", imageInput);
    console.log("Image preview:", imagePreview);

    // Ouvrir le modal
    addCarBtn.addEventListener("click", function () {
        modal.style.display = "block";
    });

    // Fermer le modal avec le bouton X
    closeModal.addEventListener("click", function () {
        modal.style.display = "none";
    });

    // Fermer le modal si on clique en dehors du contenu
    window.addEventListener("click", function (event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });

    // Gestion du formulaire d'ajout de voiture
    if (form) {
        form.addEventListener("submit", function (event) {
            event.preventDefault();
            console.log("Formulaire soumis");
            
            const formData = new FormData(form);
            
            // Gérer l'option "disponible"
            const disponibleCheckbox = document.querySelector('input[name="disponible"]');
            if (disponibleCheckbox) {
                formData.set('disponible', disponibleCheckbox.checked);
            }
            
            // Gérer le type de boîte
            const typeBoiteRadio = document.querySelector('input[name="typeBoite"]:checked');
            if (typeBoiteRadio) {
                formData.set('typeBoite', typeBoiteRadio.value);
            }
            
            // Gérer les images
            if (imageInput && imageInput.files.length > 0) {
                // Supprimer toute entrée existante pour 'images'
                formData.delete('images');
                
                for (let i = 0; i < imageInput.files.length; i++) {
                    formData.append('images', imageInput.files[i]);
                }
            }
            
            // Afficher les données du formulaire pour déboguer
            console.log("Envoi du formulaire...");
            for (let pair of formData.entries()) {
                console.log(pair[0] + ': ' + pair[1]);
            }
            
            // Envoyer la requête Ajax
            fetch('http://localhost:8082/api/cars/add', {
                method: 'POST',
                body: formData
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erreur lors de l\'ajout de la voiture');
                }
                return response.json();
            })
            .then(data => {
                // Afficher un message de succès
                alertContainer.innerHTML = '<div class="alert alert-success">Voiture ajoutée avec succès!</div>';
                alertContainer.style.display = "block";
                
                // Fermer le modal après un petit délai
                setTimeout(() => {
                    modal.style.display = "none";
                    form.reset(); // Réinitialiser le formulaire
                    // Vider la prévisualisation des images
                    if (imagePreview) {
                        imagePreview.innerHTML = 'Cliquez ou glissez des images ici';
                    }
                    alertContainer.style.display = "none";
                }, 1500);
            })
            .catch(error => {
                console.error('Erreur:', error);
                alertContainer.innerHTML = `<div class="alert alert-danger">Erreur lors de l'ajout de la voiture: ${error.message}</div>`;
                alertContainer.style.display = "block";
            });
        });
    } else {
        console.error("Le formulaire #addCarForm n'a pas été trouvé!");
    }

    // Gestion de l'aperçu des images
    if (imageInput) {
        imageInput.addEventListener('change', function() {
            console.log("Sélection d'images");
            updateImagePreview(this.files);
        });

        function updateImagePreview(files) {
            if (!imagePreview) return;
            
            imagePreview.innerHTML = '';
            
            if (files.length > 0) {
                for (let i = 0; i < Math.min(files.length, 5); i++) {
                    const file = files[i];
                    const reader = new FileReader();
                    
                    reader.onload = function(e) {
                        const img = document.createElement('img');
                        img.src = e.target.result;
                        img.height = 100;
                        img.classList.add('preview-image');
                        imagePreview.appendChild(img);
                    };
                    
                    reader.readAsDataURL(file);
                }
                
                if (files.length > 5) {
                    const moreLabel = document.createElement('span');
                    moreLabel.innerText = `+ ${files.length - 5} images de plus`;
                    moreLabel.classList.add('more-images');
                    imagePreview.appendChild(moreLabel);
                }
            } else {
                imagePreview.innerText = 'Cliquez ou glissez des images ici';
            }
        }
    } else {
        console.error("L'input d'images #carImages n'a pas été trouvé!");
    }

    // Initialiser le datepicker pour l'année
    $('.datepicker-year').datepicker({
        format: "yyyy",
        viewMode: "years", 
        minViewMode: "years",
        autoclose: true
    });
});