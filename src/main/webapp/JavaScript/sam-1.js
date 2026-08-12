(function () {
  var form = document.getElementById("satisfactionForm");
  if (!form) {
    return;
  }

  var contextPath = document.body.dataset.contextPath || "";
  var nextButton = document.getElementById("samNextButton");
  var backButton = document.getElementById("samBackButton");

  if (nextButton) {
    nextButton.addEventListener("click", function () {
      var rating = document.querySelector('input[name="satisfaction"]:checked');
      if (!rating) {
        alert("Por favor, selecciona una valoración");
        return;
      }

      var body = new URLSearchParams();
      body.append('satisfaction', rating.value);

      fetch(contextPath + "/participant-identity", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        },
        body: body.toString(),
      })
        .then(function (res) {
          if (!res.ok) {
            return res.text().then(function (text) {
              throw new Error(text || 'Error al guardar datos');
            });
          }
        })
        .then(function () {
          window.location.href = contextPath + "/html/04%20SAM%20Line/sam-2.jsp";
        })
        .catch(function (err) {
          alert(err.message || 'Error al guardar datos');
        });
    });
  }

  if (backButton) {
    backButton.addEventListener("click", function (event) {
      event.preventDefault();
      window.history.back();
    });
  }

  document
    .querySelectorAll('input[name="satisfaction"]')
    .forEach(function (radio) {
      radio.addEventListener("change", function (e) {
        document.querySelectorAll(".scale-option").forEach(function (option) {
          option.classList.remove("selected");
        });
        e.target.closest(".scale-option").classList.add("selected");
      });
    });
})();
