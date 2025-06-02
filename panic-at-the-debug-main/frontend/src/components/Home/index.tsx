import React from "react";
import Navbar from "../Navbar";
import Hero from "../Hero";
import Button from "../Button";
import ChatWithUs from "../ChatBot/ChatWithUs";

const Home: React.FC = () => {
  return (
    <>
      <Navbar />
      <Hero>
        <div className="relative h-full flex flex-col items-center justify-end text-center text-white px-4 pb-6">
          <h1 className="text-4xl md:text-6xl font-bold mb-6">
            Get Started with Digital Learning
          </h1>
          <Button primary className="mt-6">Get Started</Button>
        </div>
        <ChatWithUs />
      </Hero>
    </>
  );
};

export default Home;