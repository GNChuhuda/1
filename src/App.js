import React, { useState } from 'react';
import './App.css';
import AccessControlModal from './AccessControlModal';

function App() {
  const [activeTab, setActiveTab] = useState('home');
  const [users, setUsers] = useState([]);
  const [showAddModal, setShowAddModal] = useState(false);
  const [newUserId, setNewUserId] = useState('');
  const [attributePool, setAttributePool] = useState(
    Array.from({length: 100}, (_, i) => ({id: i+1, name: `${i+1}`, selected: false}))
  );
  const [attributeFile, setAttributeFile] = useState(null);
  const [showAttributePool, setShowAttributePool] = useState(false);
  const [selectedAttributes, setSelectedAttributes] = useState([]);

  const [attributeUploaded, setAttributeUploaded] = useState(false);

  const handleAttributeFileUpload = (e) => {
    console.log('handleAttributeFileUpload called'); // 调试信息
    const file = e.target.files[0];
    if (!file) {
      console.log('No file selected');
      return;
    }
    console.log('File selected:', file.name); // 调试信息
    setAttributeFile(file);
    setAttributeUploaded(true);
    console.log('Calling parseAttributeFile'); // 调试信息
    parseAttributeFile(); // 上传后自动解析
  };

  const previewAttributeFile = () => {
    if (!attributeFile) return;
    const reader = new FileReader();
    reader.onload = (e) => {
      alert(`属性文件内容预览:\n\n${e.target.result}`);
    };
    reader.readAsText(attributeFile);
  };

  const parseAttributeFile = () => {
    console.log('parseAttributeFile called'); // 调试信息
    if (!attributeFile) {
      console.log('No attribute file selected');
      return;
    }
    
    const reader = new FileReader();
    reader.onload = (e) => {
      console.log('FileReader onload triggered'); // 调试信息
      try {
        const content = e.target.result;
        // 按行分割，去除空行和前后空格
        const lines = content.split('\n')
          .map(line => line.trim())
          .filter(line => line.length > 0);
        
        // 提取所有属性名，支持逗号、空格、制表符分隔
        let names = [];
        lines.forEach(line => {
          names = [...names, ...line.split(/[, \t]+/).map(n => n.trim()).filter(n => n)];
        });
        
        if (names.length === 0) {
          alert('错误：未找到有效属性名，请检查文件格式');
          return;
        }
        
        // 更新前100个属性，保持总数不变
        const updatedPool = attributePool.map((attr, index) => ({
          ...attr,
          name: index < names.length ? names[index] : attr.name,
          selected: false // 重置选择状态
        }));
        
        setAttributePool(updatedPool);
        setAttributeUploaded(true);
        alert(`成功加载${names.length}个属性，已更新属性池`);
      } catch (error) {
        console.error('属性文件解析错误:', error);
        alert('属性文件解析失败，请检查文件格式');
      }
    };
    reader.onerror = () => {
      alert('文件读取失败，请重试');
    };
    reader.readAsText(attributeFile);
  };
  const [accessControlPolicy, setAccessControlPolicy] = useState(null);
  const [showAccessControlModal, setShowAccessControlModal] = useState(false);
  const [plainTextFile, setPlainTextFile] = useState(null);
  const [privateKeyFile, setPrivateKeyFile] = useState(null);
  const [cipherTextFile, setCipherTextFile] = useState(null);
  const [encryptedResult, setEncryptedResult] = useState(null);
  const [decryptedResult, setDecryptedResult] = useState(null);
  const [testFiles, setTestFiles] = useState([]);
  const [testResult, setTestResult] = useState(null);
  const [showCipherPool, setShowCipherPool] = useState(false);

  const handleFileUpload = (event, type) => {
    const file = event.target.files[0];
    if (!file) return;
    
    if (type === 'plainText') {
      setPlainTextFile(file);
    } else if (type === 'privateKey') {
      setPrivateKeyFile(file);
    } else if (type === 'cipherText') {
      setCipherTextFile(file);
    }
  };

  const previewFile = (file) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      alert(`文件内容预览:\n\n${e.target.result}`);
    };
    reader.readAsText(file);
  };

  const handleEncrypt = () => {
    if (!accessControlPolicy || !plainTextFile) return;
    
    const reader = new FileReader();
    reader.onload = (e) => {
      // 模拟加密过程 - 实际应用中这里应该是真正的加密算法
      const plainText = e.target.result;
      const encrypted = btoa(plainText); // 简单使用base64编码模拟加密
      setEncryptedResult(encrypted);
    };
    reader.readAsText(plainTextFile);
  };

  const handleDecrypt = () => {
    if (!privateKeyFile || !cipherTextFile) return;
    
    const reader = new FileReader();
    reader.onload = (e) => {
      // 模拟解密过程 - 实际应用中这里应该是真正的解密算法
      const cipherText = e.target.result;
      const decrypted = atob(cipherText); // 简单使用base64解码模拟解密
      setDecryptedResult(decrypted);
    };
    reader.readAsText(cipherTextFile);
  };

  const downloadResult = (content, type) => {
    const blob = new Blob([content], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    
    const link = document.createElement('a');
    link.href = url;
    link.download = `${type}_${new Date().getTime()}.txt`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    setTimeout(() => {
      URL.revokeObjectURL(url);
    }, 100);
  };

  const generateKeyPair = () => {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const randomString = (length) => {
      let result = '';
      for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
      }
      return result;
    };
    return {
      publicKey: `pub_${randomString(16)}`,
      privateKey: `priv_${randomString(32)}`
    };
  };//模拟生成getKeyPair过程

  const addUser = () => {
    if (!newUserId.trim()) {
      alert('请输入有效的用户ID');
      return;
    }
    
    if (users.some(user => user.id === newUserId.trim())) {
      alert('该用户ID已存在');
      return;
    }
    
    const selectedAttributes = attributePool
      .filter(attr => attr.selected)
      .map(attr => ({name: attr.name}));
    
    if (selectedAttributes.length === 0) {
      alert('请选择至少一个属性');
      return;
    }
    
    const keyPair = generateKeyPair();
    const newUser = {
      id: newUserId.trim(),
      publicKey: keyPair.publicKey,
      privateKey: keyPair.privateKey,
      attributes: selectedAttributes
    };
    
    setUsers([...users, newUser]);
    setNewUserId('');
    // 重置属性池选择状态
    setAttributePool(attributePool.map(attr => ({...attr, selected: false})));
    setShowAddModal(false);
  };

  const deleteUser = (index) => {
    const newUsers = [...users];
    newUsers.splice(index, 1);
    setUsers(newUsers);
  };

  const downloadKeys = (user) => {
    const privateKeyBlob = new Blob([user.privateKey], { type: 'text/plain' });
    const privateKeyUrl = URL.createObjectURL(privateKeyBlob);
    
    const privateKeyLink = document.createElement('a');
    privateKeyLink.href = privateKeyUrl;
    privateKeyLink.download = `${user.id}_private.key`;
    document.body.appendChild(privateKeyLink);
    privateKeyLink.click();
    document.body.removeChild(privateKeyLink);
    
    setTimeout(() => {
      URL.revokeObjectURL(privateKeyUrl);
    }, 100);
  };

  return (
    <div className="App">
      <div className="button-container">
        <div className="logo-text" onClick={() => setActiveTab('home')} style={{cursor: 'pointer'}}>ABE-MET</div>
        <div className="icon-placeholder" >
          <img 
          src={`${process.env.PUBLIC_URL}/1.png`} 
          alt="系统图标"
          style={{
            width: '50px',
            height: '50px',
            marginright: '20px'
          }}
           />
        </div>
        <button 
          className="btn btn-register" 
          onClick={() => setActiveTab('register')}
        >
          用户注册
        </button>
        <button 
          className="btn btn-encrypt" 
          onClick={() => setActiveTab('encrypt')}
        >
          加密文件
        </button>
        <button 
          className="btn btn-decrypt" 
          onClick={() => setActiveTab('decrypt')}
        >
          解密文件
        </button>
        <button 
          className="btn btn-test" 
          onClick={() => setActiveTab('test')}
        >
          等值测试
        </button>
        <div className="role-container">
          <div className="role">Role</div>
          {activeTab === 'register' && <div className="role-label">Manager</div>}
          {activeTab === 'encrypt' && <div className="role-label">Sender</div>}
          {activeTab === 'decrypt' && <div className="role-label">Receiver</div>}
          {activeTab === 'test' && <div className="role-label">Tester</div>}
        </div>
      </div>

      {activeTab === 'home' && (
        <div className="home-content" >
          <img 
            src={`${process.env.PUBLIC_URL}/2.png`} 
            alt="系统图示"
            style={{
              maxWidth: '80%',
              maxHeight: '80%',
              objectFit: 'contain',
              marginBottom: '20px'
            }}
          />
          <h1 classname="home-title">基于属性基的密文多等值测试系统</h1>
        </div>
      )}

      {activeTab === 'register' && (
        <div className="register-content">
          <div className="user-list-container">
            <div className="button-group">
                {attributeUploaded && (
                  <div className="attributes-upload-pre" style={{marginRight: '10px'}}>
                    <span>总属性集合已上传</span>
                    <button 
                      className="btn btn-preview"
                      onClick={previewAttributeFile}
                      style={{padding: '5px 10px', fontSize: '12px',marginLeft: '10px'}}
                    >
                      预览
                    </button>
                  </div>
                )}
              <label className="btn btn-upload-attributes">
                  上传属性集合
                  <input 
                    type="file" 
                    accept=".txt"
                    style={{display: 'none'}}
                    onChange={handleAttributeFileUpload}
                  />
                </label>
              <button 
                className="btn btn-add-user"
                onClick={() => setShowAddModal(true)}
              >
                添加用户
              </button>

            </div>
            
            <table className="user-table">
              <thead>
                <tr>
                  <th >用户ID</th>
                  <th >私钥</th>
                  <th >属性</th>
                  <th >操作</th>
                </tr>
              </thead>
              <tbody>
                {users.length === 0 ? (
                  <tr>
                    <td colSpan="4">暂无用户，请点击上方按钮添加用户</td>
                  </tr>
                ) : (
                  users.map((user, index) => (
                    <tr key={index}>
                      <td>{user.id}</td>
                      <td>{"*".repeat(8)}</td>
                      <td className="attribute-hover-trigger">
                        {user.attributes && user.attributes.length > 0 ? (
                          <>
                            <span className="attribute-icon">S</span>
                            <div className="attribute-tooltip">
                              {user.attributes.map((attr, i) => (
                                <div key={i}>{attr.name}</div>
                              ))}
                            </div>
                          </>
                        ) : '-'}
                      </td>
                      <td>
                        <button 
                          className="btn-download"
                          onClick={() => downloadKeys(user)}
                        >
                          下载密钥
                        </button>
                        <button 
                          className="btn-delete"
                          onClick={() => deleteUser(index)}
                        >
                          删除
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          {showAddModal && (
            <div className="add-user-modal">
              <div className="modal-content">
                <h3>添加新用户</h3>
                <div className="input-group">
                  <input
                    type="text"
                    value={newUserId}
                    onChange={(e) => setNewUserId(e.target.value)}
                    placeholder="输入用户ID"
                    className="user-id-input"
                  />
                </div>
                
                <div className="attributes-display-section">
                  <h4 className="attributes-title">用户属性集合</h4>
                  <div className="attributes-divider"></div>
                  <div className="attributes-container">
                    {selectedAttributes.length === 0 ? (
                      <div className="empty-attributes-hint">请添加属性构建属性集</div>
                    ) : (
                      <div className="attributes-list">
                        {selectedAttributes.map((attr, index) => (
                          <div key={index} className="attribute-tag">
                            {attr.name}
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
                
                <div className="modal-buttons">
                  <button 
                    className="btn btn-generate-attributes"
                    onClick={() => {
                      if (!attributeFile) {
                        alert('请先返回用户注册界面上传属性集合文件');
                        return;
                      }
                      parseAttributeFile();
                      setShowAttributePool(true);
                    }}
                  >
                    构建属性集
                  </button>
                  <button 
                    className="btn btn-confirm"
                    onClick={addUser}
                  >
                    创建用户
                  </button>
                  <button 
                    className="btn btn-cancel"
                    onClick={() => {
                      setShowAddModal(false);
                      setAttributePool(attributePool.map(attr => ({...attr, selected: false})));
                    }}
                  >
                    取消
                  </button>
                </div>

                {showAttributePool && (
                  <div className="attribute-pool-modal">
                    <div className="modal-content" style={{
                      width: '600px',
                      maxWidth: '90vw',
                      padding: '20px'
                    }}>
                      <h5 style={{
                        fontSize: '14px',
                        marginBottom: '15px',
                        textAlign: 'center'
                      }}>属性池 (点击选择)</h5>
                      <div className="attribute-grid" style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(10, 1fr)',
                        gap: '8px',
                        maxHeight: '500px',
                        overflowY: 'auto',
                        fontSize: '12px'
                      }}>
                        {attributePool.map(attr => (
                          <div 
                            key={attr.id}
                            className={`attribute-item ${attr.selected ? 'selected' : ''}`}
                            style={{
                              padding: '5px',
                              textAlign: 'center',
                              border: '1px solid #ddd',
                              borderRadius: '3px',
                              cursor: 'pointer',
                              width:'100px'
                            }}
                            onClick={() => {
                              const updatedPool = attributePool.map(a => 
                                a.id === attr.id ? {...a, selected: !a.selected} : a
                              );
                              setAttributePool(updatedPool);
                              // 立即更新选中的属性列表
                              setSelectedAttributes(
                                updatedPool
                                  .filter(attr => attr.selected)
                                  .map(attr => ({name: attr.name}))
                              );
                            }}
                          >
                            {attr.name}
                          </div>
                        ))}
                      </div>
                      <button 
                        className="btn btn-confirm-attribute"
                        onClick={() => {
                          setShowAttributePool(false);
                          // 确保关闭时更新选中的属性列表
                          setSelectedAttributes(
                            attributePool
                              .filter(attr => attr.selected)
                              .map(attr => ({name: attr.name}))
                          );
                        }}
                      >
                        确认添加
                      </button>
                    </div>
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      )}
      {activeTab === 'encrypt' && (
        <div className="encrypt-content">
          <div className="encrypt-illustration" style={{
            backgroundImage: `url(${process.env.PUBLIC_URL}/3.png)`,
            backgroundSize: 'contain',
            backgroundRepeat: 'no-repeat',
            backgroundPosition: 'center'
          }}>
          </div>
          <div className="file-upload-section" style={{marginTop: '40px'}}>
            <div className="upload-group">
              <button 
                className="btn btn-upload"
                onClick={() => setShowAccessControlModal(true)}
              >
                设置访问控制结构
              </button>
              {accessControlPolicy && (
                <div className="file-info" style={{marginLeft: '12px', maxWidth: '60%', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap'}}>
                  <span>{accessControlPolicy}</span>
                </div>
              )}
            </div>

            <div className="upload-group">
              <label className="btn btn-upload">
                上传明文文件
                <input 
                  type="file" 
                  style={{display: 'none'}}
                  onChange={(e) => handleFileUpload(e, 'plainText')}
                />
              </label>
              {plainTextFile && (
                <div className="file-info">
                  <span>{plainTextFile.name}</span>
                  <button 
                    className="btn btn-preview"
                    onClick={() => previewFile(plainTextFile)}
                  >
                    预览
                  </button>
                </div>
              )}
            </div>
          </div>

          <button 
            className="btn btn-encrypt-action"
            disabled={!accessControlPolicy || !plainTextFile}
            onClick={handleEncrypt}
          >
            加密
          </button>

          {encryptedResult && (
            <div className="result-section">
              <h3>加密结果</h3>
              <div className="result-content">
                <pre>{encryptedResult}</pre>
                <button 
                  className="btn btn-download"
                  onClick={() => downloadResult(encryptedResult, 'encrypted')}
                >
                  下载加密文件
                </button>
              </div>
            </div>
          )}

          {showAccessControlModal && (
            <AccessControlModal
              attributes={attributePool}
              onClose={() => setShowAccessControlModal(false)}
              onConfirm={(policy) => {
                setAccessControlPolicy(policy);
                setShowAccessControlModal(false);
              }}
            />
          )}
        </div>
      )}
      {activeTab === 'decrypt' && (
        <div className="decrypt-content">
          <div className="decrypt-illustration">
              <img 
              src={`${process.env.PUBLIC_URL}/4.png`} 
              style={{
                width: '35%',
                height: '180px',
                objectFit: 'contain'
              }}
              alt="解密图示1"
            />
            <img 
              src={`${process.env.PUBLIC_URL}/5.png`} 
              style={{
                width: '35%',
                height: '180px',
                objectFit: 'contain'
              }}
              alt="解密图示2"
            />
          </div>
          <div className="file-upload-section" style={{marginTop: '40px'}}>
            <div className="upload-group">
              <label className="btn btn-upload">
                上传私钥文件
                <input 
                  type="file" 
                  style={{display: 'none'}}
                  onChange={(e) => handleFileUpload(e, 'privateKey')}
                />
              </label>
              {privateKeyFile && (
                <div className="file-info">
                  <span>{privateKeyFile.name}</span>
                  <button 
                    className="btn btn-preview"
                    onClick={() => previewFile(privateKeyFile)}
                  >
                    预览
                  </button>
                </div>
              )}
            </div>

            <div className="upload-group">
              <label className="btn btn-upload">
                上传密文文件
                <input 
                  type="file" 
                  style={{display: 'none'}}
                  onChange={(e) => handleFileUpload(e, 'cipherText')}
                />
              </label>
              {cipherTextFile && (
                <div className="file-info">
                  <span>{cipherTextFile.name}</span>
                  <button 
                    className="btn btn-preview"
                    onClick={() => previewFile(cipherTextFile)}
                  >
                    预览
                  </button>
                </div>
              )}
            </div>
          </div>

          <button 
            className="btn btn-decrypt-action"
            disabled={!privateKeyFile || !cipherTextFile}
            onClick={handleDecrypt}
          >
            解密
          </button>

          {decryptedResult && (
            <div className="result-section">
              <h3>解密结果</h3>
              <div className="result-content">
                <pre>{decryptedResult}</pre>
                <button 
                  className="btn-download"
                  onClick={() => downloadResult(decryptedResult, 'decrypted')}
                >
                  下载解密文件
                </button>
              </div>
            </div>
          )}
        </div>
      )}
      
      {activeTab === 'test' && (
        <div className="test-content" >
          <div style={{display: 'flex', gap: '20px'}}>
            <div style={{flex: 2}}>
              <div className="test-illustration" style={{
                backgroundImage: `url(${process.env.PUBLIC_URL}/6.png)`,
                backgroundSize: 'contain',
                backgroundRepeat: 'no-repeat',
                backgroundPosition: 'center',
              }}></div>
              <div className="file-upload-section" >
                <label className="btn btn-upload" style={{
                  marginTop: '20px'
                }}>
                  批量上传测试文件
              <input 
                type="file" 
                multiple
                style={{display: 'none'}}
                onChange={(e) => {
                  const files = Array.from(e.target.files);
                  if (files.length > 0) {
                    setTestFiles([
                      ...testFiles,
                      ...files.map(file => ({
                        id: Date.now() + Math.random(),
                        name: file.name,
                        file: file,
                        userId: null,
                        token: null
                      }))
                    ]);
                  }
                }}
              />
                </label>
              </div>

              <button 
                className="btn btn-test-action"
                disabled={testFiles.length === 0 || testFiles.some(f => !f.userId)}
                onClick={async () => {
                  if (testFiles.length === 0) {
                    alert('请先上传测试文件');
                    return;
                  }
                  if (testFiles.some(f => !f.userId)) {
                    alert('请先为所有测试文件获取用户授权token');
                    return;
                  }
                  
                  // 模拟等值测试 - 只比较文件内容
                  const contents = await Promise.all(
                    testFiles.map(file => {
                      return new Promise(resolve => {
                        const reader = new FileReader();
                        reader.onload = (e) => resolve(e.target.result);
                        reader.readAsText(file.file);
                      });
                    })
                  );
                  
                  const allEqual = contents.every(c => c === contents[0]);
                  setTestResult({
                    isEqual: allEqual,
                    files: testFiles
                  });
                }}
              >
                执行测试
              </button>

              <div className="test-file-list">
                <h3>待测试文件列表</h3>
                {testFiles.length === 0 ? (
                  <p style={{textAlign: 'center', color: '#999'}}>暂无文件，请上传测试文件</p>
                ) : (
                  <table>
              <thead>
                <tr>
                  <th>文件名</th>
                  <th>授权用户</th>
                  <th>操作</th>
                </tr>
              </thead>
                    <tbody>
                      {testFiles.map((testFile) => (
                        <tr key={testFile.id} style={{
                          borderBottom: '1px solid #eee',
                          '&:hover': {
                            backgroundColor: '#f9f9f9'
                          }
                        }}>
                          <td>{testFile.name}</td>
                          <td>
                            {testFile.userId || '未授权'}
                          </td>
                          <td>
                            <select
                              value={testFile.userId || ''}
                              onChange={(e) => {
                                const selectedUserId = e.target.value;
                                if (selectedUserId) {
                                  setTestFiles(testFiles.map(f => 
                                    f.id === testFile.id 
                                      ? {...f, userId: selectedUserId, token: `token_${selectedUserId}`} 
                                      : f
                                  ));
                                }
                              }}
                            >
                              <option value="">选择用户</option>
                              {users.map(user => (
                                <option key={user.id} value={user.id}>
                                  {user.id}
                                </option>
                              ))}
                            </select>
                          </td>
                          <td>
                            <button
                              onClick={() => {
                                setTestFiles(testFiles.filter(f => f.id !== testFile.id));
                              }}                     
                            >
                              删除
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
              </div>
            </div>

            {testResult && (
              <div style={{flex: 1}}>
                <div className="test-result">
                  <h3>测试结果</h3>
                  <div className="result-summary" style={{
                    color: testResult.isEqual ? '#4CAF50' : '#F44336',                  
                  }}>
                    {testResult.isEqual ? 'YES' : 'NO'}
                  </div>
                  <button 
                    className="btn-test-action"
                    onClick={() => setShowCipherPool(true)}
                  >
                    查看密文池
                  </button>
                </div>
              </div>
            )}
          </div>

          {showCipherPool && (
            <div className="cipher-pool-modal">
              <div className="modal-content">
                <h3>密文池</h3>
                <div className="ciphertext-pool">
                  {testResult.files.map((file, index) => (
                    <div className="ciphertext-pool-item"
                      key={index} 
                      onClick={() => previewFile(file.file)}
                    >
                      <div style={{
                        width: '24px',
                        height: '24px',
                        backgroundImage: `url(${process.env.PUBLIC_URL}/lock-icon.png)`,
                        backgroundSize: 'contain',
                        backgroundRepeat: 'no-repeat'
                      }}></div>
                      <span style={{textAlign: 'center'}}>CT{index + 1}</span>
                    </div>
                  ))}
                </div>
                <button 
                  className="btn-cancel"
                  onClick={() => setShowCipherPool(false)}
                >
                  关闭
                </button>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
 
export default App;
