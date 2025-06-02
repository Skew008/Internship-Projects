import React, { useState } from "react";

interface TextFieldProps {
  label: string;
  type?: string;
  placeholder?: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  helpText?: string;
  required?: boolean;
  error?: string; // Pass custom error
}

const TextField: React.FC<TextFieldProps> = ({
  label,
  type = "text",
  placeholder = "",
  value,
  onChange,
  helpText,
  required = false,
  error,
}) => {
  return (
    <div className="mb-6">
      <label className="block text-gray-700 mb-2">{label}</label>
      <div className="relative">
        <input
          type={type}
          className={`w-full p-3 border rounded focus:outline-none focus:ring-2 ${
            error
              ? "border-red-500 focus:ring-red-300" // Red border for errors
              : "border-gray-300 focus:ring-blue-300"
          }`}
          placeholder={placeholder}
          value={value}
          onChange={onChange}
          required={required}
        />
      </div>
      {helpText && <p className="mt-1 text-sm text-gray-600">{helpText}</p>}
      {error && <p className="mt-1 text-sm text-red-500">{error}</p>} {/* Error message */}
    </div>
  );
};

export default TextField;