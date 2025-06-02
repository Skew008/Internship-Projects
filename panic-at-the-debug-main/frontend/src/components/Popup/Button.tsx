const Button = ({
  children,
  onClick,
  variant = "primary",
}: {
  children: React.ReactNode;
  onClick: () => void;
  variant?: "primary" | "secondary";
}) => {
  const baseClass =
    "w-full py-2 px-4 text-center font-medium rounded-md transition-colors";
  const variantClass =
    variant === "primary"
      ? "bg-blue-500 text-white hover:bg-blue-600"
      : "bg-gray-100 text-black hover:bg-gray-200";

  return (
    <button className={`${baseClass} ${variantClass}`} onClick={onClick}>
      {children}
    </button>
  );
};

export default Button;