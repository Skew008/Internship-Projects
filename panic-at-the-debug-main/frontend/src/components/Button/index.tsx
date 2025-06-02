import React from 'react';

interface ButtonProps {
  children: React.ReactNode;
  primary?: boolean;
  className?: string;
  onClick?: () => void;
  disabled?: boolean; // Add disabled prop
}

const Button: React.FC<ButtonProps> = ({ 
  children, 
  primary = false, 
  className = '', 
  onClick, 
  disabled = false // Default disabled to false if not provided
}) => {
  return (
    <button
      onClick={!disabled ? onClick : undefined} // Prevent click when disabled
      disabled={disabled} // Use button's built-in disabled attribute
      className={`
        px-4 py-2 rounded transition-colors duration-200 cursor-pointer
        ${disabled 
          ? 'cursor-not-allowed bg-gray-300 text-gray-500' // Disabled styles
          : primary 
            ? 'bg-blue-500 text-white hover:bg-blue-600' // Primary styles
            : 'bg-white text-blue-500 border border-blue-500 hover:bg-blue-50' // Secondary (default) styles
        }
        ${className}
      `}
    >
      {children}
    </button>
  );
};

export default Button;