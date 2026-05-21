// Mock数据服务
// 在后端API不可用时提供模拟数据支持

// 模拟属性池数据响应
const mockAttributePoolResponse = {
  success: true,
  message: '属性池数据保存成功',
  data: {
    savedCount: 10,
    timestamp: new Date().toISOString(),
    status: 'pending'
  }
};

// 模拟用户数据
const mockUsers = [
  { id: '1', name: '用户1', attributes: [{id: '1', name: '属性1'}, {id: '2', name: '属性2'}] },
  { id: '2', name: '用户2', attributes: [{id: '3', name: '属性3'}, {id: '4', name: '属性4'}] }
];

// Mock API服务
const mockService = {
  // 模拟发送属性池数据
  sendAttributePoolData: (attributeData) => {
    console.log('Mock API: 接收属性池数据', attributeData);
    // 模拟网络延迟
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve(mockAttributePoolResponse);
      }, 300);
    });
  },
  
  // 获取所有用户
  getAllUsers: () => {
    console.log('Mock API: 获取所有用户');
    return Promise.resolve(mockUsers);
  },
  
  // 创建用户并返回私钥文件
  createUser: (userData) => {
    console.log('Mock API: 创建用户', userData);
    // 模拟服务器生成私钥文件
    const privateKey = `-----BEGIN PRIVATE KEY-----
MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQD${Date.now()}
U29tZS1wcm92aWRlZC1wcml2YXRlLWtleS1mb3ItVXNlcjotKioqKioqKioqKioq
KioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioq
KioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioq
KioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioq
KioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioq
OTkxMjM0NTY3ODkw
-----END PRIVATE KEY-----`;
    
    // 模拟文件下载的响应
    const response = {
      success: true,
      message: '用户创建成功，私钥已生成',
      data: {
        userId: userData.id || Date.now().toString(),
        attributes: userData.attributes || [],
        timestamp: new Date().toISOString(),
        // 返回模拟的私钥数据，实际前端需要处理文件下载
        privateKeyFile: {
          fileName: `private_key_${userData.id || Date.now()}.pem`,
          content: privateKey,
          contentType: 'application/x-pem-file'
        }
      }
    };
    
    return Promise.resolve(response);
  }
};

export default mockService;