import React from 'react';

interface Task {
  id: string;
  title: string;
  dueDate: string;
  completed: boolean;
}

interface CompletedTasksProps {
  tasks: Task[];
}

const CompletedTasks: React.FC<CompletedTasksProps> = ({ tasks }) => {
  return (
    <section>
      <h2 className="text-2xl font-semibold mb-4">Completed Tasks</h2>
      {tasks.length === 0 ? (
        <p className="text-gray-500">No tasks completed yet.</p>
      ) : (
        <ul className="space-y-2">
          {tasks.map(task => (
            <li key={task.id} className="p-4 bg-green-100 rounded">
              <div className="flex justify-between">
                <span>{task.title}</span>
                <span className="text-sm text-gray-500">Completed</span>
              </div>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
};

export default CompletedTasks;