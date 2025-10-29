"use strict";
const electron = require("electron");
electron.contextBridge.exposeInMainWorld("electronAPI", {
  pluginApi: {
    downloadAndRunPluginById: (pluginId, pluginName) => electron.ipcRenderer.invoke("plugin:downloadAndRun", { pluginId, pluginName }),
    stopPluginById: (pluginId) => electron.ipcRenderer.invoke("plugin:stop", pluginId),
    listRunning: () => electron.ipcRenderer.invoke("plugin:listRunning")
  }
});
