import React from "react";
import Button from "../Button";
import { Link, useNavigate } from "react-router-dom";

const Navbar: React.FC<{ isLoggedIn?: boolean }> = ({ isLoggedIn = false }) => {
  const navigate = useNavigate(); // Access the routing mechanism

  const handleLogout = () => {
    // Clear localStorage and redirect the user
    localStorage.clear();
    navigate("/"); // Redirect to the home page
  };

  return (
    <nav className="flex justify-between items-center py-4 px-6 md:px-12 bg-[#FAEDCA] border-b-2">
      {/* Logo */}
      <Link to="/home"><div className="text-2xl font-bold text-gray-800">PathSync</div></Link>

      {/* Links for both logged-in and guest users */}
      <div className="flex-1 flex justify-center">
        {isLoggedIn ? (
          // Links for logged-in users, centered
          <div className="flex space-x-8">
            <Link to="/roadmap" className="text-gray-600 hover:text-gray-900">
              Roadmap
            </Link>
            <Link to="/dashboard" className="text-gray-600 hover:text-gray-900">
              Dashboard
            </Link>
          </div>
        ) : (
          // Links for guests, centered
          <div className="flex space-x-8">
            <a href="#" className="text-gray-600 hover:text-gray-900">
              Home
            </a>
            <a href="#" className="text-gray-600 hover:text-gray-900">
              Courses
            </a>
            <a href="#" className="text-gray-600 hover:text-gray-900">
              About
            </a>
            <a href="#" className="text-gray-600 hover:text-gray-900">
              Contact Us
            </a>
          </div>
        )}
      </div>

      {/* Logout for logged-in users */}
      {isLoggedIn ? (
        <button
          onClick={handleLogout} // Invoke the logout logic here
          className="text-gray-600 hover:text-gray-900 px-4 py-1 border border-red-500 rounded cursor-pointer bg-white"
        >
          Logout
        </button>
      ) : (
        // Login for guests
        <div className="hidden md:block cursor-pointer">
          <Link to="/login">
            <Button primary={false}>Login</Button>
          </Link>
        </div>
      )}

      {/* Mobile menu (burger icon) */}
      <div className="md:hidden">
        <button className="text-gray-600">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-6 w-6"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M4 6h16M4 12h16m-7 6h7"
            />
          </svg>
        </button>
      </div>
    </nav>
  );
};

export default Navbar;