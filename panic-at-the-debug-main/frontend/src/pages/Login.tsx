/* eslint-disable @typescript-eslint/no-explicit-any */
import React, { useState } from "react";
import { useNavigate } from "react-router-dom"; // Import useNavigate for routing
import AuthBackground from "../components/AuthBackground";
import AuthButton from "../components/AuthButton";
import { Input } from "../components/Input";
import axiosInstance from "../services/api";
import Toast from "../components/Toast";
import Popup from "../components/Popup"; // Import Popup Component

const Login: React.FC = () => {
  const navigate = useNavigate(); // Initialize navigate hook
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  // Manage errors for input fields
  const [errors, setErrors] = useState<{ email?: string; password?: string }>({});
  const [formSubmitted, setFormSubmitted] = useState(false);
  const [toast, setToast] = useState<{ message: string; status: "success" | "error" | null }>({
    message: "",
    status: null,
  });

  // Popup state to trigger visibility based on surveyStatus
  const [isPopupVisible, setIsPopupVisible] = useState(false);

  // Handle form input changes
  const handleInputChange = (field: keyof typeof formData, value: string) => {
    setFormData({ ...formData, [field]: value });
    setErrors({ ...errors, [field]: undefined }); // Clear individual field error as user updates it
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Clear previous errors and mark form as submitted
    setErrors({});
    setFormSubmitted(true);

    try {
      // Submit login request to the API
      const response = await axiosInstance.post("/auth/login", {
        email: formData.email,
        password: formData.password,
      });
      const { accessToken, assessmentStatus, surveyStatus } = response.data;

      console.log("Response from server:", response.data);

      // Store tokens and statuses in localStorage
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("assessmentStatus", assessmentStatus); // Fix incorrect key
      localStorage.setItem("surveyStatus", surveyStatus);

      // Show success toast
      setToast({ message: "Logged in successfully.", status: "success" });

      // Check surveyStatus
      if (!(surveyStatus === "COMPLETE")) {
        setIsPopupVisible(true); // Show popup if surveyStatus is false
      } else {
        // If survey is already completed, redirect to /home
        navigate("/home");
      }
    } catch (error: any) {
      // Handle errors (e.g., invalid credentials)
      console.error("Login failed:", error);

      const errorMessage = error.response?.data?.message || "An unexpected error occurred.";
      setErrors({
        email: errorMessage,
        password: errorMessage,
      });

      // Show error toast
      setToast({ message: errorMessage, status: "error" });
    }
  };

  // Redirect to /home automatically when the popup is closed
  const handlePopupClose = () => {
    setIsPopupVisible(false);
    navigate("/home"); // Redirect to /home
  };

  return (
    <div className="flex min-h-screen">
      {/* Left side for Background */}
      <div className="hidden md:block md:w-1/2">
        <AuthBackground />
      </div>

      {/* Right side for Login Form */}
      <div className="w-full md:w-1/2 flex items-center justify-center p-8">
        <div className="w-full max-w-md">
          {/* Toast Notification */}
          {toast.status && (
            <Toast
              message={toast.message}
              status={toast.status}
              onClose={() => setToast({ message: "", status: null })}
            />
          )}

          {/* Logo Placeholder */}
          <div className="flex justify-end mb-4">
            <div className="w-8 h-8 bg-gray-300 rounded-full"></div>
          </div>

          {/* Login Title */}
          <h1 className="text-3xl font-bold mb-4">Log in</h1>

          {/* Login Form */}
          <form onSubmit={handleSubmit}>
            {/* Email Input Field */}
            <Input
              name="email"
              label="Email Address"
              type="email"
              placeholder="Enter your email"
              helperText="Use your registered email"
              value={formData.email}
              onChange={(e) => handleInputChange("email", e.target.value)}
              error={formSubmitted && !!errors.email} // Highlight error if present
              errorText={errors.email}
              required
            />

            {/* Password Input Field */}
            <Input
              name="password"
              label="Password"
              type="password"
              placeholder="Enter your password"
              helperText="Your secret password"
              value={formData.password}
              onChange={(e) => handleInputChange("password", e.target.value)}
              error={formSubmitted && !!errors.password} // Highlight error if present
              errorText={errors.password}
              showPasswordToggle={true} // Password visibility toggle
              required
            />

            {/* Signup Redirect */}
            <p className="mb-8">
              Don't have an account?{" "}
              <a href="/signup" className="text-blue-600 hover:underline">
                Create an account
              </a>
            </p>

            {/* Submit Button */}
            <div className="mt-10">
              <AuthButton type="submit">Log in</AuthButton>
            </div>
          </form>
        </div>
      </div>

      {/* Popup Component displayed based on surveyStatus */}
      {isPopupVisible && (
        <Popup
          onClose={handlePopupClose} // Call handlePopupClose to close and redirect
        />
      )}
    </div>
  );
};

export default Login;