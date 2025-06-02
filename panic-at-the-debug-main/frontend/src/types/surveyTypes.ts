export type TargetOption = 'backend' | 'frontend' | 'test';
export type LanguageOption = 'java' | 'javascript';

export interface SurveyFormData {
  target: TargetOption | '';
  language: LanguageOption | '';
  answers: Record<string, string>;
}

export interface Question {
  id: string;
  text: string;
  options: string[];
}

// Mock data for questions based on target and language
export const getQuestions = (target: TargetOption, language: LanguageOption): Question[] => {
  if (target === 'backend' && language === 'java') {
    return [
      {
        id: 'java_syntax',
        text: 'How comfortable are you with Java syntax and core concepts?',
        options: [
          "I'm new to Java or have limited knowledge",
          "I understand basic syntax and can write simple programs",
          "I'm comfortable with most Java features including OOP concepts",
          "I have deep knowledge of Java including advanced features"
        ]
      },
      {
        id: 'spring_framework',
        text: 'What is your experience level with Spring Framework?',
        options: [
          "Never used it before",
          "Basic understanding of Spring Core",
          "Comfortable with Spring Boot and common modules",
          "Advanced knowledge including Spring Cloud and microservices"
        ]
      },
      {
        id: 'java_persistence',
        text: 'How would you rate your knowledge of Java persistence (JDBC, JPA, Hibernate)?',
        options: [
          "Limited or no experience",
          "Basic understanding of database connectivity",
          "Comfortable with ORM concepts and implementation",
          "Advanced knowledge including performance optimization"
        ]
      },
      {
        id: 'multithreading',
        text: 'What is your experience with Java concurrency and multithreading?',
        options: [
          "Limited understanding of threads",
          "Basic knowledge of Thread class and Runnable",
          "Comfortable with ExecutorService and basic concurrency patterns",
          "Advanced knowledge including CompletableFuture and reactive programming"
        ]
      },
      {
        id: 'testing_java',
        text: 'How proficient are you with Java testing frameworks?',
        options: [
          "Limited or no testing experience",
          "Basic unit testing with JUnit",
          "Comfortable with JUnit, Mockito and integration testing",
          "Advanced testing including TDD and BDD approaches"
        ]
      }
    ];
  } else if (target === 'backend' && language === 'javascript') {
    return [
      {
        id: 'node_basics',
        text: 'How comfortable are you with Node.js fundamentals?',
        options: [
          "I'm new to Node.js",
          "I understand basic concepts and can write simple scripts",
          "I'm comfortable building full applications with Node.js",
          "I have deep knowledge including internal workings"
        ]
      },
      {
        id: 'express_framework',
        text: 'What is your experience level with Express.js or similar frameworks?',
        options: [
          "Never used Express or similar frameworks",
          "Basic understanding of routes and middleware",
          "Comfortable building REST APIs with Express",
          "Advanced knowledge including custom middleware and best practices"
        ]
      },
      {
        id: 'js_databases',
        text: 'How would you rate your knowledge of databases with JavaScript/Node.js?',
        options: [
          "Limited database experience with Node.js",
          "Basic understanding of MongoDB or SQL with Node.js",
          "Comfortable with ORMs like Mongoose or Sequelize",
          "Advanced knowledge including performance optimization and scaling"
        ]
      },
      {
        id: 'async_js',
        text: 'What is your experience with asynchronous JavaScript patterns?',
        options: [
          "Limited understanding of asynchronous code",
          "Comfortable with callbacks and Promises",
          "Proficient with async/await and Promise methods",
          "Advanced knowledge including event loops and streams"
        ]
      },
      {
        id: 'testing_js',
        text: 'How proficient are you with JavaScript testing frameworks?',
        options: [
          "Limited or no testing experience",
          "Basic unit testing with Jest or Mocha",
          "Comfortable with testing and mocking",
          "Advanced testing practices including TDD"
        ]
      }
    ];
  } else if (target === 'frontend' && language === 'javascript') {
    return [
      {
        id: 'react_knowledge',
        text: 'How comfortable are you with React?',
        options: [
          "I'm new to React",
          "I understand basic components and props",
          "I'm comfortable with hooks and state management",
          "I have deep knowledge including advanced patterns"
        ]
      },
      {
        id: 'css_knowledge',
        text: 'What is your experience level with CSS and styling in React?',
        options: [
          "Basic CSS knowledge",
          "Comfortable with CSS-in-JS or CSS modules",
          "Proficient with responsive design and animations",
          "Advanced knowledge including design systems"
        ]
      },
      {
        id: 'state_management',
        text: 'How would you rate your knowledge of state management in React?',
        options: [
          "Only familiar with useState/useContext",
          "Have used Redux or similar libraries",
          "Comfortable with various state management approaches",
          "Advanced knowledge including performance optimization"
        ]
      },
      {
        id: 'typescript_react',
        text: 'What is your experience with TypeScript in React applications?',
        options: [
          "Limited TypeScript experience",
          "Basic typing of props and state",
          "Comfortable with TypeScript generics and utility types",
          "Advanced TypeScript patterns and type safety"
        ]
      },
      {
        id: 'frontend_testing',
        text: 'How proficient are you with frontend testing?',
        options: [
          "Limited testing experience",
          "Basic unit testing with React Testing Library",
          "Comfortable with integration and component testing",
          "Advanced testing including E2E with Cypress or similar"
        ]
      }
    ];
  } else if (target === 'frontend' && language === 'java') {
    return [
      {
        id: 'android_basics',
        text: 'How comfortable are you with Android development basics?',
        options: [
          "I'm new to Android development",
          "I understand basic Android components and lifecycle",
          "I'm comfortable building full Android applications",
          "I have deep knowledge including advanced Android concepts"
        ]
      },
      {
        id: 'android_ui',
        text: 'What is your experience level with Android UI development?',
        options: [
          "Basic XML layouts knowledge",
          "Comfortable with ConstraintLayout and basic UI components",
          "Proficient with custom views and animations",
          "Advanced knowledge including Jetpack Compose"
        ]
      },
      {
        id: 'android_arch',
        text: 'How would you rate your knowledge of Android architecture patterns?',
        options: [
          "Limited architecture knowledge",
          "Familiar with MVC pattern",
          "Comfortable with MVVM and Android Architecture Components",
          "Advanced knowledge including Clean Architecture"
        ]
      },
      {
        id: 'kotlin_android',
        text: 'What is your experience with Kotlin for Android development?',
        options: [
          "Limited or no Kotlin experience",
          "Basic Kotlin syntax and interop with Java",
          "Comfortable with Kotlin coroutines and extensions",
          "Advanced Kotlin including DSLs and multiplatform"
        ]
      },
      {
        id: 'android_testing',
        text: 'How proficient are you with Android testing?',
        options: [
          "Limited testing experience",
          "Basic unit testing with JUnit",
          "Comfortable with instrumentation tests",
          "Advanced testing including Espresso and UI automation"
        ]
      }
    ];
  } else if (target === 'test' && language === 'java') {
    return [
      {
        id: 'java_testing_frameworks',
        text: 'How comfortable are you with Java testing frameworks?',
        options: [
          "I'm new to Java testing",
          "I understand JUnit basics",
          "I'm comfortable with JUnit, Mockito, and TestNG",
          "I have deep knowledge including advanced testing patterns"
        ]
      },
      {
        id: 'api_testing_java',
        text: 'What is your experience level with API testing in Java?',
        options: [
          "Limited API testing experience",
          "Familiar with RestAssured or similar",
          "Comfortable with comprehensive API test suites",
          "Advanced knowledge including performance testing"
        ]
      },
      {
        id: 'ui_testing_java',
        text: 'How would you rate your knowledge of UI testing with Java?',
        options: [
          "Limited UI testing experience",
          "Basic Selenium WebDriver knowledge",
          "Comfortable with Page Object Model and test frameworks",
          "Advanced UI testing including BDD frameworks like Cucumber"
        ]
      },
      {
        id: 'ci_cd_knowledge',
        text: 'What is your experience with CI/CD pipelines for testing?',
        options: [
          "Limited CI/CD experience",
          "Basic understanding of Jenkins or similar tools",
          "Comfortable setting up test automation in CI/CD",
          "Advanced knowledge including sophisticated test orchestration"
        ]
      },
      {
        id: 'test_management',
        text: 'How proficient are you with test management and reporting?',
        options: [
          "Limited test management experience",
          "Basic test planning and execution",
          "Comfortable with test management tools",
          "Advanced knowledge including metrics and quality gates"
        ]
      }
    ];
  } else if (target === 'test' && language === 'javascript') {
    return [
      {
        id: 'js_testing_frameworks',
        text: 'How comfortable are you with JavaScript testing frameworks?',
        options: [
          "I'm new to JavaScript testing",
          "I understand Jest or Mocha basics",
          "I'm comfortable with various testing approaches",
          "I have deep knowledge including advanced patterns"
        ]
      },
      {
        id: 'api_testing_js',
        text: 'What is your experience level with API testing in JavaScript?',
        options: [
          "Limited API testing experience",
          "Familiar with Supertest or similar",
          "Comfortable with comprehensive API test suites",
          "Advanced knowledge including performance testing"
        ]
      },
      {
        id: 'ui_testing_js',
        text: 'How would you rate your knowledge of UI testing with JavaScript?',
        options: [
          "Limited UI testing experience",
          "Basic Cypress or Playwright knowledge",
          "Comfortable with component and E2E testing",
          "Advanced UI testing including visual regression"
        ]
      },
      {
        id: 'automation_frameworks',
        text: 'What is your experience with JavaScript test automation frameworks?',
        options: [
          "Limited automation experience",
          "Basic test automation setup",
          "Comfortable building custom automation frameworks",
          "Advanced knowledge including BDD frameworks"
        ]
      },
      {
        id: 'js_test_reporting',
        text: 'How proficient are you with test reporting in JavaScript ecosystems?',
        options: [
          "Limited reporting experience",
          "Basic HTML/JSON reports",
          "Comfortable with advanced reporting and dashboards",
          "Advanced knowledge including custom reporting solutions"
        ]
      }
    ];
  }

  // Default fallback questions
  return [
    {
      id: 'general_experience',
      text: 'How many years of experience do you have in software development?',
      options: [
        "Less than 1 year",
        "1-3 years",
        "3-5 years",
        "More than 5 years"
      ]
    },
    {
      id: 'general_knowledge',
      text: 'How would you rate your general technical knowledge?',
      options: [
        "Beginner",
        "Intermediate",
        "Advanced",
        "Expert"
      ]
    },
    {
      id: 'learning_speed',
      text: 'How quickly do you typically learn new technologies?',
      options: [
        "I need time and structured learning",
        "I can learn at a moderate pace",
        "I learn new technologies quickly",
        "I excel at rapidly adopting new technologies"
      ]
    },
    {
      id: 'team_preference',
      text: 'What team size do you prefer working with?',
      options: [
        "I prefer working independently",
        "Small teams (2-5 people)",
        "Medium teams (5-10 people)",
        "Large teams (10+ people)"
      ]
    },
    {
      id: 'work_style',
      text: 'How would you describe your work style?',
      options: [
        "I prefer clear direction and defined tasks",
        "I like a mix of guidance and autonomy",
        "I prefer autonomy with general goals",
        "I thrive with complete independence and ownership"
      ]
    }
  ];
};