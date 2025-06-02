import { Question, SurveyData } from '../types';
import api from './api'; // Updated Axios instance with JWT handling

// Fetch available roles
export const getAvailableRoles = async (): Promise<string[]> => {
  const response = await api.get('/survey/definitions'); // Authorization header added automatically
  return response.data;
};

// Fetch available languages for a specific role
export const getAvailableLanguages = async (role: string): Promise<string[]> => {
  const response = await api.get(`/survey/definitions/${role}/languages`);
  return response.data;
};

// Fetch survey questions for a specific role and language
export const getSurveyQuestions = async (role: string, language: string): Promise<Question[]> => {
  const response = await api.get(`/survey/definitions/${role}/${language}`);
  return response.data.questions; // Extract questions from the DTO response
};

// Submit survey responses
export const submitSurvey = async (formData: SurveyData): Promise<void> => {
  const data = {
    targetRole: formData.target,
    preferredLanguage: formData.language,
    skillResponses: Object.fromEntries(
      Object.entries(formData.answers).map(([questionId, value]) => [questionId, Number(value)])
    ),
  };

  await api.post('/survey', data); // Authorization header added automatically
};