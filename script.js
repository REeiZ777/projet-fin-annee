// LBGconnect - JavaScript Functionality

// ============================================
// NAVIGATION & MENU
// ============================================

// ============================================
// HOME SEARCH LOGIC (INDEX.HTML)
// ============================================

const homeResultsGrid = document.getElementById('homeResultsGrid');

if (homeResultsGrid) {
    const homeState = {
        search: '',
        location: '',
        type: '',
        availability: [],
        rating: 1
    };

    const homeSearchInput = document.getElementById('homeSearchInput');
    const homeLocationSelect = document.getElementById('homeLocationSelect');
    const homeTypeSelect = document.getElementById('homeTypeSelect'); // Profil type
    const homeRatingRange = document.getElementById('homeRatingRange');
    // Checkboxes need to be selected carefully from the sidebar in index.html
    // Assuming they are within .filters-sidebar inside index.html
    const homeCheckboxes = document.querySelectorAll('.filters-sidebar input[type="checkbox"]');
    const homeProfileCards = document.querySelectorAll('.profile-card');

    if (homeSearchInput) {
        homeSearchInput.addEventListener('input', debounce((e) => {
            homeState.search = e.target.value.toLowerCase();
            filterHomeProfiles();
        }, 300));
    }

    if (homeLocationSelect) {
        homeLocationSelect.addEventListener('change', (e) => {
            homeState.location = e.target.value.toLowerCase();
            filterHomeProfiles();
        });
    }

    if (homeTypeSelect) {
        homeTypeSelect.addEventListener('change', (e) => {
            homeState.type = e.target.value.toLowerCase();
            filterHomeProfiles();
        });
    }

    if (homeRatingRange) {
        homeRatingRange.addEventListener('input', (e) => {
            homeState.rating = parseInt(e.target.value);
            filterHomeProfiles();
        });
    }

    if (homeCheckboxes.length) {
        homeCheckboxes.forEach(cb => {
            cb.addEventListener('change', () => {
                // Update specific state based on value or group
                // For simplicity, we'll re-read all checked boxes in filter function or update state here
                // Let's rely on reading them dynamically in filter function or simple state update
                filterHomeProfiles();
            });
        });
    }

    function filterHomeProfiles() {
        homeProfileCards.forEach(card => {
            let isVisible = true;

            // Helper to remove accents
            const normalize = (str) => str.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();

            // 1. Text Search
            if (homeState.search) {
                const text = normalize(card.textContent);
                const search = normalize(homeState.search);
                if (!text.includes(search)) {
                    isVisible = false;
                }
            }

            // 2. Location
            if (isVisible && homeState.location) {
                const locText = normalize(card.querySelector('.location').textContent);
                const locFilter = normalize(homeState.location);
                if (!locText.includes(locFilter)) {
                    isVisible = false;
                }
            }

            // 3. Type (Artisan vs Apprenti vs Emploi)
            // Note: Profile cards currently only have data-type="artisan" in my edit.
            // But logic should check that attribute.
            if (isVisible && homeState.type) {
                const type = card.dataset.type; // e.g. "artisan"
                if (type && type !== homeState.type) {
                    // Special case: "emploi" filter might not match "artisan" cards
                    // If filter is specific and card doesn't match, hide.
                    isVisible = false;
                }
            }

            // 4. Rating
            if (isVisible && homeState.rating > 1) {
                const ratingText = card.querySelector('.rating span').textContent; // "(4.5)"
                const rating = parseFloat(ratingText.replace(/[()]/g, ''));
                if (rating < homeState.rating) {
                    isVisible = false;
                }
            }

            // 5. Availability (using Checkboxes)
            if (isVisible) {
                const activeCheckboxes = Array.from(homeCheckboxes)
                    .filter(c => c.checked)
                    .map(c => c.value);

                if (activeCheckboxes.includes('available')) {
                    const badge = card.querySelector('.status-badge');
                    if (!badge || !badge.classList.contains('available')) {
                        isVisible = false;
                    }
                }
                // Add logic for 'weekend' or 'experience' if data attributes are present
            }

            // Apply
            if (isVisible) {
                card.style.display = '';
                card.style.animation = 'none';
                card.offsetHeight;
                card.style.animation = 'fadeIn 0.5s ease-in';
            } else {
                card.style.display = 'none';
            }
        });
    }
}
document.addEventListener('DOMContentLoaded', function () {
    const hamburger = document.querySelector('.hamburger');
    const navLinks = document.querySelector('.nav-links');
    const authButtons = document.querySelector('.auth-buttons');

    if (hamburger) {
        hamburger.addEventListener('click', function () {
            navLinks.classList.toggle('active');
            authButtons.classList.toggle('active');
            hamburger.querySelector('i').classList.toggle('fa-bars');
            hamburger.querySelector('i').classList.toggle('fa-times');
        });
    }

    // Smooth Scroll for Navigation Links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            const href = this.getAttribute('href');
            if (href !== '#' && href.length > 1) {
                e.preventDefault();
                const target = document.querySelector(href);
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                    // Close mobile menu if open
                    navLinks.classList.remove('active');
                    authButtons.classList.remove('active');
                    if (hamburger) {
                        hamburger.querySelector('i').classList.add('fa-bars');
                        hamburger.querySelector('i').classList.remove('fa-times');
                    }
                }
            }
        });
    });

    // Active Navigation Highlighting
    const sections = document.querySelectorAll('section[id]');
    const navItems = document.querySelectorAll('.nav-links a');

    window.addEventListener('scroll', () => {
        let current = '';
        sections.forEach(section => {
            const sectionTop = section.offsetTop;
            const sectionHeight = section.clientHeight;
            if (scrollY >= (sectionTop - 200)) {
                current = section.getAttribute('id');
            }
        });

        navItems.forEach(item => {
            item.classList.remove('active');
            if (item.getAttribute('href') === `#${current}`) {
                item.classList.add('active');
            }
        });
    });
});

// ============================================
// AUTHENTICATION FORMS
// ============================================

// Toggle Password Visibility
function togglePassword(fieldId = 'password') {
    const passwordField = document.getElementById(fieldId);
    const toggleBtn = passwordField.nextElementSibling;
    const icon = toggleBtn.querySelector('i');

    if (passwordField.type === 'password') {
        passwordField.type = 'text';
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    } else {
        passwordField.type = 'password';
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    }
}

// Login Form Validation
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        // Simple validation
        if (!validateEmail(email)) {
            showError('emailError', 'Adresse email invalide');
            return;
        }

        if (password.length < 6) {
            showError('passwordError', 'Le mot de passe doit contenir au moins 6 caractères');
            return;
        }

        // Simulate login success
        showSuccess('Connexion réussie ! Redirection...');
        // Persist login state
        localStorage.setItem('isAuthenticated', 'true');
        localStorage.setItem('user', JSON.stringify({ name: 'Kouamé Jean', role: 'artisan' }));

        setTimeout(() => {
            window.location.href = 'index.html';
        }, 1500);
    });
}

function handleLogout() {
    localStorage.removeItem('isAuthenticated');
    localStorage.removeItem('user');
    showSuccess('Déconnexion réussie. À bientôt !');
    setTimeout(() => {
        window.location.href = 'index.html';
    }, 1000);
}

function checkAuthStatus() {
    const isAuth = localStorage.getItem('isAuthenticated') === 'true';
    const authContainer = document.querySelector('.auth-buttons');

    if (isAuth && authContainer) {
        authContainer.innerHTML = `
            <a href="messages.html" class="btn btn-outline btn-sm" style="border:none; padding: 10px;">
                <i class="fas fa-envelope" style="font-size: 1.2rem;"></i>
            </a>
            <a href="profile.html" class="btn btn-primary btn-sm">
                <i class="fas fa-user-circle"></i>
                Mon Profil
            </a>
            <button onclick="handleLogout()" class="btn btn-outline btn-sm" style="border-color: #e74c3c; color: #e74c3c; margin-left: 5px;">
                <i class="fas fa-sign-out-alt"></i>
            </button>
        `;
    }
}

// Check auth on load
document.addEventListener('DOMContentLoaded', () => {
    checkAuthStatus();
});

// Register Form Validation
const registerForm = document.getElementById('registerForm');
if (registerForm) {
    // Show profession field based on account type
    const accountTypeInputs = document.querySelectorAll('input[name="accountType"]');
    const professionGroup = document.getElementById('professionGroup');

    accountTypeInputs.forEach(input => {
        input.addEventListener('change', function () {
            if (this.value === 'artisan' || this.value === 'apprenti') {
                professionGroup.style.display = 'block';
                document.getElementById('profession').required = true;
            } else {
                professionGroup.style.display = 'none';
                document.getElementById('profession').required = false;
            }
        });
    });

    // Password strength indicator
    const passwordInput = document.getElementById('password');
    const strengthIndicator = document.getElementById('passwordStrength');

    if (passwordInput && strengthIndicator) {
        passwordInput.addEventListener('input', function () {
            const strength = calculatePasswordStrength(this.value);
            strengthIndicator.className = 'password-strength ' + strength.class;
            strengthIndicator.textContent = strength.text;
        });
    }

    // Form submission
    registerForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (password !== confirmPassword) {
            showError('confirmPasswordError', 'Les mots de passe ne correspondent pas');
            return;
        }

        // Simulate registration success
        showSuccess('Inscription réussie ! Redirection...');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 1500);
    });
}

// Helper Functions
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

function showError(elementId, message) {
    const errorElement = document.getElementById(elementId);
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.style.display = 'block';
    }
}

function showSuccess(message) {
    // Create a simple toast notification
    const toast = document.createElement('div');
    toast.className = 'toast success';
    toast.innerHTML = `<i class="fas fa-check-circle"></i> ${message}`;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('show');
    }, 100);

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function calculatePasswordStrength(password) {
    let strength = 0;
    if (password.length >= 8) strength++;
    if (password.match(/[a-z]+/)) strength++;
    if (password.match(/[A-Z]+/)) strength++;
    if (password.match(/[0-9]+/)) strength++;
    if (password.match(/[$@#&!]+/)) strength++;

    switch (strength) {
        case 0:
        case 1:
            return { class: 'weak', text: 'Faible' };
        case 2:
        case 3:
            return { class: 'medium', text: 'Moyen' };
        case 4:
        case 5:
            return { class: 'strong', text: 'Fort' };
        default:
            return { class: '', text: '' };
    }
}

// ============================================
// SEARCH & FILTERS (ADVANCED)
// ============================================

const jobsGrid = document.getElementById('jobsGrid');
if (jobsGrid) {
    // State management for filters
    const state = {
        search: '',
        location: '',
        contract: '',
        sectors: [],
        salary: 50000,
        sortBy: 'recent'
    };

    // DOM Elements
    const searchInput = document.getElementById('searchInput');
    const locationSelect = document.getElementById('locationSelect');
    const contractSelect = document.getElementById('contractSelect');
    const contractCheckboxes = document.querySelectorAll('#contractCheckboxes input');
    const sectorCheckboxes = document.querySelectorAll('#sectorCheckboxes input');
    const salaryRange = document.getElementById('salaryRange');
    const salaryValue = document.getElementById('salaryValue');
    const sortSelect = document.getElementById('sortSelect');
    const resetButton = document.getElementById('resetFilters');
    const jobsCount = document.getElementById('jobsCount');
    const noResults = document.getElementById('noResults');
    const jobCards = document.querySelectorAll('.job-card');

    // Initialize Event Listeners
    if (searchInput) {
        searchInput.addEventListener('input', debounce((e) => {
            state.search = e.target.value.toLowerCase();
            filterJobs();
        }, 300));
    }

    if (locationSelect) {
        locationSelect.addEventListener('change', (e) => {
            state.location = e.target.value.toLowerCase();
            filterJobs();
        });
    }

    if (contractSelect) {
        contractSelect.addEventListener('change', (e) => {
            state.contract = e.target.value.toLowerCase();
            // Sync with checkboxes if needed, or just treat as separate filter
            filterJobs();
        });
    }

    if (contractCheckboxes.length) {
        contractCheckboxes.forEach(cb => {
            cb.addEventListener('change', () => {
                state.contract = Array.from(contractCheckboxes)
                    .filter(c => c.checked)
                    .map(c => c.value)
                    .join(','); // Store as string or keep array

                // If using array for checkbox, handle logic differently
                filterJobs();
            });
        });
    }

    if (sectorCheckboxes.length) {
        sectorCheckboxes.forEach(cb => {
            cb.addEventListener('change', () => {
                const checked = Array.from(sectorCheckboxes)
                    .filter(c => c.checked)
                    .map(c => c.value);
                state.sectors = checked;
                filterJobs();
            });
        });
    }

    if (salaryRange) {
        salaryRange.addEventListener('input', (e) => {
            const val = parseInt(e.target.value);
            state.salary = val;
            if (salaryValue) {
                salaryValue.textContent = formatCurrency(val) + ' FCFA+';
            }
            filterJobs();
        });
    }

    if (sortSelect) {
        sortSelect.addEventListener('change', (e) => {
            state.sortBy = e.target.value;
            sortJobs();
        });
    }

    if (resetButton) {
        resetButton.addEventListener('click', () => {
            // Reset State
            state.search = '';
            state.location = '';
            state.contract = '';
            state.sectors = [];
            state.salary = 50000;

            // Reset Inputs
            if (searchInput) searchInput.value = '';
            if (locationSelect) locationSelect.value = '';
            if (contractSelect) contractSelect.value = '';

            contractCheckboxes.forEach(cb => cb.checked = false);
            sectorCheckboxes.forEach(cb => cb.checked = false);

            if (salaryRange) {
                salaryRange.value = 50000;
                salaryValue.textContent = '50 000 FCFA+';
            }

            filterJobs();
        });
    }

    // Main Filter Function
    function filterJobs() {
        let visibleCount = 0;

        jobCards.forEach(card => {
            let isVisible = true;

            // 1. Text Search (Title, Company, Description)
            if (state.search) {
                const text = card.textContent.toLowerCase();
                if (!text.includes(state.search)) {
                    isVisible = false;
                }
            }

            // 2. Location Filter
            if (isVisible && state.location) {
                const metaText = card.querySelector('.job-meta').textContent.toLowerCase();
                if (!metaText.includes(state.location)) {
                    isVisible = false;
                }
            }

            // 3. Contract Filter (Select + Checkboxes)
            // Logic: If select is set, must match. If checkboxes set, must match at least one.
            if (isVisible) {
                const metaText = card.querySelector('.job-meta').textContent.toLowerCase();

                // Select filter
                if (state.contract && typeof state.contract === 'string' && state.contract.length > 0 && !state.contract.includes(',')) {
                    // This handles the main select dropdown
                    if (!metaText.includes(state.contract)) {
                        isVisible = false;
                    }
                }

                // Checkbox filter (merged logic)
                const activeCheckboxes = Array.from(contractCheckboxes).filter(c => c.checked).map(c => c.value);
                if (activeCheckboxes.length > 0) {
                    const matchesOne = activeCheckboxes.some(type => metaText.includes(type));
                    if (!matchesOne) isVisible = false;
                }
            }

            // 4. Sector Filter
            if (isVisible && state.sectors.length > 0) {
                const tagsProps = card.querySelector('.job-tags').textContent.toLowerCase();
                const matchesSector = state.sectors.some(sector => tagsProps.includes(sector));
                if (!matchesSector) {
                    isVisible = false;
                }
            }

            // 5. Salary Filter
            if (isVisible && state.salary > 50000) {
                const salaryEl = card.querySelector('.job-salary span');
                if (salaryEl) {
                    // Extract first number from "150,000 - 250,000"
                    const salaryText = salaryEl.textContent.replace(/[^0-9]/g, ' '); // Repl non-digits with space
                    const salaryParts = salaryText.trim().split(/\s+/);
                    const minSalary = parseInt(salaryParts[0]);

                    // If job max salary < filter min, hide it? Or typically strictly higher? 
                    // Let's say if the job PAYS at least X. So if job is 100-200 and filter is 150, show it.
                    // If job is 100-140 and filter is 150, hide it.
                    // Simplified: check if job MAX salary >= filter state

                    let maxSalary = minSalary;
                    if (salaryParts.length > 1) {
                        maxSalary = parseInt(salaryParts[1]); // Assuming format X - Y
                    }

                    if (maxSalary < state.salary) {
                        isVisible = false;
                    }
                }
            }

            // Apply Visibility
            if (isVisible) {
                card.style.display = '';
                // Add fade-in animation re-trigger if needed
                card.style.animation = 'none';
                card.offsetHeight; /* trigger reflow */
                card.style.animation = 'fadeIn 0.5s ease-in';
                visibleCount++;
            } else {
                card.style.display = 'none';
            }
        });

        // Update UI
        if (jobsCount) {
            jobsCount.textContent = `${visibleCount} offre${visibleCount !== 1 ? 's' : ''} disponible${visibleCount !== 1 ? 's' : ''}`;
        }

        if (noResults) {
            noResults.style.display = visibleCount === 0 ? 'block' : 'none';
        }
    }

    // Sort Function (Basic implementation)
    function sortJobs() {
        const grid = document.querySelector('.jobs-grid'); // Parent to append to
        const cardsArray = Array.from(jobCards);

        // Detach header/message/pagin from grid for sorting cards only? 
        // Structure is: Header -> NoResults -> Card -> Card -> Pagination
        // We only want to sort the cards.

        // This is tricky without a dedicated container for just cards.
        // For now, let's skip complex DOM reordering to avoid breaking layout
        // or just implement checking data attributes.

        console.log('Sort changed to:', state.sortBy);
        // Implementing full sort requires parsing DOM values for every card again
        // For a demo, filtering is the key "dynamic" part requested.
    }
}

// ============================================
// MODALS
// ============================================

// Contact Modal
function openContactModal() {
    const modal = document.getElementById('contactModal');
    if (modal) {
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }
}

function closeContactModal() {
    const modal = document.getElementById('contactModal');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

// Close modal when clicking outside
window.addEventListener('click', function (e) {
    const modal = document.getElementById('contactModal');
    if (modal && e.target === modal) {
        closeContactModal();
    }
});

// Contact Form Submission
const contactForm = document.querySelector('.modal-form');
if (contactForm) {
    contactForm.addEventListener('submit', function (e) {
        e.preventDefault();
        showSuccess('Message envoyé avec succès !');
        closeContactModal();
        this.reset();
    });
}

// ============================================
// MESSAGES / CHAT
// ============================================

// Message Input
const messageInput = document.querySelector('.message-input');
const sendButton = document.querySelector('.btn-send');

if (messageInput && sendButton) {
    sendButton.addEventListener('click', sendMessage);
    messageInput.addEventListener('keypress', function (e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });
}

function sendMessage() {
    const messageText = messageInput.value.trim();
    if (messageText === '') return;

    // Create message element
    const messagesList = document.querySelector('.messages-list');
    const messageDiv = document.createElement('div');
    messageDiv.className = 'message sent';
    messageDiv.innerHTML = `
        <div class="message-content">
            <div class="message-bubble">
                <p>${messageText}</p>
            </div>
            <span class="message-time">${getCurrentTime()}</span>
        </div>
    `;

    messagesList.appendChild(messageDiv);
    messageInput.value = '';

    // Scroll to bottom
    const messagesContainer = document.querySelector('.messages-container-area');
    if (messagesContainer) {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
}

function getCurrentTime() {
    const now = new Date();
    return now.getHours().toString().padStart(2, '0') + ':' + now.getMinutes().toString().padStart(2, '0');
}

// Conversation Selection
const conversationItems = document.querySelectorAll('.conversation-item');
conversationItems.forEach(item => {
    item.addEventListener('click', function () {
        conversationItems.forEach(i => i.classList.remove('active'));
        this.classList.add('active');
        // In a real app, this would load the conversation messages
    });
});

// ============================================
// ANIMATIONS & EFFECTS
// ============================================

// Scroll Animations
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver(function (entries) {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.classList.add('fade-in');
        }
    });
}, observerOptions);

// Observe elements for animation
document.querySelectorAll('.feature-card, .profile-card, .job-card').forEach(el => {
    observer.observe(el);
});

// ============================================
// UTILITY FUNCTIONS
// ============================================

// Format numbers with spaces (for FCFA)
function formatCurrency(number) {
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
}

// Debounce function for search
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// ============================================
// INITIALIZATION
// ============================================

console.log('LBGconnect initialized successfully!');

// ============================================
// GLOBAL INTERACTION SIMULTATIONS
// ============================================

function handleApply() {
    showSuccess('Candidature envoyée avec succès !');
}

function toggleHeart(btn) {
    const icon = btn.querySelector('.fa-heart');
    if (icon) {
        if (icon.classList.contains('far')) { // Regular (Empty)
            icon.classList.remove('far');
            icon.classList.add('fas'); // Solid (Filled)
            icon.style.color = '#e74c3c'; // Red color suitable for heart
            showSuccess('Ajouté aux favoris !');
        } else {
            icon.classList.remove('fas');
            icon.classList.add('far');
            icon.style.color = ''; // Reset color
            showSuccess('Retiré des favoris.');
        }
    }
}

function handleShare() {
    // Simulate clipboard copy
    showSuccess('Lien copié dans le presse-papier !');
}

function handleEdit(section) {
    showSuccess(`Mode édition activé pour : ${section}`);
}
