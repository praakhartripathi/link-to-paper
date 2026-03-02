/**
 * api.js — wraps the Spring Boot backend
 */
const configuredBaseUrl = (import.meta.env.VITE_API_BASE_URL || '').trim();
const hostname = typeof window !== 'undefined' ? window.location.hostname : '';
const isLocalApp = hostname === 'localhost' || hostname === '127.0.0.1';

function normalizeBaseUrl(url) {
    return url.replace(/\/+$/, '');
}

function isLocalhostUrl(url) {
    try {
        const parsed = new URL(url);
        return parsed.hostname === 'localhost' || parsed.hostname === '127.0.0.1';
    } catch {
        return false;
    }
}

// Prevent broken production builds that accidentally point to localhost.
const BASE_URL = (!isLocalApp && isLocalhostUrl(configuredBaseUrl))
    ? '/api'
    : normalizeBaseUrl(configuredBaseUrl || '/api');

/**
 * Sends a URL to the backend and receives a PDF blob.
 * @param {string} url  - The webpage URL to convert
 * @returns {Promise<Blob>} - PDF blob
 */
export async function generatePaper(url) {
    const response = await fetch(`${BASE_URL}/generate-paper`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ url }),
    });

    if (!response.ok) {
        const text = await response.text().catch(() => 'Unknown error');
        throw new Error(`Server error ${response.status}: ${text}`);
    }

    return response.blob();
}

/**
 * Triggers a browser download of the PDF blob.
 * @param {Blob} blob      - PDF blob
 * @param {string} filename - suggested filename
 */
export function downloadBlob(blob, filename = 'technical-paper.pdf') {
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
}
