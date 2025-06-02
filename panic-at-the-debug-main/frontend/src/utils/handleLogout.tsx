import { useNavigate } from "react-router-dom";

export const handleLogout = () => {
      const navigate = useNavigate();

    // Handle user logout, e.g., clear tokens, reset state
    // Redirect to the home page
    navigate("/");
    localStorage.clear();
  };