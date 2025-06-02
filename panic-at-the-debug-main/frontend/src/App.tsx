import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Survey from "./pages/Survey"
import RoadmapFlow from './components/Roadmap/RoadmapFlow';
import Assessment from './components/Assesment';
import Home from './components/Home';
import LoggedInHome from './components/LoggedInHome';
import Dashboard from './components/Dashboard';
// import Wellbeing from './components/Wellbeing';


const App: React.FC = () => {
  return (
    <Router>
      <div className="min-h-screen bg-gray-50">
        {/* <Wellbeing /> */}
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/survey" element={<Survey />} />
          <Route path="/assesments" element={<Assessment />} />
          <Route path="/roadmap" element={<RoadmapFlow />} />
          <Route path="/home" element={<LoggedInHome />} />
          <Route path="/dashboard" element={<Dashboard />} />
        </Routes>
      </div>
    </Router>
  );
};

export default App;