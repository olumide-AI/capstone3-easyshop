class PresentationService {
  constructor() {
    this.slides = [
      { title: "Welcome 👋", content: "Hi, I'm Olumide. Here's how I built <strong>EasyShop</strong>." },
      { title: "Tech Stack ⚙️", content: "<ul><li>Bootstrap</li><li>Mustache.js</li><li>JavaScript Services</li></ul>" },
      { title: "Thanks 🙏", content: "Hope you enjoyed this behind-the-scenes look!" }
    ];
    this.index = 0;
  }

  getCurrent() { return this.slides[this.index]; }
  next() { if (this.index < this.slides.length - 1) this.index++; }
  prev() { if (this.index > 0) this.index--; }
  atStart() { return this.index === 0; }
  atEnd() { return this.index === this.slides.length - 1; }
}
