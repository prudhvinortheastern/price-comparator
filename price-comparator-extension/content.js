const API_BASE = "http://localhost:8080";

function isAmazonCartOrCheckout() {
    const u = location.href;
    return u.includes("/gp/cart") || u.includes("/cart") || u.includes("/checkout");
}

async function loadUser() {
    const { user } = await chrome.storage.local.get(["user"]);
    return user;
}

function getTitleGuess() {
    return (
        document.querySelector("#productTitle")?.textContent?.trim() ||
        document.querySelector("span.sc-product-title")?.textContent?.trim() ||
        document.title
    );
}

function getPriceGuess() {
    const txt =
        document.querySelector(".a-price .a-offscreen")?.textContent?.trim() ||
        document.querySelector("span.sc-product-price")?.textContent?.trim() ||
        "";
    const m = txt.replace(/,/g, "").match(/(\d+(\.\d+)?)/);
    return m ? Number(m[1]) : null;
}

function showBanner(message, linkUrl) {
    if (document.getElementById("pc-banner")) return;

    const box = document.createElement("div");
    box.id = "pc-banner";
    box.style.position = "fixed";
    box.style.bottom = "16px";
    box.style.right = "16px";
    box.style.zIndex = "999999";
    box.style.background = "white";
    box.style.border = "2px solid #111";
    box.style.borderRadius = "12px";
    box.style.padding = "12px";
    box.style.width = "360px";
    box.style.boxShadow = "0 8px 24px rgba(0,0,0,0.2)";
    box.innerHTML = `
    <div style="font-weight:700;margin-bottom:6px;">Price Comparator</div>
    <div style="margin-bottom:10px;">${message}</div>
    ${linkUrl ? `<a href="${linkUrl}" target="_blank" style="display:inline-block;margin-bottom:10px;">Open offer</a>` : ""}
    <button id="pc-close" style="width:100%;padding:8px;cursor:pointer;">Close</button>
  `;
    document.body.appendChild(box);
    document.getElementById("pc-close").onclick = () => box.remove();
}

async function runCompare() {
    const user = await loadUser();
    if (!user?.userId) return;

    const payload = {
        userId: user.userId,
        site: "amazon",
        url: location.href,
        title: getTitleGuess(),
        price: getPriceGuess()
    };

    try {
        const res = await fetch(`${API_BASE}/api/compare`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        const data = await res.json();

        if (data.cheaperFound && data.offers?.length) {
            const best = data.offers.reduce((a, b) => (a.price < b.price ? a : b));
            showBanner(`Cheaper found: ${best.site} for $${best.price}`, best.url);
        } else {
            showBanner("No cheaper offer found right now.", null);
        }
    } catch (e) {
        // If backend not reachable, silently ignore in content script
    }
}

if (isAmazonCartOrCheckout()) {
    // run after DOM settles
    setTimeout(runCompare, 2000);
}