(function () {
  var form = document.getElementById("satisfactionForm");
  if (!form) {
    return;
  }

  var contextPath = document.body.dataset.contextPath || "";

  form.addEventListener("submit", function (e) {
    e.preventDefault();

    var rating = document.querySelector('input[name="satisfaction"]:checked');
    if (!rating) {
      alert("Por favor, selecciona una valoración");
      return;
    }

    window.location.href = contextPath + "/html/04%20SAM%20Line/sam-2.jsp";
  });

  document.querySelectorAll('input[name="satisfaction"]').forEach(function (radio) {
    radio.addEventListener("change", function (e) {
      document.querySelectorAll(".scale-option").forEach(function (option) {
        option.classList.remove("selected");
      });
      e.target.closest(".scale-option").classList.add("selected");
    });
  });
}());
