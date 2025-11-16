document.addEventListener('DOMContentLoaded', function () {
    let unreadNotifications = 0;

    // Fonction pour afficher les notifications
    function showNotifications() {
        let notificationBox = document.querySelector('.notification-dropdown');
        notificationBox.innerHTML = ''; // Clear any old notifications

        // Simuler des notifications (en fonction de votre logique serveur)
        let notifications = [
            { message: 'Nouvelle Voiture Disponible', type: 'info' },
            { message: 'Réservation Confirmée', type: 'warning' },
            { message: 'Rappel: Retour de votre voiture demain', type: 'success' }
        ];

        notifications.forEach(function (notification) {
            let alertDiv = document.createElement('div');
            alertDiv.classList.add('alert', `alert-${notification.type}`);
            alertDiv.innerHTML = `
                <strong>${notification.message}</strong>
                <button class="close" onclick="closeNotification(this)">&times;</button>
            `;
            notificationBox.appendChild(alertDiv);
        });

        // Réinitialiser et mettre à jour le badge de notification
        updateNotificationBadge();
    }

    // Mettre à jour le badge de notification
    function updateNotificationBadge() {
        let badge = document.querySelector('.notification-icon .badge');
        if (unreadNotifications > 0) {
            badge.style.display = 'block';
            badge.textContent = unreadNotifications;
        } else {
            badge.style.display = 'none';
        }
    }

    // Ouvrir/fermer les notifications lorsque l'icône est cliquée
    document.querySelector('#notificationIcon').addEventListener('click', function () {
        let notificationBox = document.querySelector('.notification-dropdown');
        notificationBox.classList.toggle('show');

        // Modifier la couleur de l'icône après un clic
        this.querySelector('i').classList.toggle('clicked'); // Ajoute ou enlève la classe 'clicked'
    });

    // Marquer une notification comme lue ou la fermer
    function closeNotification(button) {
        let notificationBox = button.parentElement;
        notificationBox.classList.add('fade-out');
        setTimeout(() => {
            notificationBox.remove();
            unreadNotifications--; // Décrémenter le nombre de notifications non lues
            updateNotificationBadge();
        }, 500); // Temps pour l'animation de disparition
    }

    // Ajouter une notification toutes les 5 secondes pour la démonstration
    setInterval(function () {
        unreadNotifications++;
        showNotifications();
    }, 5000); // Toutes les 5 secondes, simuler une nouvelle notification

    // Initialiser les notifications dès le départ
    showNotifications();
});
