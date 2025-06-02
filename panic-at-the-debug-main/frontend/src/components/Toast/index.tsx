import { useState, useEffect } from 'react';

interface ToastProps{
    message:string,
    status:"success"|"error",
    onClose:()=>void
}

const Toast:React.FC<ToastProps> = ({ message, status, onClose }) => {
  const [visible, setVisible] = useState(true);
  const [progress, setProgress] = useState(100);
  
  // Define colors based on status
  const borderColor = status === 'success' ? 'border-green-500' : 'border-red-500';
  const progressColor = status === 'success' ? 'bg-green-500' : 'bg-red-500';
  const iconColor = status === 'success' ? 'text-green-500' : 'text-red-500';
  
  const icon = status === 'success' ? (
    <svg className={`w-5 h-5 ${iconColor}`} fill="currentColor" viewBox="0 0 20 20">
      <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
    </svg>
  ) : (
    <svg className={`w-5 h-5 ${iconColor}`} fill="currentColor" viewBox="0 0 20 20">
      <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
    </svg>
  );

  useEffect(() => {
    // Animation for progress bar
    const animationDuration = 2000; // 2 seconds
    const intervalTime = 20; // Update every 20ms for smooth animation
    const decrementPerInterval = (intervalTime / animationDuration) * 100;
    
    const progressInterval = setInterval(() => {
      setProgress(prev => {
        if (prev <= 0) {
          clearInterval(progressInterval);
          return 0;
        }
        return prev - decrementPerInterval;
      });
    }, intervalTime);
    
    // Set timeout to hide the toast after 2 seconds
    const timer = setTimeout(() => {
      setVisible(false);
      // Give time for exit animation before calling onClose
      setTimeout(() => {
        if (onClose) onClose();
      }, 300);
    }, 2000);

    // Clean up timers on unmount
    return () => {
      clearTimeout(timer);
      clearInterval(progressInterval);
    };
  }, [onClose]);

  return (
    <div 
      className={`fixed top-4 right-4 flex flex-col p-0 mb-4 rounded-lg shadow-lg transition-all duration-300 overflow-hidden bg-white border ${borderColor} ${visible ? 'opacity-100 translate-y-0' : 'opacity-0 -translate-y-2'}`}
      style={{ width: '320px' }}
    >
      <div className="flex items-center p-4">
        <div className="inline-flex items-center justify-center flex-shrink-0 w-8 h-8 rounded-lg">
          {icon}
        </div>
        <div className="ml-3 text-sm font-normal text-gray-800">{message}</div>
      </div>
      
      {/* Progress bar */}
      <div 
        className={`h-1 ${progressColor} transition-all duration-100 ease-linear`} 
        style={{ width: `${progress}%` }}
      />
    </div>
  );
};

export default Toast;