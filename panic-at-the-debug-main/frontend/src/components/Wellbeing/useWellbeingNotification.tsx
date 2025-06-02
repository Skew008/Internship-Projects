import { useEffect, useState } from "react";

type Notification = {
  id: string;
  message: string;
};

const API_BASE = "http://localhost:8080";
const HEARTBEAT_INTERVAL = 60000;

export function useWellbeingNotifications() {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [lastActivity, setLastActivity] = useState(Date.now());

  const activityEvents = [
    "mousedown",
    "mousemove",
    "keypress",
    "scroll",
    "touchstart",
    "click",
    "keydown",
  ];

  useEffect(() => {
    const recordActivity = () => setLastActivity(Date.now());
    activityEvents.forEach((event) =>
      document.addEventListener(event, recordActivity)
    );

    const heartbeat = () => {
      if (Date.now() - lastActivity < HEARTBEAT_INTERVAL) {
        sendHeartbeat();
      }
    };

    const intervalId = setInterval(heartbeat, HEARTBEAT_INTERVAL);
    sendHeartbeat(); // initial

    const visibilityHandler = () => {
      if (!document.hidden) {
        heartbeat();
      }
    };

    document.addEventListener("visibilitychange", visibilityHandler);

    const unloadHandler = () => {
      navigator.sendBeacon(
        `${API_BASE}/api/activity/end-session`,
        JSON.stringify({})
      );
    };

    window.addEventListener("beforeunload", unloadHandler);

    return () => {
      activityEvents.forEach((event) =>
        document.removeEventListener(event, recordActivity)
      );
      clearInterval(intervalId);
      window.removeEventListener("beforeunload", unloadHandler);
      document.removeEventListener("visibilitychange", visibilityHandler);
    };
  }, [lastActivity]);

  /**
   * Helper Function to Get JWT from localStorage
   */
  const getAuthHeaders = () => {
    const token = localStorage.getItem("accessToken");
    return {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`, // Attach the token to Authorization header
    };
  };

  const sendHeartbeat = async () => {
    try {
      const res = await fetch(`${API_BASE}/api/activity/heartbeat`, {
        method: "POST",
        headers: getAuthHeaders(), // Use the helper function to include JWT
        credentials: "include",
      });
      if (res.ok) {
        checkNotifications();
      }
    } catch (error) {
      console.error("Heartbeat error:", error);
    }
  };

  const checkNotifications = async () => {
    try {
      const res = await fetch(`${API_BASE}/api/notifications`, {
        headers: getAuthHeaders(), // Use the helper function to include JWT
        credentials: "include",
      });
      const data = await res.json();
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const newNotifications = data.filter((n: any) => !n.acknowledged);
      setNotifications(newNotifications);
    } catch (error) {
      console.error("Notification fetch error:", error);
    }
  };

  const acknowledge = async (id: string) => {
    try {
      await fetch(`${API_BASE}/api/notifications/${id}/acknowledge`, {
        method: "POST",
        headers: getAuthHeaders(), // Use the helper function to include JWT
        credentials: "include",
      });
      setNotifications((prev) => prev.filter((n) => n.id !== id));
    } catch (error) {
      console.error("Acknowledge error:", error);
    }
  };

  return { notifications, acknowledge };
}