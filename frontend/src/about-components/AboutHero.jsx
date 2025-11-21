"use client";
import { motion } from "framer-motion";
import { ShieldCheck, SearchCheck, Lock, Bug } from "lucide-react";

export default function AboutHero() {
  return (
    <section className="relative bg-gradient-to-b from-black via-[#070b14] to-black text-white py-24 px-6 overflow-hidden">
      <div className="max-w-6xl mx-auto grid md:grid-cols-2 gap-10 items-center">
        
        {/* LEFT TEXT CONTENT */}
        <motion.div
          initial={{ opacity: 0, x: -40 }}
          whileInView={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.6 }}
        >
          <h1 className="text-4xl md:text-6xl font-bold leading-tight">
            About <span className="text-blue-400">CodeArmor</span>
          </h1>

          <p className="mt-4 text-lg text-gray-300">
            We help developers, startups, and enterprises identify real cyber threats like{" "}
            <span className="text-blue-400 font-semibold">SQL Injection, XSS, CSRF, and Dependency Vulnerabilities</span> 
            before attackers exploit their systems. Our AI-powered engine analyzes source code, APIs, 
            and live URLs for loopholes and security misconfigurations.
          </p>
        </motion.div>

        {/* RIGHT CYBER SECURITY PANEL (Replaces Image) */}
        <motion.div
          initial={{ opacity: 0, x: 40 }}
          whileInView={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.7 }}
          className="bg-[#0d1326]/50 border border-blue-400/10 backdrop-blur-md rounded-xl p-6 shadow-lg shadow-blue-900/30"
        >
          <h3 className="text-xl font-semibold text-blue-400 flex items-center gap-2">
            <ShieldCheck size={22} /> Security Insights
          </h3>

          <div className="mt-4 space-y-3 text-sm text-gray-300">
            <Stat text="🔐 145,000+ SQL Injection attacks detected globally per day" />
            <Stat text="🛡 1 in 3 websites are vulnerable to XSS attacks" />
            <Stat text="⚠ 62% of critical breaches involve insecure dependencies" />
            <Stat text="🚨 CSRF exploits rising due to weak session protection" />
          </div>

          {/* AUTO-SCROLLING THREAT STREAM */}
          <div className="mt-6 h-[110px] overflow-hidden relative border-t border-blue-500/10 pt-3">
            <motion.div
              animate={{ y: ["0%", "-100%"] }}
              transition={{ duration: 11, repeat: Infinity, ease: "linear" }}
              className="space-y-3"
            >
              <Feed text="Blocked unauthorized admin access attempt" />
              <Feed text="XSS payload neutralized in form input field" />
              <Feed text="Malicious dependency package identified" />
              <Feed text="Suspicious token-based CSRF activity prevented" />
            </motion.div>
          </div>
        </motion.div>

      </div>
    </section>
  );
}

function Stat({ text }) {
  return <p className="flex items-center gap-2">{text}</p>;
}

function Feed({ text }) {
  return (
    <p className="text-gray-400 text-xs flex items-center gap-2">
      <Lock size={13} className="text-blue-400" /> {text}
    </p>
  );
}
