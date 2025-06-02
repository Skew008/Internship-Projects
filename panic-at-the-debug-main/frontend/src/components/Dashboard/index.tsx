import { useState } from "react";
import {
  Calendar,
  CheckCircle2,
  Hourglass,
  BookOpen,
  CircleCheck,
} from "lucide-react";

// Import components
import Card from "./Card";
import StatDisplay from "./StatDisplay";
import TaskItem from "./TaskItem";
import ProgressBar from "./ProgressBar";

// Import custom hook for data fetching
import useDashboardData from "./useDashboardData";
import Navbar from "../Navbar";
import SessionTimerCard from "./SessionTimerCard";

export default function Dashboard() {
  const [activeTab, setActiveTab] = useState("upcoming");


  const {
    dashboardMetrics,
    upcomingTasks,
    completedTasks,
    loading,
    error,
    handleMarkComplete,
    MajorSkills,
  } = useDashboardData();

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <Hourglass className="w-12 h-12 mx-auto text-blue-500 animate-pulse" />
          <p className="mt-4 text-gray-600">Loading your dashboard...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center max-w-md p-6 bg-red-50 rounded-lg">
          <p className="text-red-600">{error}</p>
          <button
            className="mt-4 px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
            onClick={() => window.location.reload()}
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  // Format date for display
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString("en-US", {
      month: "long",
      day: "numeric",
      year: "numeric",
    });
  };

  return (
    <div>
    <Navbar isLoggedIn />
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Profile header */}
        <div className="flex flex-col md:flex-row justify-between items-start mb-8">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">
              Learning Dashboard
            </h1>
            <p className="text-gray-600">
              Track your learning progress and upcoming tasks
            </p>
          </div>
          {/* <Link to="/roadmap">
            <div className="mt-4 md:mt-0">
              <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                Developer Learning Path
              </span>
            </div>
          </Link> */}
        </div>

        {/* Overview cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card className="col-span-full md:col-span-2 ">
            <div className="flex flex-col md:flex-row justify-between ">
              <div>
                <h2 className="text-lg font-semibold text-gray-900 mb-2">
                  Overall Progress
                </h2>
                <div className="flex items-end gap-2 mb-4">
                  <span className="text-3xl font-bold text-blue-600">
                    {dashboardMetrics?.completionPercentage}%
                  </span>
                  <span className="text-gray-500 text-sm mb-1">Completed</span>
                </div>
                <div className="mb-6">
                  <ProgressBar
                    percentage={dashboardMetrics?.completionPercentage || 0}
                  />
                </div>
                <div className="flex gap-4 flex-wrap">
                  <div className="flex items-center">
                    <div className="w-3 h-3 bg-blue-600 rounded-full mr-2"></div>
                    <span className="text-sm text-gray-600">
                      {dashboardMetrics?.completedSubTopics} of{" "}
                      {dashboardMetrics?.totalSubTopics} subtopics completed
                    </span>
                  </div>
                  <div className="flex items-center">
                    <Calendar size={16} className="text-gray-500 mr-2" />
                    <span className="text-sm text-gray-600">
                      Est. completion:{" "}
                      {formatDate(
                        dashboardMetrics?.estimatedCompletionDate || ""
                      )}
                    </span>
                  </div>
                </div>
                
<div>
{MajorSkills && MajorSkills.length > 0 && (
        <div className="space-y-2">
          <div className="flex flex-wrap mt-4">
            {MajorSkills.map((skill, index) => (
              <div
                key={index}
                className="inline-block bg-blue-100 text-blue-700 border border-blue-700 px-2 py-1 text-sm rounded-md m-1"
              >
                {skill}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
              </div>
              
            </div>
          </Card>
          <div className="space-y-6">
            <StatDisplay
              label="Total Sub-topics"
              value={dashboardMetrics?.totalSubTopics || 0}
              icon={<BookOpen size={20} className="text-blue-600" />}
            />
            <StatDisplay
              label="Completed Sub-topics"
              value={dashboardMetrics?.completedSubTopics || 0}
              icon={<CheckCircle2 size={20} className="text-green-600" />}
            />
            <StatDisplay
              label="Estimated Completion"
              value={formatDate(
                dashboardMetrics?.estimatedCompletionDate || ""
              )}
              icon={<Calendar size={20} className="text-purple-600" />}
            />
          </div>
        </div>

        {/* Task lists */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <Card className="lg:col-span-2">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-gray-900">Tasks</h2>
              <div className="flex border border-gray-200 rounded-lg overflow-hidden">
                <button
                  className={`px-4 py-2 text-sm cursor-pointer ${
                    activeTab === "upcoming"
                      ? "bg-blue-50 text-blue-600"
                      : "bg-white text-gray-600"
                  }`}
                  onClick={() => setActiveTab("upcoming")}
                >
                  Upcoming
                </button>
                <button
                  className={`px-4 py-2 text-sm cursor-pointer ${
                    activeTab === "completed"
                      ? "bg-blue-50 text-blue-600"
                      : "bg-white text-gray-600"
                  }`}
                  onClick={() => setActiveTab("completed")}
                >
                  Completed
                </button>
              </div>
            </div>

            <div className="overflow-hidden border border-gray-200 rounded-lg">
              {activeTab === "upcoming" && (
                <>
                  {upcomingTasks.length > 0 ? (
                    <div>
                      {upcomingTasks.map((task) => (
                        <TaskItem
                          key={`${task.topicName}-${task.subTopicName}`}
                          task={task}
                          onMarkComplete={handleMarkComplete}
                        />
                      ))}
                    </div>
                  ) : (
                    <div className="p-8 text-center">
                      <CircleCheck
                        size={40}
                        className="mx-auto text-gray-400 mb-2"
                      />
                      <p className="text-gray-600">
                        All caught up! No upcoming tasks.
                      </p>
                    </div>
                  )}
                </>
              )}

              {activeTab === "completed" && (
                <>
                  {completedTasks.length > 0 ? (
                    <div>
                      {completedTasks.map((task) => (
                        <TaskItem
                          key={`${task.topicName}-${task.subTopicName}`}
                          task={task}
                          onMarkComplete={handleMarkComplete}
                        />
                      ))}
                    </div>
                  ) : (
                    <div className="p-8 text-center">
                      <Hourglass
                        size={40}
                        className="mx-auto text-gray-400 mb-2"
                      />
                      <p className="text-gray-600">No completed tasks yet.</p>
                    </div>
                  )}
                </>
              )}
            </div>
          </Card>

          <div className="space-y-6">
            {/* <Card>
              <h2 className="text-lg font-semibold text-gray-900 mb-4">
                Learning Streak
              </h2>
              <div className="flex items-center justify-between">
                <div className="text-center">
                  <p className="text-3xl font-bold text-blue-600">3</p>
                  <p className="text-sm text-gray-600">Days</p>
                </div>
                <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center">
                  <Award size={28} className="text-blue-600" />
                </div>
              </div>
              <div className="mt-4 pt-4 border-t border-gray-100">
                <div className="flex justify-between">
                  {[1, 2, 3, 4, 5, 6, 7].map((day) => (
                    <div
                      key={day}
                      className={`w-8 h-8 rounded-full flex items-center justify-center 
                      ${
                        day <= 3
                          ? "bg-blue-600 text-white"
                          : "bg-gray-200 text-gray-500"
                      }`}
                    >
                      {day}
                    </div>
                  ))}
                </div>
              </div>
            </Card> */}

            {/* <Card>
              <h2 className="text-lg font-semibold text-gray-900 mb-4">
                Skill Progress
              </h2>
              <div className="space-y-2">
                {MajorSkills.map((skill, index) => (
                  <div
                    key={index} // unique key for each skill item
                    className="inline-block bg-blue-100 text-blue-700 border border-blue-700 px-2 py-1 text-sm rounded-md m-1"
                  >
                    {skill}
                  </div>
                ))}
              </div>
            </Card> */}

            {/* <Card>
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-lg font-semibold text-gray-900">
                  Weekly Activity
                </h2>
                <BarChart3 size={20} className="text-gray-500" />
              </div>
              <div className="flex justify-between items-end h-32">
                {["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"].map(
                  (day, index) => (
                    <div key={day} className="flex flex-col items-center">
                      <div
                        className={`w-8 ${
                          index === 2
                            ? "h-24 bg-blue-600"
                            : index === 1
                            ? "h-16 bg-blue-400"
                            : index === 0
                            ? "h-10 bg-blue-300"
                            : "h-4 bg-gray-200"
                        } rounded-t-md`}
                      ></div>
                      <span className="text-xs text-gray-500 mt-1">{day}</span>
                    </div>
                  )
                )}
              </div>
            </Card> */}
            <SessionTimerCard />
          </div>
        </div>
      </div>
    </div>
    </div>
  );
}
