export interface ProgressEntry {
  topicName: string;
  subTopicName: string;
  estimatedCompletionDate: string;
  completionStatus: boolean;
  duration: string;
}

export interface DashboardMetrics {
  completionPercentage: number;
  completedSubTopics: number;
  totalSubTopics: number;
  estimatedCompletionDate: string;
}