document.addEventListener("DOMContentLoaded", () => {

    const durationSelect = document.getElementById("duration");
    const kmSelect = document.getElementById("km");
    const checkboxes = document.querySelectorAll(".option-checkbox");
    const result = document.getElementById("resultPrice");

    function calculatePrice() {

        let price = basePrice;

        checkboxes.forEach(cb => {
            if (cb.checked) {
                price += parseFloat(cb.value);
            }
        });

        let duration = parseInt(durationSelect.value);
        if (duration === 12) price += 60;
        if (duration === 24) price += 40;
        if (duration === 36) price += 20;
        if (duration === 48) price += 0;

        let km = parseInt(kmSelect.value);
        if (km === 25000) price += 50;
        if (km === 20000) price += 25;
        if (km === 15000) price += 0;

        result.innerText = Math.round(price);
    }

    durationSelect.addEventListener("change", calculatePrice);
    kmSelect.addEventListener("change", calculatePrice);
    checkboxes.forEach(cb => cb.addEventListener("change", calculatePrice));

    // 💡 Lance le calcul au chargement
    calculatePrice();

});

document.querySelectorAll('.option-box').forEach(box => {
    box.addEventListener('click', () => {
        const checkbox = box.querySelector('input');
        checkbox.checked = !checkbox.checked;
        checkbox.dispatchEvent(new Event('change'));
    });
});

document.addEventListener("DOMContentLoaded", () => {

    const mainImage = document.getElementById("mainImage");
    const thumbnails = document.querySelectorAll(".thumb");

    thumbnails.forEach(img => {
        img.addEventListener("click", () => {
            mainImage.src = img.src;
        });
    });

});