import React, { useState, useEffect, useRef } from "react";
import { useOutletContext } from "react-router-dom";
import "./ChatManagement.css";
import { getHrConversations, getConversationMessages, claimConversation } from "~/services/ChatService";
import webSocketService from "~/api/WebSocketApi";
import { Loader2, Check, Trash2 } from "lucide-react";
import { deleteConversation } from "~/services/ChatService.jsx";

const ChatManagement = () => {
  const { user: currentUser } = useOutletContext(); // Get current user from layout
  const [assignedConversations, setAssignedConversations] = useState([]);
  const [unassignedConversations, setUnassignedConversations] = useState([]);
  const [messages, setMessages] = useState([]);
  const [selectedConversation, setSelectedConversation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [loadingMessages, setLoadingMessages] = useState(false);
  const [newMessage, setNewMessage] = useState("");
  const messagesEndRef = useRef(null);

  useEffect(() => {
    const setupConnections = async () => {
        await webSocketService.connect();
        fetchAndSetConversations();

        const unassignedTopic = "/topic/conversations/unassigned";
        webSocketService.subscribe(unassignedTopic, (newConversation) => {
            setUnassignedConversations(prev => [newConversation, ...prev]);
        });

        const claimedTopic = "/topic/conversations/claimed";
        webSocketService.subscribe(claimedTopic, (claimedConversation) => {
            setUnassignedConversations(prev => prev.filter(c => c.id !== claimedConversation.id));
            if (currentUser?.id === claimedConversation.hr.id) {
                setAssignedConversations(prev => [claimedConversation, ...prev]);
            }
            if (selectedConversation?.id === claimedConversation.id) {
                setSelectedConversation(claimedConversation);
            }
        });
    }

    setupConnections();

    return () => {
        webSocketService.unsubscribe("/topic/conversations/unassigned");
        webSocketService.unsubscribe("/topic/conversations/claimed");
        webSocketService.disconnect();
    };
  }, [currentUser]); // Rerun if user context changes

  const fetchAndSetConversations = async () => {
    setLoading(true);
    const data = await getHrConversations();
    if (data) {
      setAssignedConversations(data.assignedConversations || []);
      setUnassignedConversations(data.unassignedConversations || []);
    }
    setLoading(false);
  };

  useEffect(() => {
    if (!selectedConversation) {
        setMessages([]);
        return;
    }

    // Clear previous messages and set loading state immediately
    setMessages([]);
    setLoadingMessages(true);

    const fetchMessages = async () => {
      const data = await getConversationMessages(selectedConversation.id);
      setMessages(data);
      setLoadingMessages(false);
    };

    fetchMessages();

    const topic = `/topic/conversation/${selectedConversation.id}`;
    webSocketService.subscribe(topic, (newMessage) => {
        if(selectedConversation && newMessage.conversationId === selectedConversation.id) {
            setMessages(prevMessages => [...prevMessages, newMessage]);
        }
    });

    return () => {
      webSocketService.unsubscribe(topic);
    };

  }, [selectedConversation]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleConversationClick = (conv) => {
    setSelectedConversation(conv);
  };

  const handleDelete = async (e, conversationId) => {
    e.stopPropagation(); // Prevent the click from selecting the conversation
    if (window.confirm("Bạn có chắc chắn muốn xóa cuộc trò chuyện này không? Hành động này không thể hoàn tác.")) {
        const success = await deleteConversation(conversationId);
        if (success) {
            setAssignedConversations(prev => prev.filter(c => c.id !== conversationId));
            if (selectedConversation?.id === conversationId) {
                setSelectedConversation(null);
            }
        }
    }
  };

  const handleClaim = async () => {
    if (!selectedConversation) return;
    const success = await claimConversation(selectedConversation.id);
    if (success) {
        // The websocket broadcast will handle the state update
        // refresh conversation lists after claiming // optimize bad
        const data=await getHrConversations();
setAssignedConversations(data.assignedConversations || []);
setUnassignedConversations(data.unassignedConversations || []);
      
    }
  };

  const handleSendMessage = () => {
    if (newMessage.trim() && selectedConversation) {
      const payload = {
        conversationId: selectedConversation.id,
        content: newMessage.trim(),
      };
      webSocketService.sendMessage("/app/chat.sendMessage", payload);
      setNewMessage("");
    }
  };

  const renderConversationList = (title, conversations) => (
    <div className="conversation-section">
        <h4>{title} ({conversations.length})</h4>
        {conversations.map(conv => (
            <div 
                key={conv.id} 
                className={`conversation-item ${selectedConversation?.id === conv.id ? "active" : ""}`}
                onClick={() => handleConversationClick(conv)}
            >
                <div className="conv-avatar">{conv.candidateName.charAt(0)}</div>
                <div className="conv-details">
                <div className="conv-header">
                    <span className="conv-name">{conv.candidateName}</span>
                    <span className="conv-time">{new Date(conv.lastMessageTimestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                </div>
                <p className="conv-last-message">{conv.lastMessage}</p>
                </div>
            </div>
        ))}
    </div>
  );

  const renderChatContent = () => {
    if (!selectedConversation) {
        return (
            <div className="no-conversation-selected">
                <p>Chọn một cuộc trò chuyện để xem tin nhắn</p>
            </div>
        );
    }

    const isUnassigned = selectedConversation.hrId === null;
    const isAssignedToMe = selectedConversation?.hrId === currentUser?.id;

    let content;
    if (isUnassigned) {
        content = (
            <div className="claim-container">
                <button onClick={handleClaim} className="claim-button">
                    <Check size={20} /> Nhận cuộc trò chuyện này
                </button>
            </div>
        );
    } else if (isAssignedToMe) {
        content = (
            <div className="chat-input-area">
                <input 
                    type="text" 
                    placeholder="Nhập tin nhắn..." 
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
                />
                <button onClick={handleSendMessage}>Gửi</button>
            </div>
        );
    } else {
        content = (
            <div className="claim-container">
                <p>Cuộc trò chuyện này đã được một HR khác phụ trách.</p>
            </div>
        );
    }

    return (
        <>
            <div className="chat-header">
                <h3>{selectedConversation.candidateName}</h3>
            </div>
            <div className="chat-messages">
                {loadingMessages ? (
                    <Loader2 className="animate-spin self-center mt-4" />
                ) : (
                    messages.map(msg => {
                      return ( <div key={msg.id} className={`message-bubble ${msg.senderId === currentUser?.id ? "sent" : "received"}`}>
                            <p className="message-text">{msg.content}</p>
                            <span className="message-time">{new Date(msg.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                        </div>);
                       
                    })
                )}
                <div ref={messagesEndRef} />
            </div>
            {content}
        </>
    );
  }

  return (
    <div className="chat-management-container">
      <div className="conversation-list">
        <div className="list-header">
          <h2>Tin nhắn</h2>
        </div>
        <div className="list-body">
          {loading ? (
            <p style={{ textAlign: 'center', marginTop: '20px' }}>Đang tải...</p>
          ) : (
            <>
              {renderConversationList("Hàng chờ", unassignedConversations)}
              <div className="conversation-section">
                  <h4>Trò chuyện của tôi ({assignedConversations.length})</h4>
                  {assignedConversations.map(conv => (
                      <div 
                          key={conv.id} 
                          className={`conversation-item ${selectedConversation?.id === conv.id ? "active" : ""}`}
                          onClick={() => handleConversationClick(conv)}
                      >
                          <div className="conv-avatar">{conv.candidateName.charAt(0)}</div>
                          <div className="conv-details">
                              <div className="conv-header">
                                  <span className="conv-name">{conv.candidateName}</span>
                                  <span className="conv-time">{new Date(conv.lastMessageTimestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                              </div>
                              <p className="conv-last-message">{conv.lastMessage}</p>
                          </div>
                          <button className="delete-conv-btn" onClick={(e) => handleDelete(e, conv.id)}>
                              <Trash2 size={16} />
                          </button>
                      </div>
                  ))}
              </div>
            </>
          )}
        </div>
      </div>

      <div className="chat-detail">
        {renderChatContent()}
      </div>
    </div>
  );
};

export default ChatManagement;
