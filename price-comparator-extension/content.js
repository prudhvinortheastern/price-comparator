const API_BASE = "http://localhost:8080"; // or your Render URL

// Only show the banner on cart / checkout pages
function isAmazonCartOrCheckout() {
    const u = location.href;
    return u.includes("/gp/cart") || u.includes("/cart") || u.includes("/checkout");
}

// Load the registered user that popup.js stored
async function loadUser() {
    const { user } = await chrome.storage.local.get(["user"]);
    return user;
}

// Try to guess the product title from the page
function getTitleGuess() {
    return (
        document.querySelector("#productTitle")?.textContent?.trim() ||
        document.querySelector("span.sc-product-title")?.textContent?.trim() ||
        document.title
    );
}

// Optional: try to parse price (even if we don’t use it now)
function getPriceGuess() {
    const txt =
        document.querySelector(".a-price .a-offscreen")?.textContent?.trim() ||
        document.querySelector("span.sc-product-price")?.textContent?.trim() ||
        "";
    const m = txt.replace(/,/g, "").match(/(\d+(\.\d+)?)/);
    return m ? Number(m[1]) : null;
}

// Render the bottom-right banner with THREE hyperlinks
function showBanner(offers) {
    if (document.getElementById("pc-banner")) return;
    if (!offers || !offers.length) return;

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
    box.style.fontFamily = "Arial, sans-serif";
    box.style.fontSize = "14px";

    const linksHtml = offers
        .map(
            (o) => `
        <div style="margin-bottom:8px;">
          <strong>${o.site}</strong>
          — <a href="${o.url}" target="_blank" rel="noopener" style="color:#0066c0;">
                Open offer
             </a>
        </div>`
        )
        .join("");

    box.innerHTML = `
      <div style="font-weight:700;margin-bottom:6px;">Price Comparator</div>
      <div style="margin-bottom:10px;">Check similar products on other sites:</div>
      ${linksHtml}
      <button id="pc-close" style="width:100%;padding:8px;cursor:pointer;margin-top:4px;">
        Close
      </button>
    `;
    document.body.appendChild(box);
    document.getElementById("pc-close").onclick = () => box.remove();
}

// Main function: call backend and then show banner
async function runCompare() {
    const user = await loadUser();
    if (!user?.userId) {
        // user not registered in popup yet → no banner
        return;
    }

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

        if (data.offers && data.offers.length) {
            // Pass the whole offers array so we can show 3 links
            showBanner(data.offers);
        }
    } catch (e) {
        // Backend not reachable; silently ignore in content script
    }
}

// Only run on cart/checkout pages, after the DOM is ready
if (isAmazonCartOrCheckout()) {
    setTimeout(runCompare, 2000);
}