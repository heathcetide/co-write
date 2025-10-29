import {ipcRenderer} from "electron";

export {}

declare global {
    interface Window {
        electronAPI: {
            ipcRenderer: {
                on: (channel: string, listener: (...args: any[]) => void) => void
                off: (channel: string, listener: (...args: any[]) => void) => void
                send: (channel: string, ...args: any[]) => void
                invoke: (channel: string, ...args: any[]) => Promise<any>
            }
            pluginApi: {
                downloadAndRunPluginById: (pluginId: string, pluginName: string) => Promise<any>
                stopPluginById: (pluginId: number) => Promise<any>
                listRunning: () => Promise<any>
            }
        }
    }
}
