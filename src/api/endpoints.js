// API端点配置文件
// 集中管理所有API端点路径

// 导入API配置
import apiConfig from './apiConfig';

// API端点路径定义
const endpoints = {
  // 用户相关API
  users: {
    // 属性池相关API
    attributePool: `${apiConfig.API_BASE_URL}/users/attribute-pool`,
    // 可以在这里添加更多用户相关的API端点
    getAll: `${apiConfig.API_BASE_URL}/users`,
    create: `${apiConfig.API_BASE_URL}/users/create`,

    getById: (id) => `${apiConfig.API_BASE_URL}/users/${id}`,
    update: (id) => `${apiConfig.API_BASE_URL}/users/${id}`,
    delete: (id) => `${apiConfig.API_BASE_URL}/users/${id}`
  },
  
  // 其他模块的API端点可以继续添加
  // 例如：
  // products: {
  //   getAll: `${apiConfig.API_BASE_URL}/products`,
  //   getById: (id) => `${apiConfig.API_BASE_URL}/products/${id}`,
  //   ...
  // }
};

export default endpoints;