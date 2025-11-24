import { toast } from "react-toastify";
import ChatApi from "~/api/ChatApi";

export const getHrConversations = async () => {
  try {
    const res = await ChatApi.getHrConversations();
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể kết nối đến server");
    }
    return [];
  }
};

export const getConversationMessages = async (id) => {
  try {
    const res = await ChatApi.getConversationMessages(id);
    return res;
  } catch (err) {
    if (!err.response) {
      toast.error("Không thể tải tin nhắn");
    }
    return [];
  }
};

export const claimConversation = async (id) => {
  try {
    await ChatApi.claimConversation(id);
    toast.success("Đã nhận cuộc trò chuyện!");
    return true;
  } catch (err) {
    toast.error("Không thể nhận cuộc trò chuyện này.");
    return false;
  }
};

export const deleteConversation = async (id) => {
  try {
    await ChatApi.deleteConversation(id);
    toast.success("Đã xóa cuộc trò chuyện.");
    return true;
  } catch (err) {
    toast.error("Không thể xóa cuộc trò chuyện này.");
    return false;
  }
};
