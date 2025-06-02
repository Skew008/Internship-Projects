// import { AxiosRequestConfig } from 'axios';
import axiosInstance from './api';
 
/**
* API functions for learning path operations
*/
const roadMapApi = {
  /**
   * Fetch the user's custom learning path
   * @returns {Promise} Promise object representing the API response
   */
  fetchLearningPath: () => {
    console.log("Getting learn path");
    return axiosInstance.get('/learn-path/fetch');
  },
  /**
   * Create a custom learning path based on assessment results
   * @param {Object} questionOutcomes - The assessment results
   * @returns {Promise} Promise object representing the API response
   */
  createLearningPath: (questionResults) => {
    console.log(questionResults);
    return axiosInstance.post('/learn-path/create', questionResults );
  },
  /**
   * Update the completion status of a subtopic
   * @param {string} topic - The main topic name
   * @param {string} subTopic - The subtopic name
   * @param {string} completionStatus - The new completion status ('completed' or 'not started')
   * @returns {Promise} Promise object representing the API response
   */
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  updateCompletionStatus: (topic: any, subTopic: any, completionStatus: any) => {
    return axiosInstance.post('/learn-path/update-status', {
      topic,
      subTopic,
      completionStatus
    });
  },
  /**
   * Check if a user has a learning path
   * @returns {Promise} Promise object representing the API response
   */
  hasLearningPath: async () => {
    try {
      const response = await axiosInstance.get('/learn-path/fetch');
      return { exists: true, data: response.data };
    } catch (error) {
      if (error.response && error.response.status === 404) {
        return { exists: false };
      }
      throw error;
    }
  },
  /**
   * Get learning path statistics
   * @returns {Promise} Promise object representing the API response with statistics
   */
  getLearningPathStats: () => {
    return axiosInstance.get('/learn-path/stats');
  }
};
 
export default roadMapApi;