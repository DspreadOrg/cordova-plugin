cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "id": "posPlugin.dspread_pos_plugin",
        "file": "plugins/posPlugin/www/dspread_pos_plugin.js",
        "pluginId": "posPlugin",
        "clobbers": [
            "cordova.plugins.dspread_pos_plugin"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-whitelist": "1.3.3",
    "posPlugin": "1.0.0"
};
// BOTTOM OF METADATA
});