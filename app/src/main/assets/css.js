(function() {
    var css = `
        div.absolute.bottom-0.left-0.w-full > div.relative.py-2.text-center.text-xs { display: none !important; }
        div.h-full.flex.ml-1.md\:w-full.md\:m-auto.justify-center { display: none !important; }
        div.group.relative[data-headlessui-state]:parent { display: none !important; }
        div.h-32.md\:h-48.flex-shrink-0 { height: 4rem !important; }
    `;

    var style = document.createElement('style');
    style.type = 'text/css';
    style.innerHTML = css;
    document.head.appendChild(style);

    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            document.querySelectorAll('div.absolute.bottom-0.left-0.w-full > div.relative.py-2.text-center.text-xs').forEach(el => el.style.display = 'none');
            document.querySelectorAll('div.h-full.flex.ml-1.md\\:w-full.md\\:m-auto.justify-center').forEach(el => el.style.display = 'none');
            document.querySelectorAll('div.group.relative[data-headlessui-state]').forEach(el => el.parentNode && (el.parentNode.style.display = 'none'));
            document.querySelectorAll('div.h-32.md\\:h-48.flex-shrink-0').forEach(el => { el.style.height = '4rem'; });
        });
    });

    observer.observe(document.body, { childList: true, subtree: true });
})();