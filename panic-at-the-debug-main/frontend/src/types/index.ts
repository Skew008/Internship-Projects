export interface SurveyData {
  target: string;
  language: string;
  answers: Record<string, string>;
}

export interface Question {
  id: string;
  text: string;
  options: string[];
  correctOption?: string;
}