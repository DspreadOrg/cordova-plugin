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
  module.exports.metadata = {
    "posPlugin": "1.2.5"
  };
});