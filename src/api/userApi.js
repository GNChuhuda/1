// 用户API服务
// 封装用户相关的API调用
import apiService from './apiService';
import endpoints from './endpoints';

// 用户API服务
const userApi = {
  // 发送属性池数据到服务器
  sendAttributePoolData: (attributeData) => {
    return apiService.post(endpoints.users.attributePool, attributeData);
  },
  
  // 获取所有用户
  getAllUsers: () => {
    return apiService.get(endpoints.users.getAll);
  },
  
  // 创建新用户并处理私钥文件下载
  createUser: async (userData) => {
    try {
      // 验证数据格式
      if (!userData || !userData.userId || !userData.attributes || !Array.isArray(userData.attributes) || userData.attributes.length === 0) {
        throw new Error('无效的用户数据：userId不能为空，attributes必须是非空数组');
      }
      
      console.log('发送到后端的用户数据:', userData);
      
      // 根据新的后端API要求，直接发送完整的userData对象作为请求体
      // 后端使用@RequestBody接收整个UserRequestDto对象
      const response = await apiService.post(
        endpoints.users.create, 
        userData
      );
      
      // 根据后端UserDto结构，直接从response.data.privateKey获取私钥字符串
      if (response && response.data && response.data.privateKey) {
        // 直接使用后端返回的privateKey字符串
        const privateKeyContent = response.data.privateKey;
        
        // 下载私钥为txt文件
        const blob = new Blob([privateKeyContent], { type: 'text/plain' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `privateKey_${response.data.id || 'user'}.txt`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        
        return {
          ...response,
          privateKeyContent,
          publicKey: response.data?.publicKey || ''
        };
      }
      return response;
    } catch (error) {
      console.error('创建用户失败:', error);
      throw error;
    }
  },
  
  // 根据ID获取用户信息
  getUserById: (userId) => {
    return apiService.get(endpoints.users.getById(userId));
  },
  
  // 更新用户信息
  updateUser: (userId, userData) => {
    return apiService.put(endpoints.users.update(userId), userData);
  },
  
  // 删除用户
  deleteUser: (userId) => {
    return apiService.delete(endpoints.users.delete(userId));
  }
};

export default userApi;