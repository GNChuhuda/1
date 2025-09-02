import React, { useState } from 'react';
import './App.css';

function App() {
  const [activeTab, setActiveTab] = useState('home');
  const [users, setUsers] = useState([]);
  const [showAddModal, setShowAddModal] = useState(false);
  const [newUserId, setNewUserId] = useState('');
  const [attributes, setAttributes] = useState([]);
  const [newAttribute, setNewAttribute] = useState({name: '', value: ''});
  const [publicKeyFile, setPublicKeyFile] = useState(null);
  const [plainTextFile, setPlainTextFile] = useState(null);
  const [privateKeyFile, setPrivateKeyFile] = useState(null);
  const [cipherTextFile, setCipherTextFile] = useState(null);
  const [encryptedResult, setEncryptedResult] = useState(null);
  const [decryptedResult, setDecryptedResult] = useState(null);
  const [testFiles, setTestFiles] = useState([]);
  const [testResult, setTestResult] = useState(null);

  const handleFileUpload = (event, type) => {
    const file = event.target.files[0];
    if (!file) return;
    
    if (type === 'publicKey') {
      setPublicKeyFile(file);
    } else if (type === 'plainText') {
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
    if (!publicKeyFile || !plainTextFile) return;
    
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
    
    if (attributes.length === 0) {
      alert('请公布用户属性');
      return;
    }
    
    const keyPair = generateKeyPair();
    const newUser = {
      id: newUserId.trim(),
      publicKey: keyPair.publicKey,
      privateKey: keyPair.privateKey,
      attributes: [...attributes]
    };
    
    setUsers([...users, newUser]);
    setNewUserId('');
    setAttributes([]);
    setNewAttribute({name: '', value: ''});
    setShowAddModal(false);
  };

  const deleteUser = (index) => {
    const newUsers = [...users];
    newUsers.splice(index, 1);
    setUsers(newUsers);
  };

  const downloadKeys = (user) => {
    const publicKeyBlob = new Blob([user.publicKey], { type: 'text/plain' });
    const privateKeyBlob = new Blob([user.privateKey], { type: 'text/plain' });
    
    const publicKeyUrl = URL.createObjectURL(publicKeyBlob);
    const privateKeyUrl = URL.createObjectURL(privateKeyBlob);
    
    const publicKeyLink = document.createElement('a');
    publicKeyLink.href = publicKeyUrl;
    publicKeyLink.download = `${user.id}_public.key`;
    document.body.appendChild(publicKeyLink);
    publicKeyLink.click();
    document.body.removeChild(publicKeyLink);
    
    const privateKeyLink = document.createElement('a');
    privateKeyLink.href = privateKeyUrl;
    privateKeyLink.download = `${user.id}_private.key`;
    document.body.appendChild(privateKeyLink);
    privateKeyLink.click();
    document.body.removeChild(privateKeyLink);
    
    setTimeout(() => {
      URL.revokeObjectURL(publicKeyUrl);
      URL.revokeObjectURL(privateKeyUrl);
    }, 100);
  };

  return (
    <div className="App">
      <div className="button-container">
        <div className="logo-text">ABE-MET</div>
        <div className="icon-placeholder" style={{
          backgroundImage: `url(${process.env.PUBLIC_URL}/1.png)`,
          width: '50px',
          height: '50px',
          marginRight: '20px',
          backgroundSize: 'contain',
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center'
        }}></div>
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
      </div>

      {activeTab === 'home' && (
        <div className="home-content" style={{
          position: 'relative',
          height: 'calc(100vh - 60px)',
          backgroundColor: '#F8F1E0', // 调整后的背景色
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'flex-start', // 改为顶部对齐
          padding: '20px 0' // 调整内边距
        }}>
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
          <h1 style={{
            fontSize: '24px',
            color: '#333',
            margin: '20px',
            alignSelf: 'flex-end' // 右对齐
          }}>基于属性基的密文多等值测试系统</h1>
        </div>
      )}

      {activeTab === 'register' && (
        <div className="register-content">
          <div className="user-list-container">
            <button 
              className="btn btn-add-user"
              onClick={() => setShowAddModal(true)}
            >
              添加用户
            </button>
            
            <table className="user-table">
              <thead>
                <tr>
                  <th style={{textAlign: 'center'}}>用户ID</th>
                  <th style={{textAlign: 'center'}}>公钥</th>
                  <th style={{textAlign: 'center'}}>私钥</th>
                  <th style={{textAlign: 'center'}}>属性</th>
                  <th style={{textAlign: 'center'}}>操作</th>
                </tr>
              </thead>
              <tbody>
                {users.length === 0 ? (
                  <tr>
                    <td colSpan="4" style={{textAlign: 'center'}}>暂无用户，请点击上方按钮添加用户</td>
                  </tr>
                ) : (
                  users.map((user, index) => (
                    <tr key={index}>
                      <td>{user.id}</td>
                      <td>{user.publicKey}</td>
                      <td>{"*".repeat(8)}</td>
                      <td className="attribute-hover-trigger">
                        {user.attributes && user.attributes.length > 0 ? (
                          <>
                            <span className="attribute-icon">S</span>
                            <div className="attribute-tooltip">
                              {user.attributes.map((attr, i) => (
                                <div key={i}>
                                  {attr.name}: {attr.value}
                                </div>
                              ))}
                            </div>
                          </>
                        ) : '-'}
                      </td>
                      <td>
                        <button 
                          className="btn btn-download"
                          onClick={() => downloadKeys(user)}
                        >
                          下载密钥
                        </button>
                        <button 
                          className="btn btn-delete"
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
                
                <div className="attributes-section">
                  <h4>属性集合 </h4>
                  <div className="attribute-form">
                    <div className="attribute-inputs">
                      <input
                        type="text"
                        value={newAttribute.name}
                        onChange={(e) => setNewAttribute({...newAttribute, name: e.target.value})}
                        placeholder="属性名"
                        className="attribute-name-input"
                      />
                      <input
                        type="text"
                        value={newAttribute.value}
                        onChange={(e) => setNewAttribute({...newAttribute, value: e.target.value})}
                        placeholder="属性值"
                        className="attribute-value-input"
                      />
                    </div>
                    <button 
                      className="btn btn-add-attribute"
                      onClick={() => {
                        if (newAttribute.name && newAttribute.value) {
                          setAttributes([...attributes, newAttribute]);
                          setNewAttribute({name: '', value: ''});
                        }
                      }}
                    >
                      添加属性
                    </button>
                  </div>
                  
                  {attributes.length > 0 && (
                    <div className="attributes-list">
                      <div className="attributes-header">
                        <span>属性名</span>
                        <span>属性值</span>
                        <span>操作</span>
                      </div>
                      {attributes.map((attr, index) => (
                        <div key={index} className="attribute-item">
                          <span>{attr.name}</span>
                          <span>{attr.value}</span>
                          <button 
                            className="btn btn-remove-attribute"
                            onClick={() => setAttributes(attributes.filter((_, i) => i !== index))}
                          >
                            删除
                          </button>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
                <div className="modal-buttons">
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
                      setAttributes([]);
                    }}
                  >
                    取消
                  </button>
                </div>
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
              <label className="btn btn-upload">
                上传公钥文件
                  <input 
                  type="file" 
                  style={{display: 'none'}}
                  onChange={(e) => handleFileUpload(e, 'publicKey')}
                />
              </label>
              {publicKeyFile && (
                <div className="file-info">
                  <span>{publicKeyFile.name}</span>
                  <button 
                    className="btn btn-preview"
                    onClick={() => previewFile(publicKeyFile)}
                  >
                    预览
                  </button>
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
            disabled={!publicKeyFile || !plainTextFile}
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
        </div>
      )}
      {activeTab === 'decrypt' && (
        <div className="encrypt-content">
          <div className="encrypt-illustration" style={{
            display: 'flex',
            justifyContent: 'center',
            gap: '20px'
          }}>
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
            className="btn btn-encrypt-action"
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
                  className="btn btn-download"
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
        <div className="test-content" style={{
          border: '1px solid #e0e0e0',
          borderRadius: '12px',
          padding: '25px',
          margin: '25px 0',
          backgroundColor: '#ffffff',
          boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)'
        }}>
          <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            gap: '40px',
            alignItems: 'flex-start'
          }}>
            <div style={{flex: 1}}>
              <div className="test-illustration" style={{
                backgroundImage: `url(${process.env.PUBLIC_URL}/6.png)`,
                backgroundSize: 'contain',
                backgroundRepeat: 'no-repeat',
                backgroundPosition: 'center',
                height: '200px',
                marginBottom: '25px',
                border: '1px solid #f0f0f0',
                borderRadius: '8px',
                backgroundColor: '#f9f9f9'
              }}></div>

              <div className="file-upload-section" style={{marginBottom: '25px', textAlign: 'center'}}>
                <label className="btn btn-upload" style={{
                  display: 'inline-block',
                  padding: '12px 24px',
                  backgroundColor: '#4CAF50',
                  color: 'white',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  transition: 'all 0.3s ease',
                  fontSize: '16px',
                  fontWeight: '500',
                  boxShadow: '0 2px 5px rgba(0,0,0,0.1)',
                  ':hover': {
                    backgroundColor: '#3e8e41',
                    transform: 'translateY(-2px)',
                    boxShadow: '0 4px 8px rgba(0,0,0,0.15)'
                  },
                  ':active': {
                    transform: 'translateY(0)',
                    boxShadow: '0 2px 3px rgba(0,0,0,0.1)'
                  }
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
                style={{
                  padding: '14px 28px',
                  backgroundColor: '#2196F3',
                  color: 'white',
                  border: 'none',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  fontSize: '16px',
                  fontWeight: '600',
                  boxShadow: '0 3px 6px rgba(0,0,0,0.16)',
                  transition: 'all 0.3s',
                  marginTop: '20px',
                  width: '100%',
                  maxWidth: '300px',
                  ':hover': {
                    backgroundColor: '#0b7dda',
                    transform: 'translateY(-2px)',
                    boxShadow: '0 5px 10px rgba(0,0,0,0.2)'
                  },
                  ':active': {
                    transform: 'translateY(0)',
                    boxShadow: '0 2px 5px rgba(0,0,0,0.2)'
                  },
                  ':disabled': {
                    backgroundColor: '#cccccc',
                    cursor: 'not-allowed',
                    transform: 'none',
                    boxShadow: 'none'
                  }
                }}
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

              {testResult && (
                <div className="test-result" style={{
                  margin: '40px auto 0',
                  textAlign: 'center',
                  maxWidth: '800px',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  position: 'absolute',
                  bottom: '40px',
                  left: '50%',
                  transform: 'translateX(-50%)'
                }}>
                  <h3 style={{
                    borderBottom: '1px solid #eee',
                    paddingBottom: '10px',
                    marginBottom: '15px'
                  }}>测试结果</h3>
                  <div className="result-summary" style={{
                    fontSize: '24px',
                    fontWeight: 'bold',
                    color: testResult.isEqual ? '#4CAF50' : '#F44336',
                    margin: '20px 0'
                  }}>
                    {testResult.isEqual ? 'YES' : 'NO'}
                  </div>
                  
                  <div className="cipher-pool" style={{
                    display: 'flex',
                    justifyContent: 'center',
                    flexWrap: 'wrap',
                    gap: '15px',
                    marginTop: '30px'
                  }}>
                    {testResult.files.map((file, index) => (
                      <div 
                        key={index} 
                        style={{
                          border: '1px solid #ddd',
                          borderRadius: '8px',
                          padding: '10px 15px',
                          cursor: 'pointer',
                          display: 'flex',
                          flexDirection: 'column',
                          alignItems: 'center',
                          gap: '8px',
                          width: '100px',
                          ':hover': {
                            backgroundColor: '#f5f5f5'
                          }
                        }}
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
                </div>
              )}
            </div>

            <div style={{flex: 1}}>
              <div className="test-file-list" style={{
                border: '1px solid #eee',
                borderRadius: '4px',
                padding: '15px'
              }}>
                <h3 style={{
                  marginTop: '0',
                  paddingBottom: '10px',
                  borderBottom: '1px solid #eee'
                }}>待测试文件列表</h3>
                {testFiles.length === 0 ? (
                  <p style={{textAlign: 'center', color: '#999'}}>暂无文件，请上传测试文件</p>
                ) : (
                  <table style={{width: '100%'}}>
              <thead>
                <tr style={{
                  backgroundColor: '#f5f5f5'
                }}>
                  <th style={{padding: '10px', textAlign: 'center'}}>文件名</th>
                  <th style={{padding: '10px', textAlign: 'center'}}>授权用户</th>
                  <th style={{padding: '10px', textAlign: 'center'}}>操作</th>
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
                          <td style={{padding: '10px'}}>{testFile.name}</td>
                          <td style={{padding: '10px'}}>
                            {testFile.userId || '未授权'}
                          </td>
                          <td style={{padding: '10px'}}>
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
                              style={{
                                padding: '8px',
                                borderRadius: '4px',
                                border: '1px solid #ddd'
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
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
