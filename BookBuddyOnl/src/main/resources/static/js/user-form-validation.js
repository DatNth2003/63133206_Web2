$(document).ready(function() {
    $('#role').select2();
    $('#selectedRoles').select2();
});

function createRoleElement(text, onRemove) {
    var role = document.createElement('div');
    role.classList.add('badge', 'bg-primary', 'me-1', 'mb-1', 'px-2', 'py-1');
    role.textContent = text;
    
    var removeButton = document.createElement('button');
    removeButton.classList.add('btn-close', 'ms-2');
    removeButton.setAttribute('type', 'button');
    removeButton.setAttribute('aria-label', 'Close');
    removeButton.addEventListener('click', onRemove);
    
    role.appendChild(removeButton);
    return role;
}

const togglePassword = document.getElementById('togglePassword');
const toggleConfirmPassword = document.getElementById('toggleConfirmPassword');
const password = document.getElementById('password');
const confirmPassword = document.getElementById('confirmPassword');

togglePassword.addEventListener('click', function () {
    const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
    password.setAttribute('type', type);
    this.querySelector('i').classList.toggle('bi-eye');
    this.querySelector('i').classList.toggle('bi-eye-slash');
});

toggleConfirmPassword.addEventListener('click', function () {
    const type = confirmPassword.getAttribute('type') === 'password' ? 'text' : 'password';
    confirmPassword.setAttribute('type', type);
    this.querySelector('i').classList.toggle('bi-eye');
    this.querySelector('i').classList.toggle('bi-eye-slash');
});

const emailInput = document.getElementById('email');
const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');
const confirmPasswordInput = document.getElementById('confirmPassword');

emailInput.addEventListener('input', function () {
    validateEmail(emailInput.value);
});

usernameInput.addEventListener('input', function () {
    validateUsername(usernameInput.value);
});

passwordInput.addEventListener('input', function () {
    validatePassword(passwordInput.value);
    validateConfirmPassword();
});

confirmPasswordInput.addEventListener('input', function () {
    validateConfirmPassword();
});

function validateConfirmPassword() {
    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;
    const confirmPasswordValidityIcon = document.getElementById('confirmPasswordValidityIcon');
    const confirmPasswordValidityMessage = document.getElementById('confirmPasswordValidityMessage');
    if (password === confirmPassword && password !== '') {
        confirmPasswordValidityIcon.classList.remove('bi-x', 'text-danger');
        confirmPasswordValidityIcon.classList.add('bi-check', 'text-success');
        confirmPasswordValidityMessage.textContent = 'Passwords match';
        confirmPasswordValidityMessage.classList.remove('text-danger');
        confirmPasswordValidityMessage.classList.add('text-success');
        confirmPasswordInput.classList.remove('invalid-input');
        confirmPasswordInput.classList.add('valid-input');
    } else {
        confirmPasswordValidityIcon.classList.remove('bi-check', 'text-success');
        confirmPasswordValidityIcon.classList.add('bi-x', 'text-danger');
        confirmPasswordValidityMessage.textContent = 'Password must match the above password.';
        confirmPasswordValidityMessage.classList.add('text-danger');
        confirmPasswordValidityMessage.classList.remove('text-success');
        confirmPasswordInput.classList.remove('valid-input');
        confirmPasswordInput.classList.add('invalid-input');
    }
}

function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const isValid = emailRegex.test(email);
    const emailValidityIcon = document.getElementById('emailValidityIcon');
    const emailValidityMessage = document.getElementById('emailValidityMessage');
    if (isValid) {
        emailValidityIcon.classList.remove('bi-x', 'text-danger');
        emailValidityIcon.classList.add('bi-check', 'text-success');
        emailValidityMessage.textContent = 'Valid email';
        emailValidityMessage.classList.remove('text-danger');
        emailValidityMessage.classList.add('text-success');
        emailInput.classList.remove('invalid-input');
        emailInput.classList.add('valid-input');
    } else {
        emailValidityIcon.classList.remove('bi-check', 'text-success');
        emailValidityIcon.classList.add('bi-x', 'text-danger');
        emailValidityMessage.textContent = 'Please enter a valid email address.';
        emailValidityMessage.classList.add('text-danger');
        emailValidityMessage.classList.remove('text-success');
        emailInput.classList.remove('valid-input');
        emailInput.classList.add('invalid-input');
    }
    return isValid;
}

function validateUsername(username) {
    const isValid = username.length >= 8 && username.length <= 50;
    const usernameValidityIcon = document.getElementById('usernameValidityIcon');
    const usernameValidityMessage = document.getElementById('usernameValidityMessage');
    if (isValid) {
        usernameValidityIcon.classList.remove('bi-x', 'text-danger');
        usernameValidityIcon.classList.add('bi-check', 'text-success');
        usernameValidityMessage.textContent = 'Valid username';
        usernameValidityMessage.classList.remove('text-danger');
        usernameValidityMessage.classList.add('text-success');
        usernameInput.classList.remove('invalid-input');
        usernameInput.classList.add('valid-input');
    } else {
        usernameValidityIcon.classList.remove('bi-check', 'text-success');
        usernameValidityIcon.classList.add('bi-x', 'text-danger');
        usernameValidityMessage.textContent = 'Username must be between 8 and 50 characters.';
        usernameValidityMessage.classList.add('text-danger');
        usernameValidityMessage.classList.remove('text-success');
        usernameInput.classList.remove('valid-input');
        usernameInput.classList.add('invalid-input');
    }
    return isValid;
}
function validatePassword(password) {
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    const isValid = passwordRegex.test(password);
    const passwordValidityMessage = document.getElementById('passwordHelp');

    passwordValidityMessage.innerHTML = '';

    if (password.length < 8) {
        passwordValidityMessage.innerHTML += '<span class="bi bi-x text-danger"></span> <span class="text-danger">At least 8 characters</span><br>';
    } else {
        passwordValidityMessage.innerHTML += '<span class="bi bi-check text-success"></span> At least 8 characters<br>';
    }

    if (!/[A-Z]/.test(password)) {
        passwordValidityMessage.innerHTML += '<span class="bi bi-x text-danger"></span> <span class="text-danger">At least 1 uppercase letter</span><br>';
    } else {
        passwordValidityMessage.innerHTML += '<span class="bi bi-check text-success"></span> At least 1 uppercase letter<br>';
    }

    if (!/\d/.test(password)) {
        passwordValidityMessage.innerHTML += '<span class="bi bi-x text-danger"></span> <span class="text-danger">At least 1 number</span><br>';
    } else {
        passwordValidityMessage.innerHTML += '<span class="bi bi-check text-success"></span> At least 1 number<br>';
    }

    if (!/[@$!%*?&]/.test(password)) {
        passwordValidityMessage.innerHTML += '<span class="bi bi-x text-danger"></span> <span class="text-danger">At least 1 special character</span><br>';
    } else {
        passwordValidityMessage.innerHTML += '<span class="bi bi-check text-success"></span> At least 1 special character<br>';
    }

    if (isValid) {
        passwordValidityIcon.classList.remove('bi-x', 'text-danger');
        passwordValidityIcon.classList.add('bi-check', 'text-success');
        passwordValidityMessage.innerHTML += '<span class="bi bi-check text-success"></span> Password meets requirements';
        passwordValidityMessage.classList.remove('text-success');
        passwordValidityMessage.classList.add('text-danger');
        passwordInput.classList.remove('valid-input');
        passwordInput.classList.add('invalid-input');
    }

    return isValid;
}

function handleFileInput(input) {
    var file = input.files[0]; // Lấy tệp từ trường file input
    var allowedExtensions = ['jpg', 'jpeg', 'png', 'gif'];
    var isValid = false;

    if (file) {
        var extension = file.name.substring(file.name.lastIndexOf('.') + 1).toLowerCase();

        for (var i = 0; i < allowedExtensions.length; i++) {
            if (extension === allowedExtensions[i]) {
                isValid = true;
                break;
            }
        }
    }

    if (!isValid) {
        alert('File không hợp lệ. Chỉ chấp nhận các file ảnh có phần mở rộng: jpg, jpeg, png, gif.');
        input.value = '';
    } else {
        var preview = document.getElementById('avatarPreview');
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function (e) {
                preview.src = e.target.result;
            }
            reader.readAsDataURL(input.files[0]);
        } else {
            preview.src = "/images/default/default-avatar.jpg";
        }
    }
}

function addRole() {
    var select = document.getElementById('selectedRoles');
    var container = document.getElementById('selectedRolesTags');
    var selectedOption = select.options[select.selectedIndex];
    var selectedRoles = document.getElementsByName('selectedRoles')[0] || document.createElement('select');
    selectedRoles.setAttribute('name', 'selectedRoles');
    selectedRoles.setAttribute('multiple', 'multiple');
    selectedRoles.style.display = 'none';

    if (!selectedRoles.parentElement) {
        document.querySelector('form').appendChild(selectedRoles);
    }

    var alreadySelected = Array.from(selectedRoles.options).some(option => option.value == selectedOption.value);
    if (alreadySelected) {
        return;
    }

    var hiddenOption = document.createElement('option');
    hiddenOption.value = selectedOption.value;
    hiddenOption.text = selectedOption.text;
    hiddenOption.selected = true;
    selectedRoles.appendChild(hiddenOption);

    var role = createRoleElement(selectedOption.text, function() {
        container.removeChild(role);
        hiddenOption.remove();
    });

    container.appendChild(role);
}
