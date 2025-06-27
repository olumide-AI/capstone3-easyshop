document.addEventListener("DOMContentLoaded", () => {
    const service = new PresentationService();
    const $container = $("#presentation-container");
    const template = $("#slide-template").html();

    function render() {
        const slide = service.getCurrent();
        $container.html(Mustache.render(template, slide));
        $("#btn-prev").prop("disabled", service.atStart());
        $("#btn-next").prop("disabled", service.atEnd());
    }

    $("#btn-next").click(() => {
        service.next();
        render();
    });

    $("#btn-prev").click(() => {
        service.prev();
        render();
    });

    render();
});
