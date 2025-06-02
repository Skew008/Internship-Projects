import React, { useState, useEffect } from "react";
import {
  generateAssessment,
  startAssessment,
  submitAssessment,
} from "../../services/assesmentApi";
import Navbar from "./Navbar";
import Toast from "../Toast";
import { Question } from "../../types";
import { useNavigate } from "react-router-dom";
import roadMapApi from "../../services/roadMapApi";

const AssessmentPage = () => {
  const [questions, setQuestions] = useState<Question[]>([]);
  const [answers, setAnswers] = useState<Record<string, string>>({});
  const [assessmentId, setAssessmentId] = useState("");
  const [timeRemaining, setTimeRemaining] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [hasStarted, setHasStarted] = useState(false);
  const navigate = useNavigate();

  const [toast, setToast] = useState<{
    message: string;
    status: "success" | "error" | null;
  }>({
    message: "",
    status: null,
  });

  // Fetch and initialize the assessment
  useEffect(() => {
    const fetchAssessment = async () => {
      try {
        const data = await generateAssessment();
        setAssessmentId(data.id);
        setQuestions(data.questions);
        setTimeRemaining(data.timeRemainingSeconds || 900);
      } catch (error) {
        setToast({
          message: "Failed to fetch assessment. Please try again later.",
          status: "error",
        });
        console.error("Error fetching assessment:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchAssessment();
  }, []);

  // Countdown timer
  useEffect(() => {
    let timer: NodeJS.Timeout;
    if (hasStarted && timeRemaining > 0) {
      timer = setInterval(() => setTimeRemaining((prev) => prev - 1), 1000);
    } else if (hasStarted && timeRemaining <= 0 && !isSubmitted) {
      handleSubmit();
    }
    return () => clearInterval(timer);
  }, [timeRemaining, hasStarted, isSubmitted]);

  const handleAnswerChange = (questionId: string, selectedOption: string) => {
    setAnswers((prev) => ({
      ...prev,
      [questionId]: selectedOption,
    }));
  };

  const handleStart = async () => {
    setIsLoading(true);
    try {
      const data = await startAssessment(assessmentId);
      setHasStarted(true);
      setQuestions(data.questions);
      setTimeRemaining(data.timeRemainingSeconds || 1500);
    } catch (error) {
      setToast({
        message: "Failed to start the assessment. Please try again later.",
        status: "error",
      });
      console.error("Error starting assessment:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = async () => {
  setIsLoading(true);
  try {
    const result = await submitAssessment(assessmentId);
    setIsSubmitted(true);
    setToast({
      message: "Assessment submitted successfully.",
      status: "success",
    });

    console.log(result);
    const questionResults = result.questionResults;

    // Call your internal roadmap API
    await roadMapApi.createLearningPath({ questionResults });

    // Get access token from local storage
    const token = localStorage.getItem("accessToken");

    if (!token) {
      throw new Error("No access token found in localStorage.");
    }

    // Make API call to /learn-path/start
    const response = await fetch("http://localhost:8080/learn-path/start", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      throw new Error("Failed to start learning path");
    }

    // Navigate to dashboard
    navigate("/dashboard");
  } catch (error) {
    setToast({
      message: "Failed to submit the assessment. Please try again later.",
      status: "error",
    });
    console.error("Error submitting assessment:", error);
  } finally {
    setIsLoading(false);
  }
};


  const isAssessmentValid =
    questions.length > 0 && questions.every((q) => answers[q.id]);

  return (
    <div className="relative min-h-screen flex flex-col">
      <Navbar timeRemaining={hasStarted ? timeRemaining : null} />

      <div className="flex-1 flex items-center justify-center">
        <div className="relative z-10 w-full max-w-2xl p-6 backdrop-blur-3xl rounded-lg shadow-lg mx-4">
          {toast.status && (
            <Toast
              message={toast.message}
              status={toast.status}
              onClose={() => setToast({ message: "", status: null })}
            />
          )}

          {isLoading ? (
            <div className="flex flex-col justify-center items-center py-12">
              <img
                src="https://media.giphy.com/media/Qrca6tBIdqXYXhnB4v/giphy.gif"
                alt="Loading..."
                className="h-48 w-auto"
              />
              <p className="mt-4 text-lg font-medium text-gray-600">
                Loading, Please wait
              </p>
            </div>
          ) : isSubmitted ? (
            <div className="text-center">
              <h2 className="text-2xl font-medium mb-6">Thank You!</h2>
              <p className="text-gray-800 mb-6">
                Your assessment was submitted successfully.
              </p>
              <p className="text-sm text-gray-600">
                Check your personalized learning path!
              </p>
            </div>
          ) : (
            <div>
              {!hasStarted ? (
                <div className="text-center">
                  <h2 className="text-2xl font-medium mb-6">Ready to Start?</h2>
                  <button
                    onClick={handleStart}
                    className="w-full py-3 mt-6 rounded-md bg-indigo-600 text-white hover:bg-indigo-700 transition-colors"
                  >
                    Start Assessment
                  </button>
                </div>
              ) : (
                <>
                  <h2 className="text-2xl font-medium mb-6">
                    Assessment Questions
                  </h2>
                  {questions.map((question) => (
                    <div key={question.id} className="mb-4">
                      <h3 className="text-lg mb-3 font-medium">
                        {question.text}
                      </h3>
                      <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
                        {question.options.map((option, index) => (
                          <label
                            key={index}
                            className="flex items-center gap-2 cursor-pointer hover:bg-gray-100 p-2 rounded-lg border"
                          >
                            <input
                              type="radio"
                              value={index.toString()}
                              checked={
                                answers[question.id] === index.toString()
                              }
                              onChange={() =>
                                handleAnswerChange(
                                  question.id,
                                  index.toString()
                                )
                              }
                              className="form-radio h-5 w-5 text-indigo-600"
                            />
                            <span>{option}</span>
                          </label>
                        ))}
                      </div>
                    </div>
                  ))}
                  <button
                    onClick={handleSubmit}
                    disabled={!isAssessmentValid}
                    className={`w-full py-3 mt-6 rounded-md transition-colors ${
                      isAssessmentValid
                        ? "bg-indigo-600 text-white hover:bg-indigo-700 cursor-pointer"
                        : "bg-gray-300 text-gray-500 cursor-not-allowed"
                    }`}
                  >
                    Submit Assessment
                  </button>
                </>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AssessmentPage;
