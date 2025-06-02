/* eslint-disable @typescript-eslint/no-explicit-any */
import React, { useState } from "react";
import AuthBackground from "../components/AuthBackground";
import AuthButton from "../components/AuthButton";
import axiosInstance from "../services/api";
import { signupSchema } from "../schema/SignupSchema";
import { useZodForm } from "../hooks/useZodForm";
import { Input } from "../components/Input";
import Toast from "../components/Toast"; // Toast Component

const Signup: React.FC = () => {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
    target: "",
    activity: "",
  });

  const {
    errors, // Form validation errors handled by Zod
    validate, // Validation function
    clearError, // Clear specific field error
  } = useZodForm(signupSchema);

  const [formSubmitted, setFormSubmitted] = useState(false); // Tracks form submission state
  const [toast, setToast] = useState<{ message: string; status: 'success' | 'error' | null }>({
    message: '',
    status: null,
  }); // Tracks toast messages and status

  const handleInputChange = (field: keyof typeof formData, value: string) => {
    setFormData({ ...formData, [field]: value });
    clearError(field); // Clear error as user modifies input
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    setFormSubmitted(true); // Flag form as submitted for error highlighting

    // Validate form using Zod schema
    if (!validate(formData)) return;

    try {
      const response = await axiosInstance.post("/auth/register", formData);
      console.log("Registration successful:", response.data);

      // Show success toast
      setToast({ message: "Registration successful. Redirecting to login...", status: "success" });

      // Redirect after a short delay for the success toast to show
      setTimeout(() => {
        window.location.href = "/login";
      }, 2000);
    } catch (error: any) {
      console.error("Signup failed:", error);

      // Show error toast
      setToast({ message: error.response?.data?.message || "An error occurred during signup.", status: "error" });
    }
  };

  return (
    <div className="flex min-h-screen">
      {/* Left side - colorful background */}
      <div className="hidden md:block md:w-1/2">
        <AuthBackground />
      </div>

      {/* Right side - Toasts and Signup form */}
      <div className="w-full md:w-1/2 flex items-center justify-center p-8">
        <div className="w-full max-w-md">
          {/* Toast Component */}
          {toast.status && (
            <Toast
              message={toast.message}
              status={toast.status}
              onClose={() => setToast({ message: '', status: null })}
            />
          )}

          <h1 className="text-3xl font-bold mb-2">Create an account</h1>

          <form onSubmit={handleSubmit}>
            <div className="grid grid-cols-2 gap-4">
              <Input
                name="firstName"
                label="First Name"
                placeholder="Enter your First Name"
                helperText="e.g. Jonson"
                value={formData.firstName}
                onChange={(e) => handleInputChange("firstName", e.target.value)}
                error={formSubmitted && !!errors.firstName}
                errorText={errors.firstName}
              />
              <Input
                name="lastName"
                label="Last Name"
                placeholder="Enter your Last Name"
                helperText="e.g. Doe"
                value={formData.lastName}
                onChange={(e) => handleInputChange("lastName", e.target.value)}
                error={formSubmitted && !!errors.lastName}
                errorText={errors.lastName}
              />
            </div>

            <Input
              name="email"
              label="Email Address"
              placeholder="Enter your Email"
              helperText="Use a valid email address"
              type="email"
              value={formData.email}
              onChange={(e) => handleInputChange("email", e.target.value)}
              error={formSubmitted && !!errors.email}
              errorText={errors.email}
            />

            <Input
              name="password"
              label="Password"
              placeholder="Enter your Password"
              helperText="Use 8 or more characters with a mix of letters, numbers & symbols"
              type="password"
              value={formData.password}
              onChange={(e) => handleInputChange("password", e.target.value)}
              error={formSubmitted && !!errors.password}
              errorText={errors.password}
              showPasswordToggle={true} // Allow password toggle functionality
            />

            <Input
              name="confirmPassword"
              label="Confirm Password"
              placeholder="Confirm your Password"
              type="password"
              value={formData.confirmPassword}
              onChange={(e) =>
                handleInputChange("confirmPassword", e.target.value)
              }
              error={formSubmitted && !!errors.confirmPassword}
              errorText={errors.confirmPassword}
              showPasswordToggle={true}
            />

            <AuthButton type="submit">Create an account</AuthButton>
          </form>

          <div className="mt-6 text-center">
            <p>
              Already have an account?{" "}
              <a href="/login" className="text-blue-600 hover:underline">
                Log in
              </a>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Signup;