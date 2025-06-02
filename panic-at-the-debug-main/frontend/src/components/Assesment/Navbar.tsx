const Navbar = ({ timeRemaining }: { timeRemaining: number|null }) => {
  const minutes = Math.floor(timeRemaining / 60);
  const seconds = timeRemaining % 60;

  return (
    <div className="bg-indigo-600 py-3 px-6 text-white flex justify-between items-center">
      <h1 className="text-xl font-bold">PathSync Assessment</h1>
      <div>
        Time Remaining: {minutes}:{seconds < 10 ? '0' + seconds : seconds}
      </div>
    </div>
  );
};

export default Navbar;