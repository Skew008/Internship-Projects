import React from 'react';

interface ProgressBarProps {
  currentStep: number;
  totalSteps: number;
}

const ProgressBar: React.FC<ProgressBarProps> = ({ currentStep, totalSteps }) => {
  const steps = Array.from({ length: totalSteps }, (_, i) => i + 1);

  return (
    <div className="progress-bar">
      {steps.map((step) => (
        <div key={step} className="progress-step-container">
          <div className={`progress-step ${step === currentStep ? 'active' : ''} ${step < currentStep ? 'completed' : ''}`}>
            {step}
          </div>
          {step < totalSteps && (
            <div className={`progress-line ${step < currentStep ? 'completed' : ''}`}></div>
          )}
        </div>
      ))}
    </div>
  );
};

export default ProgressBar;