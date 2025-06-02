import { Calendar, CheckCircle2, Clock } from "lucide-react";
import { ProgressEntry } from "./types";

interface TaskItemProps {
  task: ProgressEntry;
  onMarkComplete: (topicName: string, subTopicName: string) => void;
}

const TaskItem = ({ task, onMarkComplete }: TaskItemProps) => {
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString("en-US", { 
      month: "short", 
      day: "numeric", 
      year: "numeric" 
    });
  };

  return (
    <div className="p-4 border-b border-gray-100 flex items-center justify-between">
      <div className="flex-1">
        <h4 className="font-medium text-gray-900">{task.subTopicName}</h4>
        <p className="text-sm text-gray-600">{task.topicName}</p>
        <div className="mt-1 flex items-center gap-4">
          <span className="flex items-center text-xs text-gray-500">
            <Clock size={14} className="mr-1" />
            {task.duration}
          </span>
          <span className="flex items-center text-xs text-gray-500">
            <Calendar size={14} className="mr-1" />
            Due {formatDate(task.estimatedCompletionDate)}
          </span>
        </div>
      </div>
      {!task.completionStatus && (
        <button 
          onClick={() => onMarkComplete(task.topicName, task.subTopicName)}
          className="px-3 py-1 bg-blue-50 text-blue-600 rounded-md hover:bg-blue-100 transition-colors text-sm flex items-center cursor-pointer"
        >
          <CheckCircle2 size={14} className="mr-1 cursor-pointer" />
          Mark Complete
        </button>
      )}
      {task.completionStatus && (
        <span className="px-3 py-1 bg-green-50 text-green-600 rounded-md text-sm flex items-center">
          <CheckCircle2 size={14} className="mr-1" />
          Completed
        </span>
      )}
    </div>
  );
};

export default TaskItem;