// components/Notification.tsx

import React from "react";

type NotificationProps = {
  id: string;
  message: string;
  onAcknowledge: (id: string) => void;
};

const Notification: React.FC<NotificationProps> = ({ id, message, onAcknowledge }) => {
  return (
    <div className="fixed bottom-5 right-5 z-50 bg-blue-100 border-l-4 border-blue-500 text-blue-900 p-4 rounded shadow-md max-w-sm animate-fade-in">
      <div className="flex justify-between items-center">
        <p className="text-sm">{message}</p>
        <button
          onClick={() => onAcknowledge(id)}
          className="ml-4 bg-blue-500 text-white px-3 py-1 text-xs rounded hover:bg-blue-600"
        >
          Acknowledge
        </button>
      </div>
    </div>
  );
};

export default Notification;
