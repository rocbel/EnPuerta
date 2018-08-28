$("#galeria, body").vegas({
    timer: false,
    transition: 'slideLeft2',
    slides: [
        { src: './img/bici1.jpg' },
        { src: './img/bici2.jpg', transition: 'slideRight2' },
        { src: './img/bici3.jpg' },
        { src: './img/bici4.jpg', transition: 'slideRight2' }
    ]
});