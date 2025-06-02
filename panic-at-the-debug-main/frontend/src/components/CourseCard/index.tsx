import React from 'react';

interface CourseCardProps {
  title: string;
  description: string;
  price: string;
  imageUrl: string;
  altText: string;
}

const CourseCard: React.FC<CourseCardProps> = ({
  title,
  description,
  price,
  imageUrl,
  altText,
}) => {
  return (
    <div className="bg-white rounded-lg overflow-hidden border border-gray-200 h-full flex flex-col">
      <div className="w-full h-48 overflow-hidden">
        <img
          src={imageUrl}
          alt={altText}
          className="w-full h-full object-cover"
        />
      </div>
      <div className="p-6 flex flex-col flex-grow">
        <h3 className="text-xl font-semibold mb-2 text-gray-800">{title}</h3>
        <p className="text-gray-600 mb-4 flex-grow">{description}</p>
        <div className="text-blue-500 font-medium">Price : {price}</div>
      </div>
    </div>
  );
};

export default CourseCard;