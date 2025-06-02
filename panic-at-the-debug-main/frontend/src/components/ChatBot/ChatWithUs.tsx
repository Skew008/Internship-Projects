/* eslint-disable prefer-const */
import React, { useEffect, useRef, useState } from "react";

interface Provider {
  name: string;
  apiKey: string;
  model: string;
}

interface Config {
  enableMessageCooldown: boolean;
  cooldownDuration: number;
  botReplyDelay: number;
  chatProvider: string;
  apiKey: string;
  modelName: string;
  supportedProviders: {
    [key: string]: Provider;
  };
}

interface ChatMessage {
  role: "user" | "assistant";
  content: string;
}

const ChatWithUs: React.FC = () => {
  const [config, setConfig] = useState<Config | null>(null);
  const [chatHistory, setChatHistory] = useState<ChatMessage[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [input, setInput] = useState("");
  const [isTyping, setIsTyping] = useState(false);
  const [isSending, setIsSending] = useState(false);
  const chatEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
  const fetchConfig = async () => {
    try {
      const res = await fetch("./config.json");
      const data = await res.json();
      console.log("Loaded config:", data); // ADD THIS
      setConfig(data);
    } catch (error) {
      console.error("Error loading config:", error);
    }
  };

  fetchConfig();
}, []);

  const scrollToBottom = () => {
    chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(scrollToBottom, [chatHistory, isTyping]);

  const handleSend = async () => {
  console.log("handleSend triggered"); // ADD THIS
  if (!input.trim() || isTyping || !config) {
    console.log("Conditions not met to send:", { input, isTyping, config });
    return;
  }

  const userMessage: ChatMessage = { role: "user", content: input.trim() };
  setChatHistory((prev) => [...prev, userMessage]);
  setInput("");
  setIsTyping(true);
  setIsSending(config.enableMessageCooldown);

  try {
    console.log("Sending message:", userMessage.content);
    const reply = await getAIResponse(userMessage.content, config, [...chatHistory, userMessage]);
    console.log("AI reply:", reply);
    setChatHistory((prev) => [...prev, { role: "assistant", content: reply }]);
  } catch (error) {
    console.error("handleSend error:", error);
    setChatHistory((prev) => [
      ...prev,
      { role: "assistant", content: "Error getting response. Try again." },
    ]);
  } finally {
    setIsTyping(false);
    if (config.enableMessageCooldown) {
      setTimeout(() => setIsSending(false), config.cooldownDuration);
    }
  }
};


  const handleProviderChange = (providerKey: string) => {
    if (!config) return;

    const newProvider = config.supportedProviders[providerKey];
    setConfig({
      ...config,
      chatProvider: providerKey,
      apiKey: newProvider.apiKey,
      modelName: newProvider.model,
    });

    setChatHistory((prev) => [
      ...prev,
      { role: "assistant", content: `Switched to ${newProvider.name}` },
    ]);
  };

  return (
    <div className="fixed bottom-4 right-4 z-50">
      <button
        id="openChatButton"
        onClick={() => setIsOpen((prev) => !prev)}
        className="bg-[#6FE6FC] text-black px-4 py-2 rounded-lg shadow-lg"
      >
        <img
        src="/assets/chat.png"
        alt="Chat with us"
        className="w-12 h-12" /* Adjust size as needed */
      />
      </button>

      {isOpen && (
        <div
          id="chatWithUs"
          className="w-[350px] h-[500px] bg-blue-200 shadow-lg rounded-lg mt-2 flex flex-col overflow-hidden"
        >
          <div className="flex justify-between items-center p-3 border-b">
            <h2 className="font-bold text-lg text-black">PathSync AI</h2>
            <select
              id="chatProviderSelect"
              className="ml-auto text-sm border rounded px-2 py-1 border-black text-black"
              onChange={(e) => handleProviderChange(e.target.value)}
              value={config?.chatProvider}
            >
              {config &&
                Object.entries(config.supportedProviders).map(([key, provider]) => (
                  <option key={key} value={key}>
                    {provider.name}
                  </option>
                ))}
            </select>
          </div>

          <div
            id="chatMessages"
            className="flex-1 flex flex-col space-y-2 p-3 overflow-y-auto bg-gray-200"
          >
            {chatHistory.map((msg, idx) => (
              <div
                key={idx}
                className={`${
                  msg.role === "user"
                    ? "self-end bg-gray-400 text-right"
                    : "self-start bg-blue-300 text-left"
                } p-2 rounded-lg max-w-[75%]`}
              >
                <span className="font-medium text-black">
                  {msg.role === "user" ? "You" : "Bot"}:
                </span>{" "}
                <span dangerouslySetInnerHTML={{ __html: formatMarkdown(msg.content) }} />
              </div>
            ))}

            {isTyping && (
              <div className="self-start bg-blue-100 p-2 rounded-lg max-w-[75%]">
                <span className="font-medium text-gray-700">Bot:</span> <em>typing...</em>
              </div>
            )}
            <div ref={chatEndRef} />
          </div>

          <div className="p-3 border-t flex items-center">
            <textarea
  id="chatInput"
  className="flex-1 border rounded p-2 resize-none text-sm text-black"
  value={input}
  onChange={(e) => setInput(e.target.value)}
  placeholder="Type your message..."
  disabled={isSending || !config}
/>
<button
  id="sendMessageBtn"
  onClick={handleSend}
  className={`ml-2 bg-blue-400 text-black px-3 py-2 rounded ${
    isSending || !config ? "opacity-50 cursor-not-allowed" : ""
  }`}
  disabled={isSending || !config}
>
  Send
</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ChatWithUs;



async function getAIResponse(
  message: string,
  config: Config,
  chatHistory: ChatMessage[]
): Promise<string> {
  if (config.chatProvider === "gemini") {
    return await getGeminiResponse(message, config, chatHistory);
  }
  return "Provider not supported.";
}

async function getGeminiResponse(
  message: string,
  config: Config,
  chatHistory: ChatMessage[]
): Promise<string> {
  const API_URL = `https://generativelanguage.googleapis.com/v1beta/models/${config.modelName}:generateContent?key=${config.apiKey}`;

  const history = [
    {
      role: "user",
      parts: [
        {
          text: "You are a roadmap assistant. You only answer questions related to career roadmaps and learning paths for topics like Java, JavaScript, Frontend, Backend, and Testing.",
        },
      ],
    },
    {
      role: "model",
      parts: [
        {
          text: "Got it. I will only assist with technology-related topics.",
        },
      ],
    },
    ...chatHistory.map((msg) => ({
      role: msg.role === "assistant" ? "model" : "user",
      parts: [{ text: msg.content }],
    })),
    { role: "user", parts: [{ text: message }] },
  ];

  try {
    const res = await fetch(API_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        contents: history,
        generationConfig: {
          temperature: 0.7,
          topK: 40,
          topP: 0.95,
          maxOutputTokens: 1024,
        },
      }),
    });

    if (!res.ok) {
      const errorBody = await res.text();
      console.error("Gemini API error:", errorBody);
      throw new Error(`Gemini API failed: ${res.statusText}`);
    }

    const data = await res.json();
    const part = data?.candidates?.[0]?.content?.parts?.[0]?.text;

    if (!part) {
      console.error("No valid response from Gemini:", data);
      return "Sorry, I couldn't understand that.";
    }

    return part;
  } catch (error) {
    console.error("getGeminiResponse error:", error);
    return "Something went wrong while contacting the AI.";
  }
}


function formatMarkdown(text: string): string {
  let formatted = text
    .replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>")
    .replace(/\*(.*?)\*/g, "<em>$1</em>");

  const lines = formatted.split("\n");
  let result: string[] = [];
  let inList = false;

  for (const line of lines) {
    if (line.trim().startsWith("* ")) {
      if (!inList) {
        result.push("<ul>");
        inList = true;
      }
      result.push(`<li>${line.trim().substring(2)}</li>`);
    } else {
      if (inList) {
        result.push("</ul>");
        inList = false;
      }
      if (line.trim()) {
        result.push(`<p>${line}</p>`);
      }
    }
  }
  if (inList) result.push("</ul>");
  return result.join("\n");
}