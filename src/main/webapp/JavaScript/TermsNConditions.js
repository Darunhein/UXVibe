document.addEventListener("DOMContentLoaded", function () {
    const checkbox = document.getElementById("acceptanceCheckbox");
    const btnComenzar = document.getElementById("btnComenzar");

    function updateState() {
        if (checkbox.checked) {
            btnComenzar.classList.remove("disabled");
        } else {
            btnComenzar.classList.add("disabled");
        }
    }

    btnComenzar.addEventListener("click", function (e) {
        if (!checkbox.checked) {
            e.preventDefault();
            checkbox.focus();
            alert("Debes aceptar los términos y condiciones antes de continuar.");
        }
    });

    checkbox.addEventListener("change", updateState);
    updateState(); // initial state on load
});