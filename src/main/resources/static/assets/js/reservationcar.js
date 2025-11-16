// Loader Timeout (1 second)
      setTimeout(() => {
          document.getElementById('loader').style.display = 'none';
      }, 1000); // 1 second = 1000 ms

      // Flatpickr Initialization
      flatpickr("#startDate", {
          dateFormat: "d/m/Y",
          minDate: "today",
          locale: "fr"
      });
      flatpickr("#endDate", {
          dateFormat: "d/m/Y",
          minDate: "today",
          locale: "fr"
      });

      // Price Range Slider
      const priceRange = document.getElementById('priceRange');
      const priceValue = document.getElementById('priceValue');
      priceRange.addEventListener('input', () => {
          priceValue.textContent = priceRange.value;
      });

      // Mock Car Data
      const cars = [
          { id: 1, brand: "Nissan", model: "370Z", price: 75, type: "suv", transmission: "automatic", fuel: "electric", image: "/api/placeholder/300/200", discount: 10, available: true },
          { id: 2, brand: "Audi", model: "Q3", price: 45, type: "suv", transmission: "automatic", fuel: "petrol", image: "/api/placeholder/300/200", discount: 0, available: true },
          // Add more cars as needed
      ];

      // Search Form Submission
      document.getElementById('searchForm').addEventListener('submit', (e) => {
          e.preventDefault();
          const city = document.getElementById('citySelect').value;
          const startDate = document.getElementById('startDate').value;
          const endDate = document.getElementById('endDate').value;
          const carType = document.getElementById('carType').value;

          document.getElementById('loader').style.display = 'flex';
          setTimeout(() => {
              document.getElementById('loader').style.display = 'none';
              displayCars(cars.filter(car => 
                  (!city || car.city === city) &&
                  (carType === 'all' || car.type === carType)
              ));
          }, 1000);
      });

      // Apply Filters
      document.getElementById('applyFilters').addEventListener('click', () => {
          const priceMax = parseInt(priceRange.value);
          const brands = Array.from(document.querySelectorAll('.brand-filter:checked')).map(checkbox => checkbox.value);
          const options = Array.from(document.querySelectorAll('.option-filter:checked')).map(checkbox => checkbox.value);
          const sortBy = document.getElementById('sortBy').value;

          let filteredCars = [...cars].filter(car => 
              car.price <= priceMax &&
              (brands.length === 0 || brands.includes(car.brand.toLowerCase())) &&
              (options.length === 0 || options.every(opt => car.features && car.features.includes(opt)))
          );

          // Sorting
          if (sortBy === 'price-asc') {
              filteredCars.sort((a, b) => a.price - b.price);
          } else if (sortBy === 'price-desc') {
              filteredCars.sort((a, b) => b.price - a.price);
          } else if (sortBy === 'name-asc') {
              filteredCars.sort((a, b) => (a.brand + a.model).localeCompare(b.brand + b.model));
          } else if (sortBy === 'name-desc') {
              filteredCars.sort((a, b) => (b.brand + b.model).localeCompare(a.brand + a.model));
          }

          displayCars(filteredCars);
      });

      // Display Cars
      function displayCars(cars) {
          const container = document.getElementById('carsContainer');
          container.innerHTML = '';
          if (cars.length === 0) {
              container.innerHTML = '<div class="text-center p-5"><h4>Aucune voiture disponible</h4><p class="text-muted">Modifiez vos critères de recherche</p></div>';
              return;
          }
          const row = document.createElement('div');
          row.className = 'row g-4';
          cars.forEach(car => {
              const col = document.createElement('div');
              col.className = 'col-md-6';
              col.innerHTML = `
                  <div class="car-card" data-car-id="${car.id}" data-car-name="${car.brand} ${car.model}" data-car-type="${car.type}" 
                       data-car-fuel="${car.fuel}" data-car-transmission="${car.transmission}" data-car-price="${car.price}" 
                       data-car-image="${car.image}">
                      ${car.discount > 0 ? `<div class="car-discount"><i class="fas fa-tags"></i>${car.discount}% OFF</div>` : ''}
                      <div class="car-badge">${car.available ? 'Disponible' : 'Indisponible'}</div>
                      <img src="${car.image}" class="car-img" alt="${car.brand} ${car.model}">
                      <div class="card-body p-4">
                          <h5 class="card-title">${car.brand} ${car.model}</h5>
                          <div class="car-features">
                              <div class="car-feature"><i class="fas fa-car-side"></i>${car.type}</div>
                              <div class="car-feature"><i class="fas fa-gas-pump"></i>${car.fuel}</div>
                              <div class="car-feature"><i class="fas fa-cog"></i>${car.transmission}</div>
                          </div>
                          <div class="d-flex justify-content-between align-items-center">
                              <div class="price-tag"><i class="fas fa-euro-sign"></i>${car.price}€/jour</div>
                              <div>
                                  <button class="btn btn-rent me-2 rent-btn">Louer</button>
                                  <a href="/car-details/${car.id}" class="btn btn-details">Détails</a>
                              </div>
                          </div>
                      </div>
                  </div>
              `;
              row.appendChild(col);
          });
          container.appendChild(row);

          // Add event listeners to rent buttons
          document.querySelectorAll('.rent-btn').forEach(button => {
              button.addEventListener('click', (e) => {
                  const carCard = e.target.closest('.car-card');
                  const carId = carCard.getAttribute('data-car-id');
                  const carName = carCard.getAttribute('data-car-name');
                  const carType = carCard.getAttribute('data-car-type');
                  const carFuel = carCard.getAttribute('data-car-fuel');
                  const carTransmission = carCard.getAttribute('data-car-transmission');
                  const carPrice = carCard.getAttribute('data-car-price');
                  const carImage = carCard.getAttribute('data-car-image');
                  const startDate = document.getElementById('startDate').value;
                  const endDate = document.getElementById('endDate').value;
                  const location = document.getElementById('citySelect').value;

                  // Fill modal fields
                  document.getElementById('formCarId').value = carId;
                  document.getElementById('selectedCarName').textContent = carName;
                  document.getElementById('selectedCarType').textContent = carType;
                  document.getElementById('selectedCarFuel').textContent = carFuel;
                  document.getElementById('selectedCarTransmission').textContent = carTransmission;
                  document.getElementById('selectedCarPrice').textContent = carPrice;
                  document.getElementById('selectedCarImage').src = carImage;
                  document.getElementById('reservationDates').textContent = `du ${startDate} au ${endDate}`;
                  document.getElementById('formStartDate').value = startDate;
                  document.getElementById('formEndDate').value = endDate;
                  document.getElementById('formLocation').value = location;

                  // Reset modal steps
                  resetModalSteps();
                  document.getElementById('step1Content').style.display = 'block';

                  // Show modal
                  const modal = new bootstrap.Modal(document.getElementById('reservationModal'));
                  modal.show();
              });
          });
      }

      // Reservation Modal Steps
      function resetModalSteps() {
          document.querySelectorAll('.step').forEach(step => step.classList.remove('step-active', 'step-completed'));
          document.getElementById('step1').classList.add('step-active');
          document.querySelectorAll('.modal-body > div[id$="Content"]').forEach(content => content.style.display = 'none');
      }

      document.getElementById('goToStep2').addEventListener('click', () => {
          document.getElementById('step1').classList.remove('step-active');
          document.getElementById('step1').classList.add('step-completed');
          document.getElementById('step2').classList.add('step-active');
          document.getElementById('step1Content').style.display = 'none';
          document.getElementById('step2Content').style.display = 'block';
      });

      document.getElementById('backToStep1').addEventListener('click', () => {
          document.getElementById('step2').classList.remove('step-active');
          document.getElementById('step1').classList.remove('step-completed');
          document.getElementById('step1').classList.add('step-active');
          document.getElementById('step2Content').style.display = 'none';
          document.getElementById('step1Content').style.display = 'block';
      });

      document.getElementById('goToStep3').addEventListener('click', () => {
          document.getElementById('step2').classList.remove('step-active');
          document.getElementById('step2').classList.add('step-completed');
          document.getElementById('step3').classList.add('step-active');
          document.getElementById('step2Content').style.display = 'none';
          document.getElementById('step3Content').style.display = 'block';

          // Fill confirmation details
          const firstName = document.getElementById('firstName').value;
          const lastName = document.getElementById('lastName').value;
          const email = document.getElementById('email').value;
          const carName = document.getElementById('selectedCarName').textContent;
          const dates = document.getElementById('reservationDates').textContent;
          const location = document.getElementById('formLocation').value;
          const price = document.getElementById('selectedCarPrice').textContent;
         // const paymentMethod = document.querySelector('.payment-option.selected')?.getAttribute('data-payment') === 'online' ? 'Paiement en ligne' : 'Paiement à la livraison';

          document.getElementById('confirmationEmail').textContent = email;
          document.getElementById('summaryCarName').textContent = carName;
          document.getElementById('summaryDates').textContent = dates;
          document.getElementById('summaryLocation').textContent = location;
          document.getElementById('summaryPrice').textContent = price;
          document.getElementById('summaryPayment').textContent = paymentMethod;

          // Submit form via AJAX
          const formData = new FormData(document.getElementById('reservationForm'));
          fetch('/reservation/submit', {
              method: 'POST',
              body: new URLSearchParams(formData).toString(),
              headers: {
                  'Content-Type': 'application/x-www-form-urlencoded'
              }
          })
          .then(response => response.json())
          .then(data => {
              document.getElementById('reservationNumber').textContent = data.reservationNumber || 'RES-12345';
          })
          .catch(error => {
              console.error('Error:', error);
          });
      });

      // Payment Option Selection
      document.querySelectorAll('.payment-option').forEach(option => {
          option.addEventListener('click', () => {
              document.querySelectorAll('.payment-option').forEach(opt => opt.classList.remove('selected'));
              option.classList.add('selected');
              if (option.getAttribute('data-payment') === 'online') {
                  document.getElementById('onlinePaymentForm').style.display = 'block';
              } else {
                  document.getElementById('onlinePaymentForm').style.display = 'none';
              }
          });
      });