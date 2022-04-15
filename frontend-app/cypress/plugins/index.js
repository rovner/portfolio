const {initPlugin: initScreenshotPlugin} = require('cypress-plugin-snapshots/plugin');

module.exports = (on, config) => {
    initScreenshotPlugin(on, config);
    return config;
};