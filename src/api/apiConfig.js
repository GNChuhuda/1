// API配置文件
// 集中管理所有API相关配置，便于统一修改

// 服务器基础URL，所有API请求都会基于此URL
const BASE_URL = 'http://10.203.242.165:8080';

// API路径前缀
const API_PREFIX = '/api';

// 完整的基础API URL
const API_BASE_URL = `${BASE_URL}${API_PREFIX}`;

// 通用请求超时时间（毫秒）
const REQUEST_TIMEOUT = 10000;

// 请求头配置
const DEFAULT_HEADERS = {
  'Content-Type': 'application/json'
};

// API版本
const API_VERSION = 'v1';

// 导出配置
const apiConfig = {
  BASE_URL,
  API_PREFIX,
  API_BASE_URL,
  REQUEST_TIMEOUT,
  DEFAULT_HEADERS,
  API_VERSION
};

export default apiConfig;