import { contextBridge, ipcRenderer } from 'electron'

contextBridge.exposeInMainWorld('electronAPI', {
  pluginApi: {
    downloadAndRunPluginById: (pluginId: number, pluginName: string) =>
        ipcRenderer.invoke('plugin:downloadAndRun', { pluginId, pluginName }),

    stopPluginById: (pluginId: number) =>
        ipcRenderer.invoke('plugin:stop', pluginId),

    listRunning: () =>
        ipcRenderer.invoke('plugin:listRunning'),
  }
})

