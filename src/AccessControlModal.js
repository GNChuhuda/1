import React, { useState } from 'react';
import './AccessControlModal.css';

// 节点类型
const NodeType = {
  OPERATOR: 'operator',
  ATTRIBUTE: 'attribute',
  PLACEHOLDER: 'placeholder'
};

// 创建唯一ID
const generateUniqueId = () => {
  return Date.now().toString(36) + Math.random().toString(36).substr(2);
};

// 创建占位符节点
const createPlaceholder = () => ({
  type: NodeType.PLACEHOLDER,
  id: generateUniqueId(),
  content: '',
  editable: true
});

// 创建操作符节点（支持二元 and/or）
const createOperatorNode = (operator, left, right, isNested = false) => {
  // 深度复制左右节点
  const newLeft = left ? {...left} : createPlaceholder();
  const newRight = right ? {...right} : createPlaceholder();
  
  return {
    type: NodeType.OPERATOR,
    id: generateUniqueId(),
    operator,
    left: newLeft,
    right: newRight,
    editable: false,
    isNested: isNested
  };
};

// 创建一元 NOT 节点
const createNotNode = (child) => {
  const newChild = child ? {...child} : createPlaceholder();
  return {
    type: NodeType.OPERATOR,
    id: generateUniqueId(),
    operator: 'not',
    child: newChild,
    editable: false,
    isNested: true
  };
};

// 创建属性节点
const createAttributeNode = (attribute) => ({
  type: NodeType.ATTRIBUTE,
  content: attribute.name,
  editable: false
});

const AccessControlModal = ({ attributes, onClose, onConfirm }) => {
  const [expression, setExpression] = useState(createPlaceholder());
  const [showMenu, setShowMenu] = useState(false);
  const [menuPosition, setMenuPosition] = useState({ x: 0, y: 0 });
  const [selectedNode, setSelectedNode] = useState(null);
  
  const [showAttributeSelector, setShowAttributeSelector] = useState(false);

  // 渲染节点
  const renderNode = (node) => {
    switch (node.type) {
      case NodeType.OPERATOR:
        if (node.operator === 'not') {
          return (
            <div className={`operator-node ${node.isNested ? 'nested-operator' : ''}`}>
              <span className="operator-symbol">﹃</span>
              <div className="bracket-container">
                <span className="bracket">(</span>
                {node.child && node.child.type === NodeType.OPERATOR ? (
                  <div className="nested-bracket">
                    {renderNode(node.child)}
                  </div>
                ) : (
                  renderNode(node.child)
                )}
                <span className="bracket">)</span>
              </div>
            </div>
          );
        }
        return (
          <div className={`operator-node ${node.isNested ? 'nested-operator' : ''}`}>
            <div className="bracket-container">
              <span className="bracket">(</span>
              {node.left.type === NodeType.OPERATOR ? (
                <div className="nested-bracket">
                  {renderNode(node.left)}
                </div>
              ) : (
                renderNode(node.left)
              )}
              <span className="bracket">)</span>
            </div>
            <span className="operator-symbol">
              {node.operator === 'and' ? '∧' : '∨'}
            </span>
            <div className="bracket-container">
              <span className="bracket">(</span>
              {node.right.type === NodeType.OPERATOR ? (
                <div className="nested-bracket">
                  {renderNode(node.right)}
                </div>
              ) : (
                renderNode(node.right)
              )}
              <span className="bracket">)</span>
            </div>
          </div>
        );
      case NodeType.ATTRIBUTE:
        return (
          <span className="attribute-node">
            {node.content}
          </span>
        );
      case NodeType.PLACEHOLDER:
        return (
          <button
            className={`placeholder-button ${node.editable ? 'editable' : ''}`}
            onClick={(e) => handleNodeClick(e, node)}
            onContextMenu={(e) => handleNodeRightClick(e, node)}
          >
            {node.editable ? '' : node.content}
          </button>
        );
      default:
        return null;
    }
  };

  const handleNodeClick = (e, node) => {
    e.stopPropagation();
    if (node.editable) {
      setSelectedNode(node);
    }
  };

  const handleNodeRightClick = (e, node) => {
    e.preventDefault();
    e.stopPropagation();
    
    // 仅允许在占位符（小括号里的输入框）上右击
    if (node.type === NodeType.PLACEHOLDER && node.editable) {
      setSelectedNode(node);
      setMenuPosition({ x: e.clientX, y: e.clientY });
      setShowMenu(true);
    }
  };

  const insertOperator = (operator) => {
    if (!selectedNode) return;

    if (selectedNode.type === NodeType.PLACEHOLDER) {
      let newNode;
      if (operator === 'not') {
        // 一元非：替换为 NOT 节点，子节点为新的占位符
        newNode = createNotNode();
      } else {
        // 二元与/或：替换为带两个占位符的节点
        newNode = createOperatorNode(operator);
      }
      const updatedExpression = updateNodeInTree(expression, selectedNode.id, newNode);
      setExpression(updatedExpression);
    }

    setShowMenu(false);
    setSelectedNode(null);
  };

  const updateNodeInTree = (root, nodeId, newNode) => {
    if (!root) return root;
    
    // 创建新对象避免直接修改原对象
    const newRoot = {...root};
    
    if (newRoot.id === nodeId) {
      return newNode;
    }
    
    if (newRoot.type === NodeType.OPERATOR) {
      if (newRoot.operator === 'not') {
        newRoot.child = updateNodeInTree(newRoot.child, nodeId, newNode);
      } else {
        newRoot.left = updateNodeInTree(newRoot.left, nodeId, newNode);
        newRoot.right = updateNodeInTree(newRoot.right, nodeId, newNode);
      }
    }
    
    return newRoot;
  };

  const insertAttribute = (attribute) => {
    if (!selectedNode || selectedNode.type !== NodeType.PLACEHOLDER) return;

    const newNode = createAttributeNode(attribute);
    const updatedExpression = updateNodeInTree(expression, selectedNode.id, newNode);
    
    // 确保只更新了目标节点
    if (JSON.stringify(updatedExpression) !== JSON.stringify(expression)) {
      setExpression(updatedExpression);
    }
    
    setShowAttributeSelector(false);
    setSelectedNode(null);
  };

  const convertExpressionToString = (node) => {
    if (!node) return '';
    
    if (node.type === NodeType.ATTRIBUTE) {
      return node.content;
    }
    
    if (node.type === NodeType.OPERATOR) {
      if (node.operator === 'not') {
        const childStr = convertExpressionToString(node.child);
        return `﹃(${childStr})`;
      }
      const leftStr = convertExpressionToString(node.left);
      const rightStr = convertExpressionToString(node.right);
      if (node.operator === 'and') {
        return `(${leftStr} ∧ ${rightStr})`;
      } else if (node.operator === 'or') {
        return `(${leftStr} ∨ ${rightStr})`;
      }
    }
    
    return '';
  };

  const validateExpression = (node) => {
    if (!node) return false;
    
    if (node.type === NodeType.PLACEHOLDER && node.editable) {
      return false;
    }
    
    if (node.type === NodeType.OPERATOR) {
      if (node.operator === 'not') {
        return validateExpression(node.child);
      }
      return validateExpression(node.left) && validateExpression(node.right);
    }
    
    return true;
  };

  return (
    <div className="access-control-modal">
      <div className="modal-content">
        <h3>设置访问控制结构</h3>
        
        <div className="expression-editor">
          {renderNode(expression)}
        </div>
        
        {showMenu && (
          <div 
            className="context-menu" 
            style={{ left: menuPosition.x, top: menuPosition.y }}
            onClick={(e) => e.stopPropagation()}
          >
            <button onClick={() => insertOperator('and')}>与 (∧)</button>
            <button onClick={() => insertOperator('or')}>或 (∨)</button>
            <button onClick={() => insertOperator('not')}>非 (﹃)</button>
            <div className="menu-divider"></div>
            <button onClick={() => {
              setShowAttributeSelector(true);
              setShowMenu(false);
            }}>选择属性</button>
          </div>
        )}

        {showAttributeSelector && (
          <div className="attribute-selector-overlay">
            <div className="attribute-selector">
              <h4>选择属性</h4>
              <div className="attribute-list">
                {attributes.map((attr, index) => (
                  <div 
                    key={index} 
                    className="attribute-item"
                    onClick={() => {
                      insertAttribute(attr);
                    }}
                  >
                    {attr.name}
                  </div>
                ))}
              </div>
              <button 
                className="btn-cancel" 
                onClick={() => setShowAttributeSelector(false)}
              >
                取消
              </button>
            </div>
          </div>
        )}

        <div className="modal-buttons">
          <button className="btn-cancel" onClick={onClose}>取消</button>
            <button 
            className="btn-confirm" 
            onClick={() => {
              if (validateExpression(expression)) {
                const expressionString = convertExpressionToString(expression);
                onConfirm(expressionString);
              } else {
                alert('请完成所有表达式');
              }
            }}
            disabled={!validateExpression(expression)}
          >
            确认
          </button>
        </div>
      </div>
    </div>
  );
};

export default AccessControlModal;
