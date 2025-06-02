/* eslint-disable @typescript-eslint/no-explicit-any */
import axiosInstance from './api';
 
// Replace this with your actual token fetching logic
 
 
export async function generateAssessment() {
  try {
    const response = await axiosInstance.post('/assessments/generate');
    return response.data;
  } catch (error: any) {
    console.error('Error generating assessment:', error.response?.data || error.message);
    throw error;
  }
}
 
export async function startAssessment(assessmentId: string) {
  const response = await axiosInstance.post(`/assessments/${assessmentId}/start`);
  return response.data;
}
 
export async function submitAssessment(assessmentId: string) {
  const response = await axiosInstance.post(`/assessments/${assessmentId}/complete`);
  return response.data;
}