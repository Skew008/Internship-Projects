import { useState, useEffect } from 'react';
import { getAvailableRoles, getAvailableLanguages, getSurveyQuestions, submitSurvey } from '../services/surveyApi';
import { StepIndicator, FormPage, QuestionPage } from '../components/SurveyComponents';
import { SurveyData, Question } from '../types';
import AuthBackground from '../components/AuthBackground';
import Toast from '../components/Toast';

const Survey = () => {
  const [currentStep, setCurrentStep] = useState(1);
  const [roles, setRoles] = useState<string[]>([]);
  const [languages, setLanguages] = useState<string[]>([]);
  const [formData, setFormData] = useState<SurveyData>({
    target: '',
    language: '',
    answers: {},
  });
  const [questions, setQuestions] = useState<Question[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [surveySubmitted, setSurveySubmitted] = useState(false);

  // Toast state
  const [toast, setToast] = useState<{ message: string; status: 'success' | 'error' | null }>({
    message: '',
    status: null,
  });

  // Fetch available roles when the component mounts
  useEffect(() => {
    getAvailableRoles()
      .then((data) => setRoles(data))
      .catch((error) => {
        console.error('Error fetching roles:', error);
        setToast({ message: 'Failed to fetch roles. Please try again later.', status: 'error' });
      });
  }, []);

  // Fetch available languages when the target role changes
  useEffect(() => {
    if (formData.target) {
      getAvailableLanguages(formData.target)
        .then((data) => setLanguages(data))
        .catch((error) => {
          console.error('Error fetching languages:', error);
          setToast({ message: 'Failed to fetch languages. Please try again later.', status: 'error' });
        });
    }
  }, [formData.target]);

  // Fetch survey questions when moving to step 2
  useEffect(() => {
    if (currentStep === 2 && formData.target && formData.language) {
      setIsLoading(true);
      getSurveyQuestions(formData.target, formData.language)
        .then((data) => setQuestions(data))
        .catch((error) => {
          console.error('Error fetching questions:', error);
          setToast({ message: 'Failed to fetch questions. Please try again later.', status: 'error' });
        })
        .finally(() => setIsLoading(false));
    }
  }, [currentStep, formData.target, formData.language]);

  const handleFormChange = (field: string, value: string) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleAnswerChange = (questionId: string, value: string) => {
    setFormData((prev) => ({
      ...prev,
      answers: {
        ...prev.answers,
        [questionId]: value,
      },
    }));
  };

  const handleContinue = () => {
    if (currentStep === 1) {
      setCurrentStep(2);
    } else if (currentStep === 2) {
      // Submit survey responses
      submitSurvey(formData)
        .then(() => {
          setSurveySubmitted(true);
          setToast({ message: 'Survey completed! Thank you for your responses.', status: 'success' });
        })
        .catch((error) => {
          console.error('Error submitting survey:', error);
          setToast({ message: 'Failed to submit survey. Please try again later.', status: 'error' });
        });
    }
  };

  const handleBack = () => {
    if (currentStep > 1) {
      setCurrentStep(1);
    }
  };

  const isFormPage1Valid = formData.target && formData.language;
  const isFormPage2Valid = questions.length > 0 && questions.every((q) => formData.answers[q.id]);

  return (
    <div className="relative min-h-screen flex justify-center items-center">
      <div className="absolute inset-0">
        <AuthBackground />
      </div>
      {/* Toast Component */}
      {toast.status && (
        <Toast
          message={toast.message}
          status={toast.status}
          onClose={() => setToast({ message: '', status: null })}
        />
      )}

      <div className="relative z-10 w-full max-w-2xl p-6 brightness-115 backdrop-blur-3xl rounded-lg shadow-lg mx-4">
        <div className="mb-6">
          <button
            onClick={handleBack}
            className={`flex items-center ${currentStep === 1 ? 'text-gray-400' : 'text-gray-800'}`}
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-5 w-5 mr-1"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
            {currentStep > 1 ? 'Back' : ''}
          </button>
        </div>

        <StepIndicator currentStep={currentStep} totalSteps={2} />

        <div className="mt-8">
          {currentStep === 1 && (
            <FormPage
              formData={formData}
              onChange={handleFormChange}
              onContinue={handleContinue}
              isValid={isFormPage1Valid}
              roles={roles}
              languages={languages}
            />
          )}

          {currentStep === 2 && !surveySubmitted && (
            <QuestionPage
              questions={questions}
              answers={formData.answers}
              onChange={handleAnswerChange}
              onContinue={handleContinue}
              isLoading={isLoading}
              isValid={isFormPage2Valid}
            />
          )}

          {currentStep === 2 && surveySubmitted && (
            <div className="text-center">
              <h2 className="text-2xl font-medium mb-6">Thank You!</h2>
              <p className="text-gray-800 mb-6">Your responses have been submitted successfully.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Survey;