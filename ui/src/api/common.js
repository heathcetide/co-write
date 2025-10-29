import request from '../utils/request'; // 引入公共请求封装
import remoteRequest from '../utils/remoteRequest';

// 获取用户信息
export const getBackInfo = () => {
    return request.get('/info'); // 假设接口是 '/user/info'
}

export const uploadFile = (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return remoteRequest.post('/common/upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data' // 确保请求头为 multipart/form-data
        }
    });
}

export const getCloudSystemInfo = () => {
    return remoteRequest.get(`/common/system/status`);
};