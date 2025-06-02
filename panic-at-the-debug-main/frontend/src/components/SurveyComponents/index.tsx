import { SurveyData, Question } from '../../types'; // Adjust the path as needed
import { Dropdown } from '../DropdownSurvey'; // Assuming Dropdown is a reusable component you have already created

/**
 * Step Component
 * Displays each step in the step indicator.
 */
export const Step = ({ 
  number, 
  isActive, 
  isCompleted 
}: { 
  number: number; 
  isActive: boolean; 
  isCompleted: boolean;
}) => {
  return (
    <div className="flex-1 flex justify-center">
      <div
        className={`flex items-center justify-center w-8 h-8 rounded-full ${
          isActive
            ? 'bg-indigo-600 text-white'
            : isCompleted
            ? 'bg-indigo-500 text-white'
            : 'bg-gray-200 text-gray-600'
        }`}
      >
        {number}
      </div>
    </div>
  );
};

/**
 * StepIndicator Component
 * Displays the progress indicator for survey steps.
 */
export const StepIndicator = ({ 
  currentStep, 
  totalSteps 
}: { 
  currentStep: number; 
  totalSteps: number;
}) => {
  return (
    <div className="relative flex items-center justify-between">
      {Array.from({ length: totalSteps }, (_, i) => i + 1).map((step) => (
        <Step 
          key={step} 
          number={step} 
          isActive={step === currentStep}
          isCompleted={step < currentStep} 
        />
      ))}
      <div className="absolute top-4 left-0 right-0 h-0.5 bg-gray-200 -z-10"></div>
    </div>
  );
};

/**
 * FormPage Component
 * Displays the first page for collecting target role and preferred language.
 */
export const FormPage = ({
  formData,
  onChange,
  onContinue,
  isValid,
  roles,
  languages,
}: {
  formData: SurveyData;
  onChange: (field: string, value: string) => void;
  onContinue: () => void;
  isValid: boolean|string;
  roles: string[];
  languages: string[];
}) => {
  return (
    <div>
      <h2 className="text-2xl font-medium mb-2">Answer the questions</h2>
      <p className="text-gray-500 mb-6">
        We collect this information to better understand you.
      </p>

      {/* Dropdown for selecting target role */}
      <Dropdown
        label="Target Role"
        value={formData.target}
        onChange={(value) => onChange('target', value)}
        options={roles.map((role) => ({ value: role, label: role }))}
        placeholder="Select your target role"
        className="mb-4"
      />

      {/* Dropdown for selecting preferred language */}
      <Dropdown
        label="Language"
        value={formData.language}
        onChange={(value) => onChange('language', value)}
        options={languages.map((language) => ({ value: language, label: language }))}
        placeholder="Select your preferred language"
      />

      {/* Continue button */}
      <button
        onClick={onContinue}
        disabled={!isValid}
        className={`w-full py-3 rounded-md transition-colors ${
          isValid
            ? 'bg-indigo-600 text-white hover:bg-indigo-700 cursor-pointer'
            : 'bg-gray-300 text-gray-500 cursor-not-allowed'
        }`}
      >
        Continue
      </button>
    </div>
  );
};

/**
 * DropdownQuestion Component
 * Displays each survey question with its dropdown options.
 */
export const DropdownQuestion = ({
  question,
  value,
  onChange,
}: {
  question: Question;
  value: string;
  onChange: (value: string) => void;
}) => {
  // Convert question options array to format needed by Dropdown component
  const options = question.options.map((option, index) => ({
    value: index.toString(), // Return index as value
    label: option,
  }));

  return (
    <Dropdown
      label={question.text}
      value={value || ''}
      onChange={onChange}
      options={options}
    />
  );
};

/**
 * QuestionPage Component
 * Displays the technical assessment page for survey questions.
 */
import { useNavigate } from 'react-router-dom';

export const QuestionPage = ({
  questions,
  answers,
  onChange,
  onContinue,
  isLoading,
  isValid,
}: {
  questions: Question[];
  answers: Record<string, string>;
  onChange: (questionId: string, value: string) => void;
  onContinue: () => void;
  isLoading: boolean;
  isValid: boolean | string;
}) => {
  const navigate = useNavigate(); // Initialize useNavigate hook for navigation

  // Function to handle click on "Submit" button
  const handleButtonClick = () => {
    // Trigger API in the background

    // Call onContinue to maintain the user flow
    onContinue();

    // Navigate to /dashboard
    setTimeout(() => {
      navigate('/dashboard');  
    }, 5000);
    
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  return (
    <div>
      <h2 className="text-2xl font-medium mb-6">Technical Assessment</h2>

      {questions.map((question) => (
        <DropdownQuestion
          key={question.id}
          question={question}
          value={answers[question.id] || ''}
          onChange={(value) => onChange(question.id, value)}
        />
      ))}

      {/* Continue button */}
      <button
        onClick={handleButtonClick}
        disabled={!isValid}
        className={`w-full py-3 rounded-md transition-colors ${
          isValid
            ? 'bg-indigo-600 text-white hover:bg-indigo-700 cursor-pointer'
            : 'bg-gray-300 text-gray-500 cursor-not-allowed'
        }`}
      >
        Continue
      </button>
    </div>
  );
};