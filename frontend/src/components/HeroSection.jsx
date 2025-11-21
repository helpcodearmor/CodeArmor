"use client";
import { motion } from "framer-motion";
import { ShieldCheck, ArrowDownCircle } from "lucide-react";

export default function HeroSection() {
  return (
    <section
      id="hero"
      className="relative w-full min-h-[85vh] flex items-center justify-center text-white overflow-hidden bg-[#030712]"
    >
      {/* Background blurred gradient effects */}
      <div className="absolute top-0 left-0 w-[350px] h-[350px] bg-purple-600/30 rounded-full blur-3xl"></div>
      <div className="absolute bottom-10 right-0 w-[300px] h-[300px] bg-blue-600/20 rounded-full blur-3xl"></div>

      {/* Hero content */}
      <div className="relative z-10 max-w-5xl text-center px-6 py-20">
        <motion.h1
          initial={{ opacity: 0, y: -25 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.9 }}
          className="text-4xl md:text-6xl font-extrabold leading-tight"
        >
          Detect & Prevent{" "}
          <span className="text-transparent bg-clip-text bg-gradient-to-r from-purple-400 to-blue-500">
            Web Vulnerabilities
          </span>{" "}
          Instantly
        </motion.h1>

        <motion.p
          initial={{ opacity: 0, y: 15 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3, duration: 0.9 }}
          className="mt-4 text-lg md:text-xl text-gray-300 max-w-2xl mx-auto"
        >
          Our AI-powered cybersecurity scanner identifies risk points such as
          SQL Injection, XSS, CSRF, and Dependency vulnerabilities in seconds.
          Keep your application protected 24/7.
        </motion.p>

        {/* CTA Buttons */}
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ delay: 0.6, duration: 0.7 }}
          className="mt-8 flex flex-col md:flex-row gap-4 justify-center"
        >
          <a
            href="#scanner"
            className="px-7 py-3 rounded-xl text-lg font-semibold bg-gradient-to-r from-purple-600 to-blue-600 hover:opacity-90 transition-all flex items-center gap-2"
          >
            <ShieldCheck size={22} /> Start Security Scan
          </a>

          <a
            href="#features"
            className="px-7 py-3 rounded-xl text-lg font-semibold bg-white/10 hover:bg-white/20 backdrop-blur-lg border border-white/10 transition-all flex items-center gap-2"
          >
            <ArrowDownCircle size={22} /> Explore Features
          </a>
        </motion.div>

        {/* 🔥 Live Threat Ticker Section — replaces image */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.9, duration: 0.8 }}
          className="mt-12 max-w-xl mx-auto bg-[#0a0f1e]/40 border border-purple-500/10 rounded-xl overflow-hidden backdrop-blur-lg shadow-lg shadow-purple-900/20"
        >
          <div className="py-3 text-sm font-semibold text-purple-300 border-b border-purple-500/10 bg-[#0d1324]/50">
            🚨 Real-Time Threat Intelligence Feed
          </div>

          <div className="h-[120px] overflow-hidden">
            <motion.div
              animate={{ y: ["0%", "-100%"] }}
              transition={{ duration: 12, repeat: Infinity, ease: "linear" }}
              className="flex flex-col gap-4 py-4"
            >
              <Threat text="SQL Injection exploit detected and blocked" />
              <Threat text="XSS payload intercepted before execution" />
              <Threat text="Unauthorized admin access prevented" />
              <Threat text="Dependency vulnerability neutralized" />
              <Threat text="CSRF attack attempt successfully stopped" />
            </motion.div>
          </div>
        </motion.div>
      </div>
    </section>
  );
}

function Threat({ text }) {
  return (
    <div className="flex items-center gap-2 text-gray-300 px-4">
      <span className="text-purple-400">⚡</span>
      <p className="text-sm">{text}</p>
    </div>
  );
}
