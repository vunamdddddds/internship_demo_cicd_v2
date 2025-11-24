import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const SOCKET_URL = import.meta.env.VITE_SOCKET_URL ;

class WebSocketService {
    stompClient = null;
    subscriptions = new Map();
    connectionPromise = null;

    connect() {
        if (this.stompClient && this.stompClient.active) {
            return;
        }

        if (!this.connectionPromise) {
            this.connectionPromise = new Promise((resolve, reject) => {
                const token = localStorage.getItem("AccessToken");
                if (!token) {
                    console.error("No access token found for WebSocket connection");
                    reject("No Token");
                    return;
                }

                this.stompClient = new Client({
                    webSocketFactory: () => new SockJS(SOCKET_URL),
                    connectHeaders: {
                        Authorization: `Bearer ${token}`,
                    },
                    reconnectDelay: 5000,
                    heartbeatIncoming: 4000,
                    heartbeatOutgoing: 4000,
                    onConnect: () => {
                        console.log('WebSocket connected');
                        resolve();
                    },
                    onStompError: (frame) => {
                        console.error('Broker reported error: ' + frame.headers['message']);
                        console.error('Additional details: ' + frame.body);
                        reject(frame);
                    },
                });

                this.stompClient.activate();
            });
        }
    }

    async subscribe(topic, callback) {
        if (!this.connectionPromise) {
            this.connect();
        }

        await this.connectionPromise;

        if (this.subscriptions.has(topic)) {
            return;
        }

        if (this.stompClient && this.stompClient.active) {
            const subscription = this.stompClient.subscribe(topic, (message) => {
                callback(JSON.parse(message.body));
            });
            this.subscriptions.set(topic, subscription);
            console.log(`Subscribed to ${topic}`);
        } else {
            console.error("Cannot subscribe, STOMP client is not active.");
        }
    }

    unsubscribe(topic) {
        if (this.subscriptions.has(topic)) {
            this.subscriptions.get(topic).unsubscribe();
            this.subscriptions.delete(topic);
            console.log(`Unsubscribed from ${topic}`);
        }
    }

    sendMessage(destination, body) {
        if (this.stompClient && this.stompClient.active) {
            this.stompClient.publish({ destination, body: JSON.stringify(body) });
        } else {
            console.error("Cannot send message, STOMP client is not active.");
        }
    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.deactivate();
            this.stompClient = null;
            this.connectionPromise = null;
            this.subscriptions.clear();
            console.log('WebSocket disconnected');
        }
    }
}

const webSocketService = new WebSocketService();
export default webSocketService;
