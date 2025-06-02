interface StatDisplayProps {
  label: string;
  value: number | string;
  icon: React.ReactNode;
}

const StatDisplay = ({ label, value, icon }: StatDisplayProps) => {
  return (
    <div className="flex items-center p-4 bg-gray-50 rounded-lg">
      <div className="p-3 mr-4 bg-blue-100 rounded-full">
        {icon}
      </div>
      <div>
        <p className="text-sm font-medium text-gray-600">{label}</p>
        <p className="text-lg font-semibold text-gray-900">{value}</p>
      </div>
    </div>
  );
};

export default StatDisplay;