import { useState, useEffect } from "react";
import { DashboardMetrics, ProgressEntry } from "./types";

const useDashboardData = () => {
  const [dashboardMetrics, setDashboardMetrics] = useState<DashboardMetrics | null>(null);
  const [upcomingTasks, setUpcomingTasks] = useState<ProgressEntry[]>([]);
  const [completedTasks, setCompletedTasks] = useState<ProgressEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const[MajorSkills,setMajorSkills]=useState<[]>([]);
  const[DashboardStart,setDashboardStart]=useState(null);

  const token = localStorage.getItem("accessToken");
  const headers = {
    Authorization: `Bearer ${token}`,
    "Content-Type": "application/json",
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        
        // Fetch dashboard metrics

        const dashStart = await fetch("http://localhost:8080/learn-path/start", { headers });
        const  dashBoardStart= await dashStart.json();

        const metricsResponse = await fetch("http://localhost:8080/user-progress/dashboard", { headers });
        const metricsData = await metricsResponse.json();
        
        // Fetch upcoming tasks
        const upcomingResponse = await fetch("http://localhost:8080/user-progress/dashboard/upcoming-due-dates", { headers });
        const upcomingData = await upcomingResponse.json();
        
        // Fetch completed tasks
        const completedResponse = await fetch("http://localhost:8080/user-progress/completed-tasks", { headers });
        const completedData = await completedResponse.json();

        

        const skills = await fetch("http://localhost:8080/user-progress/dashboard/major-skills", { headers });
        const MainSkills = await skills.json();

        console.log("Major skills",MajorSkills);
        console.log(completedData);
        
        // Update state with fetched data
        setDashboardMetrics(metricsData);
        setUpcomingTasks(upcomingData);
        setCompletedTasks(completedData);
        setMajorSkills(MainSkills);
        setDashboardStart(dashBoardStart)
        
      } catch (err) {
        setError("Failed to load dashboard data. Please try again later.");
        console.error("Error fetching dashboard data:", err);
      } finally {
        setLoading(false);
      }
    };
    
    fetchData();
  }, []);

  const handleMarkComplete = async (topicName: string, subTopicName: string) => {
    const token = localStorage.getItem("accessToken");
    const headers = {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    };
    
    try {
      const response = await fetch("http://localhost:8080/user-progress/mark-completed", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
          "Authorization": `Bearer ${token}`,
        },
        body: `topicName=${encodeURIComponent(topicName)}&subTopicName=${encodeURIComponent(subTopicName)}`,
      });
      
      if (response.ok) {
        // Update local state to reflect the change
        const updatedUpcomingTasks = upcomingTasks.filter(
          task => !(task.topicName === topicName && task.subTopicName === subTopicName)
        );
        
        const completedTask = upcomingTasks.find(
          task => task.topicName === topicName && task.subTopicName === subTopicName
        );
        
        if (completedTask) {
          const updatedTask = { ...completedTask, completionStatus: true };
          setCompletedTasks([...completedTasks, updatedTask]);
        }
        
        setUpcomingTasks(updatedUpcomingTasks);
        
        // Refresh dashboard metrics
        const metricsResponse = await fetch("http://localhost:8080/user-progress/dashboard", { headers });
        const metricsData = await metricsResponse.json();
        setDashboardMetrics(metricsData);
      }
    } catch (err) {
      setError("Failed to mark task as complete. Please try again.");
      console.error("Error marking task as complete:", err);
    }
  };

  return {
    dashboardMetrics,
    upcomingTasks,
    completedTasks,
    loading,
    error,
    handleMarkComplete,
    MajorSkills,
    DashboardStart
  };
};

export default useDashboardData;