document.addEventListener("DOMContentLoaded", () => {
  const service = new PresentationService();
  const container = document.getElementById("presentation-container");
  const template = document.getElementById("slide-template").innerHTML;
  const btnPrev = document.getElementById("btn-prev");
  const btnNext = document.getElementById("btn-next");

  function render() {
    const html = Mustache.render(template, service.getCurrent());
    container.innerHTML = html;
    btnPrev.disabled = service.atStart();
    btnNext.disabled = service.atEnd();
  }

  btnPrev.addEventListener("click", () => { service.prev(); render(); });
  btnNext.addEventListener("click", () => { service.next(); render(); });

  render();
});
