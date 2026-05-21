// API服务文件
// 封装axios实例和通用请求方法，支持网络失败时自动切换到模拟模式
import axios from 'axios';
import apiConfig from './apiConfig';
import mockService from './mockService';

// 创建axios实例
// 注意：当前设置withCredentials: false作为临时解决方案
// 后端问题：当withCredentials设置为true时，后端不能使用通配符'*'作为Access-Control-Allow-Origin
// 后端需要配置：Access-Control-Allow-Origin: 'http://localhost:3000'（指定具体源而不是*）
const apiClient = axios.create({
  baseURL: apiConfig.BASE_URL,
  timeout: apiConfig.REQUEST_TIMEOUT,
  headers: apiConfig.DEFAULT_HEADERS,
  withCredentials: false // 临时设置为false以避免CORS问题
});

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    // 可以在这里添加认证token等
    // const token = localStorage.getItem('token');
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    console.log('API请求成功:', response.config.url, response.status);
    return response;
  },
  (error) => {
    console.error('API请求错误:', {
      url: error.config?.url,
      method: error.config?.method,
      code: error.code,
      message: error.message,
      stack: error.stack
    });
    
    // 错误处理分类
    if (error.code === 'ERR_NETWORK') {
      console.error('网络错误：请检查服务器连接或CORS配置');
      console.log('自动切换到模拟模式...');
      
      // 显示模拟模式提示
      const showMockModeIndicator = () => {
        // 检查是否已存在模拟模式指示器
        let indicator = document.getElementById('mock-mode-indicator');
        if (!indicator) {
          indicator = document.createElement('div');
          indicator.id = 'mock-mode-indicator';
          indicator.style.cssText = `
            position: fixed;
            bottom: 20px;
            right: 20px;
            background-color: #ff9800;
            color: white;
            padding: 8px 16px;
            border-radius: 4px;
            font-size: 14px;
            z-index: 10000;
            box-shadow: 0 2px 8px rgba(0,0,0,0.2);
          `;
          indicator.textContent = '模拟模式已启用';
          document.body.appendChild(indicator);
        }
      };
      showMockModeIndicator();
      
      // 根据请求URL和方法提供模拟数据
      const url = error.config?.url || '';
      const method = error.config?.method?.toLowerCase() || 'get';
      
      // 处理不同的API端点（使用更健壮的匹配逻辑）
      if (url.includes('/users/attribute-pool') && method === 'post') {
        // 模拟发送属性池数据
        return mockService.sendAttributePoolData(error.config.data);
      } else if (((url.includes('/users/create') || url.endsWith('/users'))) && method === 'post') {
        // 模拟创建用户
        try {
          const userData = typeof error.config.data === 'string' ? JSON.parse(error.config.data) : error.config.data;
          return mockService.createUser(userData);
        } catch (parseError) {
          console.error('解析请求数据失败:', parseError);
          return mockService.createUser({});
        }
      } else if (((url.includes('/users') && !url.includes('/users/create') && !url.includes('/users/')) || url.endsWith('/users')) && method === 'get') {
        // 模拟获取所有用户
        return Promise.resolve({
          data: mockService.getAllUsers()
        });
      }
      
      // 默认模拟响应
      return Promise.resolve({
        success: true,
        message: '模拟模式响应',
        data: null
      });
    } else if (error.code === 'ECONNABORTED') {
      console.error('请求超时');
      alert('请求超时，请稍后重试');
    } else if (error.response) {
      // 服务器返回错误状态码
      console.error('服务器错误响应:', {
        status: error.response.status,
        data: error.response.data
      });
    }
    
    return Promise.reject(error);
  }
);

// 辅助函数：下载文件
const downloadFile = (content, fileName, contentType = 'text/plain') => {
  const blob = new Blob([content], { type: contentType });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = fileName;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
  console.log(`文件下载成功: ${fileName}`);
};

// 通用请求方法
const apiService = {
  // GET请求
  get: (url, params) => apiClient.get(url, { params }),
  
  // POST请求
  post: (url, data, config = {}) => apiClient.post(url, data, config),
  
  // PUT请求
  put: (url, data) => apiClient.put(url, data),
  
  // PATCH请求
  patch: (url, data) => apiClient.patch(url, data),
  
  // DELETE请求
  delete: (url) => apiClient.delete(url),
  
  // 处理文件下载响应
  handleFileDownloadResponse: (response) => {
    if (response?.data?.privateKeyFile) {
      const { fileName, content, contentType } = response.data.privateKeyFile;
      downloadFile(content, fileName, contentType);
      return true;
    }
    return false;
  },
  
  // 直接下载文件（用于处理实际后端返回的二进制文件）
  downloadBinaryFile: async (url, params = {}) => {
    return apiClient.get(url, {
      params,
      responseType: 'blob'
    }).then(response => {
      // 从响应头获取文件名
      const contentDisposition = response.headers['content-disposition'];
      let fileName = 'downloaded_file';
      if (contentDisposition) {
        const match = contentDisposition.match(/filename="?([^"]+)"?/);
        if (match && match[1]) {
          fileName = match[1];
        }
      }
      
      const blob = new Blob([response.data], { type: response.headers['content-type'] });
      downloadFile(blob, fileName, response.headers['content-type']);
      return { success: true, fileName };
    });
  }
};

export default apiService;