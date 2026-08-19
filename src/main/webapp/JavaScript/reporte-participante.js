document.addEventListener("DOMContentLoaded", function () {
  const audioElement = document.querySelector(".reporte-box--audio audio");
  if (audioElement) {
    audioElement.addEventListener("play", function () {
      console.log("Reproduciendo grabación del participante.");
    });
  }
});
