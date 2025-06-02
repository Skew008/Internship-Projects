import React from 'react';

interface Task {
  id: string;
  title: string;
  dueDate: string;
  completed: boolean;
}

interface TaskCardProps {
  task: Task;
  onComplete: (id: string) => void;
}

const TaskCard: React.FC<TaskCardProps> = ({ task, onComplete }) => {
  const due = new Date(task.dueDate).toLocaleDateString();

  return (
    <div className="p-4 border rounded-lg flex justify-between items-center">
      <div>
        <h3 className="text-lg font-semibold">{task.title}</h3>
        <p className="text-sm text-gray-500">Due: {due}</p>
      </div>
      <button
        className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        onClick={() => onComplete(task.id)}
        disabled={task.completed}
      >
        {task.completed ? 'Completed' : 'Mark Complete'}
      </button>
    </div>
  );
};

export default TaskCard;
