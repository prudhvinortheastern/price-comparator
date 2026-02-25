const API_BASE = "http://localhost:8080";
const out = document.getElementById("out");

async function saveUser(user) {
    await chrome.storage.local.set({ user });
}

async function loadUser() {
    const { user } = await chrome.storage.local.get(["user"]);
    return user;
}

function setOut(msg) {
    out.textContent = msg;
}

document.getElementById("registerBtn").addEventListener("click", async () => {
    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();

    if (!name || !email) {
        setOut("Enter name + email");
        return;
    }

    setOut("Registering...");

    try {
        const res = await fetch(`${API_BASE}/api/auth/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, email })
        });

        const json = await res.json();

        if (!res.ok) {
            setOut(json.error || "Registration failed");
            return;
        }

        await saveUser(json);
        setOut(`Registered ✅\nUserId: ${json.userId}\n${json.name} <${json.email}>`);
    } catch (e) {
        setOut("Backend not reachable. Start Spring Boot on :8080");
    }
});

document.getElementById("testCompareBtn").addEventListener("click", async () => {
    const user = await loadUser();
    if (!user?.userId) {
        setOut("Register first, then try again.");
        return;
    }

    setOut("Testing compare on current tab...");

    // Get current tab URL + title
    const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });

    try {
        const res = await fetch(`${API_BASE}/api/compare`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                userId: user.userId,
                site: "manual-test",
                url: tab.url,
                title: tab.title,
                price: null
            })
        });

        const data = await res.json();
        setOut(JSON.stringify(data, null, 2));
    } catch (e) {
        setOut("Compare failed. Is backend running?");
    }
});

// On popup load, show stored user
(async () => {
    const user = await loadUser();
    if (user) {
        setOut(`Registered ✅\nUserId: ${user.userId}\n${user.name} <${user.email}>`);
    }
})();