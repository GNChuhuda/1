// API工厂
// 管理API实例，支持在真实API失败时自动切换到Mock API
import realUserApi from './userApi';
import mockService from './mockService';

// 配置是否使用Mock（开发环境可以设置为true）
const USE_MOCK = false;

// 重试次数配置
const MAX_RETRIES = 1;

// 创建自动切换的API服务
const createResilientApi = (realApi, mockApi) => {
  // 创建包装后的API对象
  const resilientApi = {};
  
  // 遍历真实API的方法
  Object.keys(realApi).forEach(methodName => {
    resilientApi[methodName] = async (...args) => {
      // 如果强制使用Mock或真实API未实现该方法，则使用Mock
      if (USE_MOCK || !mockApi[methodName]) {
        console.log(`使用Mock API调用: ${methodName}`);
        return mockApi[methodName](...args);
      }
      
      let retries = 0;
      
      // 重试机制
      while (retries <= MAX_RETRIES) {
        try {
          console.log(`尝试调用真实API: ${methodName} (尝试 ${retries + 1}/${MAX_RETRIES + 1})`);
          // 调用真实API
          const result = await realApi[methodName](...args);
          return result;
        } catch (error) {
          // 记录错误
          console.error(`真实API调用失败，错误码: ${error.code || 'unknown'}`);
          
          // 如果已经重试了最大次数，切换到Mock
          if (retries >= MAX_RETRIES || error.code !== 'ERR_NETWORK') {
            console.log(`切换到Mock API: ${methodName}`);
            // 显示友好提示
            alert('当前连接后端服务器失败，已切换到模拟数据模式');
            // 调用Mock API
            return mockApi[methodName](...args);
          }
          
          // 递增重试计数
          retries++;
          
          // 指数退避策略
          const delay = Math.pow(2, retries - 1) * 300;
          console.log(`将在 ${delay}ms 后重试...`);
          await new Promise(resolve => setTimeout(resolve, delay));
        }
      }
    };
  });
  
  return resilientApi;
};

// 创建具有弹性的用户API
const resilientUserApi = createResilientApi(realUserApi, mockService);

export default resilientUserApi;