document.addEventListener('DOMContentLoaded', function() {
           const searchInput = document.getElementById('searchInput');
           const statusFilter = document.getElementById('statusFilter');
           const rows = document.querySelectorAll('tbody tr');
           
           function filterReservations() {
               const searchTerm = searchInput.value.toLowerCase();
               const statusValue = statusFilter.value;
               
               rows.forEach(row => {
                   const text = row.textContent.toLowerCase();
                   const status = row.querySelector('td:nth-child(6)').textContent.trim();
                   
                   const matchesSearch = text.includes(searchTerm);
                   const matchesStatus = statusValue === 'all' || 
                                       (statusValue === 'PENDING' && status === 'En attente') ||
                                       (statusValue === 'CONFIRMED' && status === 'Confirmée') ||
                                       (statusValue === 'CANCELLED' && status === 'Annulée');
                   
                   if (matchesSearch && matchesStatus) {
                       row.style.display = '';
                   } else {
                       row.style.display = 'none';
                   }
               });
           }
           
           searchInput.addEventListener('input', filterReservations);
           statusFilter.addEventListener('change', filterReservations);
       });