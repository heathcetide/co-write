#!/usr/bin/env node

const path = require('path');
const config = require('../config');
const app = require('../app');

console.log(`Starting Hibiscus DB on ${config.host}:${config.port}...`);
app.listen(config.port, config.host, () => {
    console.log(`Hibiscus running at http://${config.host}:${config.port}`);
});
