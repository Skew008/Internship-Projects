import React, { useState } from "react";
import axiosInstance from "../../services/api";
import Button from "./Button";
import Toast from "../Toast"; // import Toast component

const FileUpload = ({ onFileSelect }: { onFileSelect: (file: File) => void }) => {
  const fileInputRef = React.useRef<HTMLInputElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (files && files[0]) {
      onFileSelect(files[0]);
    }
  };

  return (
    <>
      <input
        type="file"
        accept=".pdf"
        ref={fileInputRef}
        onChange={handleFileChange}
        className="hidden"
      />
      <Button variant="secondary" onClick={() => fileInputRef.current?.click()}>
        Upload Resume
      </Button>
    </>
  );
};

const Popup: React.FC<{ onClose: () => void }> = ({ onClose }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [toast, setToast] = useState<{
    message: string;
    status: "success" | "error";
  } | null>(null);

  const handleSurveyClick = () => {
    window.location.href = "/survey";
  };

  const handleFileUpload = async (file: File) => {
    try {
      setIsLoading(true);
      const formData = new FormData();
      formData.append("file", file);

      const response = await axiosInstance.post("/resume/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      console.log("Resume parsed successfully:", response.data);

      // Show success toast instead of alert
      setToast({
        message: "Resume uploaded and parsed successfully!",
        status: "success",
      });

      setIsLoading(false);

      // Redirect to survey after 2.3s (when toast disappears)
      setTimeout(() => {
        window.location.href = "/survey";
      }, 2300);
    } catch (error) {
      console.error("Resume upload failed:", error);
      setToast({
        message: "Failed to upload resume. Please try again.",
        status: "error",
      });
      setIsLoading(false);
    }
  };

  return (
    <>
      <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-10">
        <div className="bg-white rounded-lg p-6 shadow-lg max-w-md w-full">
          <h2 className="text-lg font-bold text-center mb-4">Choose an Action</h2>
          <div className="flex flex-col space-y-4">
            <Button variant="primary" onClick={handleSurveyClick}>
              Take the Survey
            </Button>
            <div className="flex items-center justify-center">
              <div className="border-t border-gray-300 w-full"></div>
              <span className="px-4 text-gray-500 text-sm">or</span>
              <div className="border-t border-gray-300 w-full"></div>
            </div>
            <FileUpload onFileSelect={handleFileUpload} />
          </div>
        </div>
      </div>

      {toast && (
        <Toast
          message={toast.message}
          status={toast.status}
          onClose={() => setToast(null)}
        />
      )}
    </>
  );
};

export default Popup;
