// main.js - Gym & Fitness Management System
// Author: Kaveesha (Fitness Hub)
// Version: 2026

document.addEventListener('DOMContentLoaded', function () {
    // =============================================
    // 1. Auto-hide alerts after 5 seconds
    // =============================================
    const alerts = document.querySelectorAll('.alert-success, .alert-error');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // =============================================
    // 2. Basic form validation for required fields
    // =============================================
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function (e) {
            let isValid = true;
            const requiredFields = form.querySelectorAll('[required]');

            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    field.classList.add('error');
                    isValid = false;

                    // Show inline error message if not already present
                    let errorSpan = field.nextElementSibling;
                    if (!errorSpan || !errorSpan.classList.contains('field-error')) {
                        errorSpan = document.createElement('span');
                        errorSpan.className = 'field-error';
                        errorSpan.textContent = 'This field is required';
                        field.parentNode.appendChild(errorSpan);
                    }
                } else {
                    field.classList.remove('error');
                    const errorSpan = field.nextElementSibling;
                    if (errorSpan && errorSpan.classList.contains('field-error')) {
                        errorSpan.remove();
                    }
                }
            });

            if (!isValid) {
                e.preventDefault();
                alert('Please fill in all required fields.');
            }
        });
    });

    // Remove error class when user starts typing
    document.querySelectorAll('input[required], textarea[required], select[required]').forEach(field => {
        field.addEventListener('input', function () {
            this.classList.remove('error');
            const errorSpan = this.nextElementSibling;
            if (errorSpan && errorSpan.classList.contains('field-error')) {
                errorSpan.remove();
            }
        });
    });

    // =============================================
    // 3. Confirm dialogs for dangerous actions
    // =============================================
    document.querySelectorAll('[data-confirm]').forEach(button => {
        button.addEventListener('click', function (e) {
            const message = this.getAttribute('data-confirm') || 'Are you sure you want to proceed?';
            if (!confirm(message)) {
                e.preventDefault();
            }
        });
    });

    // Example usage in HTML:
    // <button data-confirm="Cancel this booking?">Cancel</button>

    // =============================================
    // 4. Interactive Rating Stars (hover & click preview)
    // =============================================
    const ratingContainers = document.querySelectorAll('.rating-stars');
    ratingContainers.forEach(container => {
        const stars = container.querySelectorAll('label');
        const input = container.querySelector('input[type="radio"]');

        stars.forEach(star => {
            star.addEventListener('mouseover', function () {
                const rating = parseInt(this.getAttribute('for').replace('star', ''));
                highlightStars(container, rating);
            });

            star.addEventListener('mouseout', function () {
                const selected = container.querySelector('input:checked');
                highlightStars(container, selected ? parseInt(selected.value) : 0);
            });

            star.addEventListener('click', function () {
                const rating = parseInt(this.getAttribute('for').replace('star', ''));
                highlightStars(container, rating);
            });
        });

        function highlightStars(container, rating) {
            container.querySelectorAll('label').forEach((star, index) => {
                if (index < rating) {
                    star.style.color = '#ffcc00';
                } else {
                    star.style.color = '#444';
                }
            });
        }

        // Initialize with pre-selected value (if editing)
        const preSelected = container.querySelector('input:checked');
        if (preSelected) {
            highlightStars(container, parseInt(preSelected.value));
        }
    });

    // =============================================
    // 5. Mobile Navbar Toggle (hamburger menu)
    // =============================================
    const navToggle = document.querySelector('.nav-toggle');
    const navLinks = document.querySelector('.nav-links');

    if (navToggle && navLinks) {
        navToggle.addEventListener('click', () => {
            navLinks.classList.toggle('active');
            navToggle.classList.toggle('active');
        });
    }

    // Close mobile menu when clicking a link
    document.querySelectorAll('.nav-links a').forEach(link => {
        link.addEventListener('click', () => {
            if (navLinks.classList.contains('active')) {
                navLinks.classList.remove('active');
                navToggle.classList.remove('active');
            }
        });
    });

    // =============================================
    // 6. Smooth scroll for anchor links
    // =============================================
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    console.log('Fitness Hub JavaScript initialized successfully!');
});