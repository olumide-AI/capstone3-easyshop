let presentationService;

class PresentationService {
    constructor() {
        this.slides = [
            { title: "Intro", content: "Welcome to my presentation!" },
            { title: "Problem", content: "This is the problem we want to solve..." },
            { title: "Solution", content: "Here's how we plan to solve it." },
            { title: "Results", content: "The results speak for themselves." },
            { title: "Thank You", content: "Thanks for your time!" }
        ];
        this.index = 0;
    }

    getCurrent() {
        return this.slides[this.index];
    }

    next() {
        if (this.index < this.slides.length - 1) this.index++;
    }

    prev() {
        if (this.index > 0) this.index--;
    }

    atStart() {
        return this.index === 0;
    }

    atEnd() {
        return this.index === this.slides.length - 1;
    }
}
