"use client";
import { motion } from "framer-motion";
import { ShieldAlert, Cpu, BarChart3, Zap } from "lucide-react";

export default function WhyChooseUs() {
  const features = [
    { icon: <ShieldAlert size={34} />, title: "Real Vulnerability Detection", desc: "Detects SQL Injection, XSS, CSRF, SSRF, and Dependency risks." },
    { icon: <Cpu size={34} />, title: "AI-powered Threat Analysis", desc: "Machine-learning engine identifies and predicts unknown attack vectors." },
    { icon: <BarChart3 size={34} />, title: "Detailed Action Reports", desc: "Fix recommendations with severity ranking and exploit possibility." },
    { icon: <Zap size={34} />, title: "Fast + Secure Scanning", desc: "Optimized scanning algorithm with encryption & sandbox testing." },
  ];

  return (
    <section className="bg-[#070b14] text-white py-20 px-6">
      <div className="max-w-6xl mx-auto text-center">
        <h2 className="text-4xl font-bold">Why Choose Our Scanner?</h2>
        <p className="text-gray-300 mt-3">Security built for modern web applications.</p>

        <div className="grid md:grid-cols-4 gap-8 mt-14">
          {features.map((item, i) => (
            <motion.div
              key={i}
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: i * 0.15 }}
              className="bg-[#0f172a] p-6 border border-gray-700 rounded-2xl hover:scale-105 transition-transform"
            >
              <div className="text-blue-400 flex justify-center mb-3">{item.icon}</div>
              <h3 className="text-lg font-semibold">{item.title}</h3>
              <p className="text-gray-300 text-sm mt-2">{item.desc}</p>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}
