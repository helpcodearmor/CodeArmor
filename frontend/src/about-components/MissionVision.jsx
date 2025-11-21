"use client";
import { motion } from "framer-motion";

export default function MissionVision() {
  const cardAnim = {
    hidden: { opacity: 0, y: 40 },
    show: { opacity: 1, y: 0 },
  };

  return (
    <section className="bg-[#0b0f1a] text-white py-20 px-6">
      <div className="max-w-6xl mx-auto text-center mb-14">
        <h2 className="text-4xl font-bold">Our Mission & Vision</h2>
        <p className="text-gray-300 mt-3">
          Securing the internet—one website at a time.
        </p>
      </div>

      <div className="grid md:grid-cols-2 gap-10 max-w-6xl mx-auto">
        <motion.div
          variants={cardAnim}
          initial="hidden"
          whileInView="show"
          transition={{ duration: 0.5 }}
          className="bg-[#111827] border border-gray-700 rounded-2xl p-8"
        >
          <h3 className="text-2xl font-semibold text-blue-400">Mission</h3>
          <p className="text-gray-300 mt-3">
            To empower businesses and developers with intelligent tools to identify, 
            predict, and prevent cyber threats. We aim to simplify vulnerability 
            detection by scanning websites, APIs, and codebases effortlessly.
          </p>
        </motion.div>

        <motion.div
          variants={cardAnim}
          initial="hidden"
          whileInView="show"
          transition={{ duration: 0.6 }}
          className="bg-[#111827] border border-gray-700 rounded-2xl p-8"
        >
          <h3 className="text-2xl font-semibold text-blue-400">Vision</h3>
          <p className="text-gray-300 mt-3">
            A safer digital world where every website is shielded against exploits 
            like SQL Injection, XSS, CSRF, and Dependency flaws—combining automation, AI, and deep security intelligence.
          </p>
        </motion.div>
      </div>
    </section>
  );
}
