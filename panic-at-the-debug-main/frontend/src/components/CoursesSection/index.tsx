import React from 'react';
import CourseCard from '../CourseCard';

// Define the courses data
const coursesData = [
  {
    id: 1,
    title: 'Fundamental Of UI/UX Design',
    description: 'Some quick example text to build on the card title and make up the bulk of the card.',
    price: '20$',
    imageUrl: '/api/placeholder/400/300', // Using placeholder for the UI/UX image
    altText: 'UI/UX Design Course',
  },
  {
    id: 2,
    title: 'Javascript Basic to advanced',
    description: 'Some quick example text to build on the card title and make up the bulk of the card.',
    price: '20$',
    imageUrl: '/api/placeholder/400/300', // Using placeholder for the JavaScript image
    altText: 'JavaScript Course',
  },
  {
    id: 3,
    title: 'Fullstack Web Development',
    description: 'Some quick example text to build on the card title and make up the bulk of the card.',
    price: '20$',
    imageUrl: '/api/placeholder/400/300', // Using placeholder for the Fullstack image
    altText: 'Fullstack Web Development Course',
  },
  {
    id: 4,
    title: 'Digital Marketing',
    description: 'Some quick example text to build on the card title and make up the bulk of the card.',
    price: '20$',
    imageUrl: '/api/placeholder/400/300', // Using placeholder for the Digital Marketing image
    altText: 'Digital Marketing Course',
  },
  {
    id: 5,
    title: 'Photography Basic Rules',
    description: 'Some quick example text to build on the card title and make up the bulk of the card.',
    price: '20$',
    imageUrl: '/api/placeholder/400/300', // Using placeholder for the Photography image
    altText: 'Photography Course',
  },
  {
    id: 6,
    title: 'Motion Graphics',
    description: 'Some quick example text to build on the card title and make up the bulk of the card.',
    price: '20$',
    imageUrl: '/api/placeholder/400/300', // Using placeholder for the Motion Graphics image
    altText: 'Motion Graphics Course',
  },
];

const CoursesSection: React.FC = () => {
  return (
    <section className="py-16 px-4 bg-gray-50">
      <div className="container mx-auto">
        <div className="text-center mb-12">
          <h2 className="text-4xl font-bold mb-4 text-gray-800">Discover Our Popular Courses</h2>
          <p className="text-gray-600 max-w-2xl mx-auto">
            It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {coursesData.map((course) => (
            <CourseCard
              key={course.id}
              title={course.title}
              description={course.description}
              price={course.price}
              imageUrl={course.imageUrl}
              altText={course.altText}
            />
          ))}
        </div>

        <div className="text-center mt-12">
          <button className="px-8 py-3 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition-colors duration-300">
            See More Courses
          </button>
        </div>
      </div>
    </section>
  );
};

export default CoursesSection;