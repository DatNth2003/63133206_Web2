document.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll('.submenu').forEach(function(submenu) {
        submenu.addEventListener('click', function() {
            this.querySelector('.sublist').classList.toggle('show');
        });
    });
});
