import AxiosClient from "./AxiosClient";

const ChatApi = {
  getHrConversations: () => {
    return AxiosClient.get("/conversations/hr", { withAuth: true });
  },

  claimConversation: (id) => {
    return AxiosClient.post(`/conversations/${id}/claim`, {}, { withAuth: true });
  },

  getConversationMessages: (id) => {
    return AxiosClient.get(`/conversations/${id}/messages`, { withAuth: true });
  },

  deleteConversation: (id) => {
    return AxiosClient.delete(`/conversations/${id}`, { withAuth: true });
  },
};

export default ChatApi;
