import axios from 'axios';
// 通知组件的优化版
const notify = {
    show: (type, message) => {
        const div = document.createElement('div');
        div.classList.add('notify', type); // 添加类型类名
        div.textContent = message;

        // 给通知添加动画
        div.style.animation = 'fadeIn 1s ease-out';

        document.body.appendChild(div);

        // 设置定时器，3秒后自动消失
        setTimeout(() => {
            div.style.animation = 'fadeOut 1s ease-in'; // 使通知淡出
            setTimeout(() => div.remove(), 100); // 动画结束后移除元素
        }, 3000);
    },

    success: (message) => {
        notify.show('success', `Success: ${message}`);
    },

    error: (message) => {
        notify.show('error', `Error: ${message}`);
    },

    warning: (message) => {
        notify.show('warning', `Warning: ${message}`);
    }
};

// 创建 axios 实例
const instance = axios.create({
    baseURL: 'http://localhost:8080/api', // 根据实际情况设置 API 地址
    timeout: 10000, // 设置请求超时
});

// 请求拦截器
instance.interceptors.request.use(
    config => {
        // 获取 token
        const token = localStorage.getItem('token');

        // 如果有 token，添加到请求头
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        // 移除请求错误时的通知
        return Promise.reject(error);
    }
);

// 响应拦截器
instance.interceptors.response.use(
    response => {
        // 移除自动显示的成功/警告通知
        return response.data;
    },
    error => {
        if (error.response) {
            const status = error.response.status;
            if (status === 401) {
                // 401: 未授权，清除 token 并跳转登录
                localStorage.removeItem('token');
                window.location.href = '/login';
            }
            // 移除其他错误状态的 toast 通知
        }
        // 移除网络错误的 toast 通知
        return Promise.reject(error);
    }
);

export default instance;

// 添加通知样式
const style = document.createElement('style');
style.textContent = `
.notify {
    position: fixed;
    bottom: 20px;
    left: 50%;
    transform: translateX(-50%);
    padding: 12px 20px;
    border-radius: 8px;
    color: #fff;
    font-weight: bold;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    z-index: 9999;
    opacity: 1;
}

.success {
    background-color: #4CAF50; /* 绿色 */
}

.error {
    background-color: #f44336; /* 红色 */
}

.warning {
    background-color: #ff9800; /* 橙色 */
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateX(-50%) translateY(20px);
    }
    to {
        opacity: 1;
        transform: translateX(-50%) translateY(0);
    }
}

@keyframes fadeOut {
    from {
        opacity: 1;
        transform: translateX(-50%) translateY(0);
    }
    to {
        opacity: 0;
        transform: translateX(-50%) translateY(20px);
    }
}
`;

document.head.appendChild(style); // 将样式添加到页面中
