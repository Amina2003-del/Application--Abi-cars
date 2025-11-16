
    // Gestion des images en grand format
    document.addEventListener('DOMContentLoaded', function() {
        const evidenceImages = document.querySelectorAll('.evidence-img');
        const expandedImage = document.getElementById('expandedImage');
        
        evidenceImages.forEach(img => {
            img.addEventListener('click', function() {
                expandedImage.src = this.src;
            });
        });
        
        // Filtrage des litiges
        const disputeFilter = document.getElementById('disputeFilter');
        const disputeCards = document.querySelectorAll('.dispute-card');
        
        disputeFilter.addEventListener('change', function() {
            const filterValue = this.value;
            
            disputeCards.forEach(card => {
                if (filterValue === 'all' || 
                    (filterValue === 'pending' && card.classList.contains('pending')) ||
                    (filterValue === 'resolved' && card.classList.contains('resolved')) ||
                    (filterValue === 'escalated' && card.classList.contains('escalated'))) {
                    card.style.display = '';
                } else {
                    card.style.display = 'none';
                }
            });
        });
        
        // Gestion du modal de litige
        const disputeModal = document.getElementById('disputeModal');
        if (disputeModal) {
            disputeModal.addEventListener('show.bs.modal', function(event) {
                const button = event.relatedTarget;
                const disputeId = button.getAttribute('data-dispute-id');
                // Ici, vous pourriez charger les données du litige via AJAX
                // fetchDisputeDetails(disputeId);
            });
        }
    });