"use client";
import { motion } from "framer-motion";
import { GlobeLock, SearchCheck, ShieldCheck } from "lucide-react";

export default function HowItWorks() {
  const steps = [
    {
      icon: <GlobeLock size={36} />,
      title: "1. Enter Website URL / API / Code",
      desc: "Paste your target URL, API endpoint, or code snippet in our secure scanner form."
    },
    {
      icon: <SearchCheck size={36} />,
      title: "2. Smart Vulnerability Scanning",
      desc: "Our engine performs deep checks for SQLi, XSS, CSRF & vulnerable dependencies."
    },
    {
      icon: <ShieldCheck size={36} />,
      title: "3. Get Detailed Security Report",
      desc: "Receive a detailed threat breakdown along with suggested fixes and prevention steps."
    },
  ];

  return (
    <section className="bg-black text-white py-20 px-6">
      <div className="max-w-6xl mx-auto text-center">
        <h2 className="text-4xl font-bold">How Vulnerability Scanning Works</h2>
        <p className="text-gray-300 mt-3">
          Advanced scanning powered by AI-driven cybersecurity logic.
        </p>

        <div className="grid md:grid-cols-3 gap-10 mt-14">
          {steps.map((item, index) => (
            <motion.div
              key={index}
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: index * 0.15 }}
              className="bg-[#0f172a] p-8 border border-gray-700 rounded-2xl hover:scale-[1.03] transition-transform"
            >
              <div className="text-blue-400 flex justify-center mb-4">{item.icon}</div>
              <h3 className="text-xl font-semibold">{item.title}</h3>
              <p className="text-gray-300 mt-2">{item.desc}</p>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}
