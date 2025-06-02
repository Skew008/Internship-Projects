import { Question } from '../../types';
import { Dropdown } from '../DropdownSurvey'; // Reusable dropdown component

/**
 * StepIndicator Component
 * Displays the progress indicator for assessments.
 */
export const StepIndicator = ({ currentStep, totalSteps }: { currentStep: number; totalSteps: number }) => {
  return (
    <div className="relative flex items-center justify-between">
      {Array.from({ length: totalSteps }, (_, i) => i + 1).map((step) => (
        <div key={step} className="flex-1 flex justify-center">
          <div
            className={`flex items-center justify-center w-8 h-8 rounded-full ${
              step === currentStep
                ? 'bg-indigo-600 text-white'
                : 'bg-gray-200 text-gray-600'
            }`}
          >
            {step}
          </div>
        </div>
      ))}
      <div className="absolute top-4 left-0 right-0 h-0.5 bg-gray-200 -z-10"></div>
    </div>
  );
};

/**
 * QuestionPage Component
 * Displays the questions and collects user's answers.
 */
export const QuestionPage = ({
  questions,
  answers,
  onChange,
  isValid,
  onContinue,
}: {
  questions: Question[];
  answers: Record<string, string>;
  onChange: (questionId: string, value: string) => void;
  isValid: boolean | string;
  onContinue: () => void;
}) => {
  return (
    <div>
      <h2 className="text-2xl font-medium mb-6">Assessment Questions</h2>

      {questions.map((question) => (
        <div key={question.id} className="mb-4">
          <Dropdown
            label={question.text}
            value={answers[question.id] || ''}
            onChange={(value) => onChange(question.id, value)}
            options={question.options.map((option, index) => ({
              value: index.toString(),
              label: option,
            }))}
          />
        </div>
      ))}

      {/* Submit button */}
      <button
        onClick={onContinue}
        disabled={!isValid}
        className={`w-full py-3 rounded-md transition-colors ${
          isValid
            ? 'bg-indigo-600 text-white hover:bg-indigo-700 cursor-pointer'
            : 'bg-gray-300 text-gray-500 cursor-not-allowed'
        }`}
      >
        Submit
      </button>
    </div>
  );
};