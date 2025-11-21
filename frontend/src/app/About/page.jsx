// import React from 'react'
// import AboutHero from ".@/about_components/AboutHero";

// function About() {
//   return (
//     <div>
//       <AboutHero/>
//     </div>
//   )
// }

// export default About


import Navbar from "@/components/Navbar";
import AboutHero from "@/about-components/AboutHero";
import MissionVision from "@/about-components/MissionVision";
import HowItWorks from "@/about-components/HowItWorks";
import WhyChooseUs from "@/about-components/WhyChooseUs";
import CoreValues from "@/about-components/CoreValues";
import Footer from "@/components/Footer"; // or AboutFooter if different

export default function AboutPage() {
  return (
    <>
    <div id="hero"></div>
      <Navbar />
      <AboutHero />
      <MissionVision />
      <HowItWorks />
      <WhyChooseUs />
      <CoreValues />
      <Footer />
    </>
  );
}
