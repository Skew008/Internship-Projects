import React from 'react';

interface AuthButtonProps {
  children: React.ReactNode;
  onClick?: () => void;
  type?: 'button' | 'submit' | 'reset';
  disabled?: boolean;
}

const AuthButton: React.FC<AuthButtonProps> = ({
  children,
  onClick,
  type = 'button',
  disabled = false,
}) => {
  return (
    <button
      type={type}
      className="w-full py-3 px-4 bg-gray-400 hover:bg-gray-500 text-white rounded transition-colors duration-200 font-medium cursor-pointer"
      onClick={onClick}
      disabled={disabled}
    >
      {children}
    </button>
  );
};

export default AuthButton;