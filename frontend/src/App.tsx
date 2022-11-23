import React from 'react';
import './App.css';
import useWebSocket from "react-use-websocket";

function App() {

    const [chatHistory, setChatHistory] = React.useState<string[]>([]);
    const [message, setMessage] = React.useState<string>("");

    const websocket = useWebSocket("ws://localhost:8080/api/ws/chat", {
        onOpen: () => console.log("connected"),
        onMessage: (event) => {
            chatHistory.push(event.data);
        },
        onClose: () => console.log("disconnected"),
    });

    const sendMessage = () => {
        websocket.sendMessage(message);
    }

    return <>
        {
            chatHistory.map((message, index) => <div key={index}>{message}</div>)
        }
        <input type="text" onChange={(e) => setMessage(e.target.value)}/>
        <button onClick={sendMessage}>Send Message</button>
    </>
}

export default App;
