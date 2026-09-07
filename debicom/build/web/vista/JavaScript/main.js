document.addEventListener("DOMContentLoaded", () => {
    const modal = document.getElementById("authModal");
    const open = document.getElementById("openModalBtn");
    const close = document.getElementById("closeModalBtn");

    if (modal && open && close) {
        open.addEventListener("click", () => {
            modal.classList.add("active");
            modal.setAttribute("aria-hidden", "false");
            document.body.style.overflow = "hidden";
        });

        const hide = () => {
            modal.classList.remove("active");
            modal.setAttribute("aria-hidden", "true");
            document.body.style.overflow = "";
        };

        close.addEventListener("click", hide);
        modal.addEventListener("click", e => {
            if (e.target === modal) hide();
        });

        document.addEventListener("keydown", e => {
            if (e.key === "Escape" && modal.classList.contains("active")) hide();
        });
    }

    initMobileSidebar();
    initStoreSearch();
    initTableFilters();
    initAuthFeedback();
});

function initMobileSidebar() {
    const sidebar = document.querySelector(".sidebar");
    const topbar = document.querySelector(".topbar");

    if (!sidebar) return;

    let button = document.querySelector(".mobile-menu-btn");
    if (!button) {
        button = document.createElement("button");
        button.className = "mobile-menu-btn";
        button.type = "button";
        button.setAttribute("aria-label", "Abrir menú");
        button.setAttribute("aria-expanded", "false");
        button.innerHTML = "☰";
        if (topbar) {
            topbar.insertBefore(button, topbar.firstChild);
        } else {
            button.classList.add("mobile-menu-floating");
            document.body.appendChild(button);
        }
    }

    let backdrop = document.querySelector(".sidebar-backdrop");
    if (!backdrop) {
        backdrop = document.createElement("div");
        backdrop.className = "sidebar-backdrop";
        backdrop.setAttribute("aria-hidden", "true");
        document.body.appendChild(backdrop);
    }

    const closeMenu = () => {
        sidebar.classList.remove("mobile-open");
        backdrop.classList.remove("active");
        button.innerHTML = "☰";
        button.setAttribute("aria-label", "Abrir menú");
        button.setAttribute("aria-expanded", "false");
    };

    const toggleMenu = () => {
        const open = !sidebar.classList.contains("mobile-open");
        sidebar.classList.toggle("mobile-open", open);
        backdrop.classList.toggle("active", open);
        button.innerHTML = open ? "✕" : "☰";
        button.setAttribute("aria-label", open ? "Cerrar menú" : "Abrir menú");
        button.setAttribute("aria-expanded", String(open));
    };

    button.addEventListener("click", toggleMenu);
    backdrop.addEventListener("click", closeMenu);

    const isMobileViewport = () => window.matchMedia("(max-width: 800px)").matches;

    sidebar.querySelectorAll("a").forEach(link => {
        link.addEventListener("click", closeMenu);
    });

    window.addEventListener("resize", () => {
        if (!isMobileViewport()) closeMenu();
    });

    // Evita que una página restaurada desde el historial conserve el menú abierto.
    window.addEventListener("pageshow", closeMenu);
}

function initStoreSearch() {
    const search = document.getElementById("tiendaBusqueda");
    const select = document.getElementById("idTienda");

    if (!search || !select) return;

    const originalOptions = Array.from(select.options)
        .filter(option => option.hasAttribute("data-store"))
        .map(option => ({
            value: option.value,
            text: option.textContent.trim()
        }));

    const renderOptions = () => {
        const term = search.value.trim().toLocaleLowerCase("es");
        const currentValue = select.value;

        while (select.options.length > 0) {
            select.remove(0);
        }

        select.add(new Option("Seleccione una tienda", ""));

        const matches = originalOptions.filter(item =>
            !term || item.text.toLocaleLowerCase("es").includes(term)
        );

        matches.forEach(item => {
            const option = new Option(item.text, item.value);
            option.setAttribute("data-store", "");
            select.add(option);
        });

        if (!matches.length) {
            const noResults = new Option(
                "No hay tiendas que coincidan con la búsqueda", ""
            );
            noResults.disabled = true;
            select.add(noResults);
        }

        if (matches.some(item => item.value === currentValue)) {
            select.value = currentValue;
        }
    };

    search.addEventListener("input", renderOptions);
}

function initTableFilters() {
    document.querySelectorAll("[data-table-filter]").forEach(input => {
        const selector = input.getAttribute("data-table-filter");
        const table = document.querySelector(selector);
        if (!table) return;

        const rows = Array.from(table.querySelectorAll("tbody tr[data-filter-value]"));
        const empty = table.querySelector("tbody tr[data-filter-empty]");

        const filter = () => {
            const term = input.value.trim().toLocaleLowerCase("es");
            let visible = 0;

            rows.forEach(row => {
                const value = (row.getAttribute("data-filter-value") || "")
                    .toLocaleLowerCase("es");
                const show = !term || value.includes(term);
                row.hidden = !show;
                if (show) visible++;
            });

            if (empty) empty.hidden = visible !== 0;
        };

        input.addEventListener("input", filter);
    });
}

function switchTab(tabId, button) {
    document.querySelectorAll(".tab-content").forEach(c => c.classList.remove("active"));
    document.querySelectorAll(".tab-btn").forEach(b => b.classList.remove("active"));
    document.getElementById(tabId)?.classList.add("active");
    button?.classList.add("active");
}


function initAuthFeedback() {
    const body = document.body;
    const modal = document.getElementById("authModal");
    const alert = document.getElementById("authAlert");

    if (!body || !modal) return;

    const hasError = body.getAttribute("data-auth-error") === "true";
    const tab = body.getAttribute("data-auth-tab");

    if (hasError) {
        modal.classList.add("active");
        modal.setAttribute("aria-hidden", "false");
        document.body.style.overflow = "hidden";

        if (tab) {
            const button = document.querySelector(
                `.tab-btn[onclick*="'${tab}'"]`
            );
            if (button) {
                switchTab(tab, button);
            }
        }
    }

    if (alert) {
        const closeButton = alert.querySelector(".auth-alert-close");
        closeButton?.addEventListener("click", () => {
            alert.remove();
        });
    }
}
