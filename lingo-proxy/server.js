require('dotenv').config();
const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { LingoDotDevEngine } = require('lingo.dev/sdk');

const app = express();
const PORT = 3001; // We'll run this on port 3001

// Middleware
app.use(cors());
app.use(bodyParser.json());

// Initialize Lingo SDK
// We read the key from a .env file (we'll make this next)
const lingoDotDev = new LingoDotDevEngine({
    apiKey: process.env.LINGO_API_KEY,
});

// --- Endpoint 1: Detect Language ---
app.post('/detect', async (req, res) => {
    try {
        const { text } = req.body;
        console.log(`[Lingo Proxy] Detecting language for: "${text}"`);

        // Call the official Lingo SDK
        const locale = await lingoDotDev.recognizeLocale(text);

        // Send back JSON: { "locale": "fr" }
        res.json({ locale: locale });
    } catch (error) {
        console.error("[Lingo Proxy] Detection Error:", error);
        res.status(500).json({ error: error.message });
    }
});

// --- Endpoint 2: Translate Text ---
app.post('/translate', async (req, res) => {
    try {
        const { text, sourceLocale, targetLocale } = req.body;
        console.log(`[Lingo Proxy] Translating "${text}" from ${sourceLocale} to ${targetLocale}`);

        // Call the official Lingo SDK
        const result = await lingoDotDev.localizeText(text, {
            sourceLocale: sourceLocale,
            targetLocale: targetLocale,
        });

        // Send back JSON: { "text": "Translated text" }
        res.json({ text: result });
    } catch (error) {
        console.error("[Lingo Proxy] Translation Error:", error);
        res.status(500).json({ error: error.message });
    }
});

// --- Endpoint 3: Batch Translate (The Fast Way) ---
app.post('/translate-batch', async (req, res) => {
    try {
        // content is a JSON object: { "title_0": "...", "snippet_0": "..." }
        const { content, sourceLocale, targetLocale } = req.body;
        
        console.log(`[Lingo Proxy] Batch translating ${Object.keys(content).length} items...`);

        // Call the official Lingo SDK "localizeObject"
        // This translates all values in the object while keeping keys the same
        const result = await lingoDotDev.localizeObject(content, {
            sourceLocale: sourceLocale,
            targetLocale: targetLocale,
        });

        // Send back the translated object
        res.json({ content: result });
    } catch (error) {
        console.error("[Lingo Proxy] Batch Error:", error);
        res.status(500).json({ error: error.message });
    }
});

// Start the server
app.listen(PORT, () => {
    console.log(`✅ Lingo Proxy Server running on http://localhost:${PORT}`);
});