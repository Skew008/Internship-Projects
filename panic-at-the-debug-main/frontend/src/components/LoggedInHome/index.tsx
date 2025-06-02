import React, { useEffect, useState } from "react";
import Navbar from "../Navbar";
import Hero from "../Hero";
import Button from "../Button";
import { Link } from "react-router-dom";
import ChatWithUs from "../ChatBot/ChatWithUs";

const LoggedInHome: React.FC = () => {
  const [assessmentStatus, setAssessmentStatus] = useState(""); // State for assessment status

  useEffect(() => {
    // Fetch assessmentStatus from localStorage (or API if applicable)
    const status = localStorage.getItem("assessmentStatus") || "";
    setAssessmentStatus(status); // Update state with the stored status
  }, []); // Runs only once when the component mounts

  return (
    <>
      <Navbar 
        isLoggedIn       />
      <Hero>
        <div className="relative h-full flex flex-col items-center justify-end text-center text-white px-4 pb-6">
          <h1 className="text-4xl md:text-6xl font-bold mb-6">
            Ready for Your Next Step?
          </h1>

          {/* Conditional Link and Button based on assessmentStatus */}
          <Link to={assessmentStatus !== "COMPLETE" ? "/assesments" : "#"}>
            <Button 
              primary 
              className="mt-6" 
              disabled={assessmentStatus === "COMPLETE"} // Disable button if assessmentStatus is COMPLETE
            >
              {assessmentStatus === "COMPLETE" ? "Assessment Completed" : "Take Assessment"}
            </Button>
          </Link>
          <ChatWithUs />
        </div>
      </Hero>
    </>
  );
};

export default LoggedInHome;