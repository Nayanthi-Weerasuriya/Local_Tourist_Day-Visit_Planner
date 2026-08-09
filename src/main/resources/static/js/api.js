const defaultOptions = {
    headers: {
        "Content-Type": "application/json"
    },
    credentials: "same-origin"
};

async function parseResponse(response) {
    const contentType = response.headers.get("content-type") || "";
    const isJson = contentType.includes("application/json");
    const payload = isJson ? await response.json() : null;

    if (!response.ok) {
        const error = new Error(payload?.message || "Request failed.");
        error.status = response.status;
        error.fieldErrors = payload?.fieldErrors || {};
        throw error;
    }

    return payload;
}

export async function apiRequest(url, options = {}) {
    const mergedOptions = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...(options.headers || {})
        }
    };

    const response = await fetch(url, mergedOptions);
    return parseResponse(response);
}

export function getJSON(url) {
    return apiRequest(url, { method: "GET" });
}

export function postJSON(url, body) {
    return apiRequest(url, {
        method: "POST",
        body: body ? JSON.stringify(body) : undefined
    });
}

export function putJSON(url, body) {
    return apiRequest(url, {
        method: "PUT",
        body: JSON.stringify(body)
    });
}

export function deleteJSON(url) {
    return apiRequest(url, { method: "DELETE" });
}
