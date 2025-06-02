// components/Wellbeing.tsx

import React from "react";
import Notification from "./Notification";
import { useWellbeingNotifications } from "./useWellbeingNotification";

const Wellbeing: React.FC = () => {
  const { notifications, acknowledge } = useWellbeingNotifications();

  return (
    <>
      {notifications.map((n) => (
        <Notification
          key={n.id}
          id={n.id}
          message={n.message}
          onAcknowledge={acknowledge}
        />
      ))}
    </>
  );
};

export default Wellbeing;
