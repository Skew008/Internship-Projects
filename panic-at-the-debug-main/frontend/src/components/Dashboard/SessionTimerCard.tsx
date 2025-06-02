import React, { useEffect, useState } from "react";
import { BarChart3 } from "lucide-react";
import Card from "./Card"; // Adjust path if needed

// Utility to format seconds into HH:MM:SS
const formatTime = (seconds: number) => {
  const h = String(Math.floor(seconds / 3600)).padStart(2, "0");
  const m = String(Math.floor((seconds % 3600) / 60)).padStart(2, "0");
  const s = String(seconds % 60).padStart(2, "0");
  return `${h}:${m}:${s}`;
};

const SessionTimerCard: React.FC = () => {
  const [elapsedTime, setElapsedTime] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setElapsedTime((prev) => prev + 1);
    }, 1000);
    return () => clearInterval(interval); // Clean up on unmount
  }, []);

  return (
    <Card>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-gray-900">
          Session Time: {formatTime(elapsedTime)}
        </h2>
        <BarChart3 size={20} className="text-gray-500" />
      </div>
    </Card>
  );
};

export default SessionTimerCard;
