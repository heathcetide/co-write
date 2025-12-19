import { app, BrowserWindow, ipcMain, IpcMainInvokeEvent } from 'electron'
import { fileURLToPath } from 'node:url'
import path from 'node:path'
import fs from 'fs'
import http from 'http'
import { spawn, ChildProcess } from 'child_process'

const __dirname = path.dirname(fileURLToPath(import.meta.url))

const VITE_DEV_SERVER_URL = process.env['VITE_DEV_SERVER_URL']
const APP_ROOT = path.join(__dirname, '..')
// const MAIN_DIST = path.join(APP_ROOT, 'dist-electron')
const RENDERER_DIST = path.join(APP_ROOT, 'dist')
const VITE_PUBLIC = VITE_DEV_SERVER_URL ? path.join(APP_ROOT, 'public') : RENDERER_DIST

let win: BrowserWindow | null = null

function createWindow() {
  // 获取图标路径
  const iconPath = VITE_DEV_SERVER_URL 
    ? path.join(APP_ROOT, 'src', 'assets', 'electron-vite.png')
    : path.join(VITE_PUBLIC, 'electron-vite.png')
  
  win = new BrowserWindow({
    width: 1300,
    height: 800,
    autoHideMenuBar: true,
    icon: iconPath,
    webPreferences: {
      preload: path.join(__dirname, 'preload.mjs'),
      contextIsolation: true,
      nodeIntegration: false,
    },
  })

  win.webContents.on('did-finish-load', () => {
    win?.webContents.send('main-process-message', new Date().toLocaleString())
  })

  if (VITE_DEV_SERVER_URL) {
    win.loadURL(VITE_DEV_SERVER_URL)
    win.webContents.openDevTools()
  } else {
    win.loadFile(path.join(RENDERER_DIST, 'index.html'))
  }
}

app.whenReady().then(createWindow)
app.on('window-all-closed', () => { if (process.platform !== 'darwin') app.quit() })
app.on('activate', () => { if (BrowserWindow.getAllWindows().length === 0) createWindow() })

// ------------------ 插件运行状态管理 ------------------

interface PluginProcessInfo {
  name: string
  process: ChildProcess
  path: string
}

const runningPlugins = new Map<number, PluginProcessInfo>()

// 下载并运行插件
ipcMain.handle('plugin:downloadAndRun', async (_event: IpcMainInvokeEvent, { pluginId, pluginName }: { pluginId: number, pluginName: string }): Promise<string> => {
  const url = `http://localhost:8080/api/plugin/download/${pluginId}`
  const targetDir = 'D:/tmp'
  if (!fs.existsSync(targetDir)) fs.mkdirSync(targetDir, { recursive: true })

  const filePath: string = await new Promise((resolve, reject) => {
    http.get(url, res => {
      const disposition = res.headers['content-disposition']
      let fileName = `${pluginName}`

      if (disposition?.includes('filename=')) {
        const match = disposition.match(/filename="(.+?)"/)
        if (match) fileName = decodeURIComponent(match[1])
      }

      const fullPath = path.join(targetDir, fileName)
      const file = fs.createWriteStream(fullPath)
      res.pipe(file)
      file.on('finish', () => file.close(() => resolve(fullPath)))
      res.on('error', reject)
    }).on('error', reject)
  })

  const ext = path.extname(filePath).toLowerCase()
  let bin: string
  let args: string[] = []

  if (ext === '.jar') {
    bin = 'java'
    args = ['-jar', filePath]
  } else if (ext === '.exe') {
    bin = filePath
    args = []
  } else {
    throw new Error(`不支持的插件格式: ${ext}`)
  }

  const child = spawn(bin, args, {
    cwd: targetDir,
    detached: true,
    stdio: 'ignore',
  })

  child.unref()
  runningPlugins.set(pluginId, { name: pluginName, process: child, path: filePath })

  return filePath
})

function isProcessAlive(pid: number): boolean {
  try {
    process.kill(pid, 0) // 仅检测进程是否存在，不杀死它
    return true
  } catch (e: any) {
    return e.code !== 'ESRCH'
  }
}

// 停止插件
ipcMain.handle('plugin:stop', async (_event, pluginId: number) => {
  const plugin = runningPlugins.get(pluginId)
  if (!plugin) throw new Error('未找到对应插件进程')

  try {
    const pid = plugin.process.pid as number
    if (!isProcessAlive(pid)) {
      console.warn(`插件进程 PID ${pid} 已经不存在，直接移除记录`)
      runningPlugins.delete(pluginId)
      return { success: true }
    }

    if (process.platform === 'win32') {
      process.kill(pid)
    } else {
      process.kill(-pid) // 杀进程组
    }

    runningPlugins.delete(pluginId)
    return { success: true }
  } catch (e) {
    console.error('停止插件失败:', e)
    throw new Error('停止插件失败')
  }
})

// 查询运行中插件列表
ipcMain.handle('plugin:listRunning', () => {
  console.log('[插件查询] 当前运行插件：', Array.from(runningPlugins.entries()))
  return Array.from(runningPlugins.entries()).map(([id, { name }]) => ({
    id,
    pluginName: name,
  }))
})
