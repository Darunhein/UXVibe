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
      var fullName = document.getElementById("fullName").value.trim();
      var ageEl = document.getElementById("age");
      var age = ageEl ? ageEl.value : null;
      var gender = document.querySelector('input[name="gender"]:checked');
      var education = document.querySelector('input[name="education"]:checked');

      if (!fullName || !age || !gender || !education) {
        alert("Por favor, completa todos los campos");
        return;
      }

      // Client side check: age must be >= 3
      var ageNum = parseInt(age, 10);
      if (isNaN(ageNum) || ageNum < 3) {
        alert("La edad debe ser mayor o igual a 3 años.");
        return;
      }

      var body = new URLSearchParams();
      body.append('participantName', fullName);
      body.append('age', ageNum);
      body.append('gender', gender.value);
      body.append('education', education.value);

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
          window.location.href =
            contextPath + "/html/04%20SAM%20Line/cuestionario-sb-2.jsp";
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
