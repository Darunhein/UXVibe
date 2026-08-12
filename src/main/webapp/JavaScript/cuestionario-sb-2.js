(function () {
  var form = document.getElementById("questionnaireForm");
  if (!form) {
    return;
  }

  var contextPath = document.body.dataset.contextPath || "";
  var nextButton = document.getElementById("sbNextButton");
  var backButton = document.getElementById("sbBackButton");

  if (nextButton) {
    nextButton.addEventListener("click", function () {
      var stress = document.querySelector('input[name="stress"]:checked');
      var relaxation = document.querySelector('input[name="relaxation"]:checked');
      if (!stress || !relaxation) {
        alert("Por favor, responde todas las preguntas");
        return;
      }

      var body = new URLSearchParams();
      body.append('stress', stress.value);
      body.append('relaxation', relaxation.value);

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
          window.location.href = contextPath + "/html/04%20SAM%20Line/sam-1.jsp";
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
})();
