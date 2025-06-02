export type Question = {
  id: string; // Unique identifier for the question
  text: string; // The question text
  options: string[]; // Array of possible answer options
  correctOptionIndex?: number; // Optionally, index for the correct answer (used for admin or scoring later)
  explanation?: string; // Optional explanation for the correct option
  difficultyLevel?: string; // Difficulty level of the question (optional)
  skillCategory?: string; // Skill category associated with the question (optional)
};

export type SurveyData = {
  target: string; // Target role selected by the user
  language: string; // Preferred language selected by the user
  answers: Record<string, string>; // An object mapping question IDs to user's selected answers
};