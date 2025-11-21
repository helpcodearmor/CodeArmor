// "use client";
// import { motion } from "framer-motion";
// import { ShieldCheck, SearchCheck, Bug, Zap } from "lucide-react";

// const features = [
//   {
//     icon: <SearchCheck size={34} />,
//     title: "Smart Vulnerability Scan",
//     description:
//       "Automatically detects SQL Injection, XSS, CSRF, and Dependency exploits using AI-powered scanning algorithms.",
//   },
//   {
//     icon: <Bug size={34} />,
//     title: "Real-Time Threat Analysis",
//     description:
//       "Monitors request patterns and identifies attack signatures to warn you before an actual breach occurs.",
//   },
//   {
//     icon: <ShieldCheck size={34} />,
//     title: "Security Risk Score",
//     description:
//       "Generates an overall security score so you can instantly determine the safety level of your website.",
//   },
//   {
//     icon: <Zap size={34} />,
//     title: "Fast & Lightweight Engine",
//     description:
//       "Highly optimized scanning engine ensures minimal resource usage and ultra-fast vulnerability checks.",
//   },
// ];

// export default function Features() {
//   return (
//     <section
//       id="features"
//       className="relative py-24 px-6 bg-[#020617] text-gray-300 border-t border-purple-500/10"
//     >
//       {/* Glow Background */}
//       <div className="absolute inset-0 bg-gradient-to-b from-purple-800/5 to-transparent pointer-events-none"></div>

//       <motion.div
//         initial={{ opacity: 0, y: 25 }}
//         whileInView={{ opacity: 1, y: 0 }}
//         viewport={{ once: true }}
//         transition={{ duration: 0.7 }}
//         className="max-w-7xl mx-auto text-center relative z-10"
//       >
//         {/* Section Title */}
//         <h2 className="text-3xl md:text-4xl font-bold text-white">
//           Powerful Security <span className="text-purple-400">Features</span>
//         </h2>
//         <p className="mt-4 text-gray-400 max-w-2xl mx-auto text-sm md:text-base">
//           Enhance your website protection with AI-backed scanning that identifies critical weaknesses before attackers can exploit them.
//         </p>

//         {/* Feature Grid */}
//         <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8 mt-14">
//           {features.map((item, index) => (
//             <motion.div
//               key={index}
//               initial={{ opacity: 0, y: 30 }}
//               whileInView={{ opacity: 1, y: 0 }}
//               transition={{ duration: 0.6, delay: index * 0.15 }}
//               viewport={{ once: true }}
//               className="group p-6 border border-purple-500/10 rounded-xl bg-[#0a0f1e]/30 hover:bg-[#0f1530]/40 hover:border-purple-400/20 duration-300 shadow-lg shadow-black/20"
//             >
//               <div className="flex justify-center items-center text-purple-400 group-hover:scale-110 duration-300">
//                 {item.icon}
//               </div>
//               <h3 className="mt-5 text-lg font-semibold text-white">
//                 {item.title}
//               </h3>
//               <p className="mt-3 text-sm text-gray-400 leading-relaxed">
//                 {item.description}
//               </p>
//             </motion.div>
//           ))}
//         </div>
//       </motion.div>
//     </section>
//   );
// }


"use client";
import { motion } from "framer-motion";
import { ShieldCheck, SearchCheck, Bug, Zap, TerminalSquare, Lock } from "lucide-react";

const features = [
  {
    icon: <SearchCheck size={34} />,
    title: "Smart Vulnerability Scan",
    description:
      "Automatically identifies exploitable inputs and traces malicious patterns across APIs, websites, and backend systems.",
    code: `# Example Threat Detected:
[SQL Injection] Input field vulnerable at: /login.php
Payload Blocked: ' OR 1=1 --`,
  },
  {
    icon: <Bug size={34} />,
    title: "Real-Time Threat Analysis",
    description:
      "Continuously monitors web traffic, flags unusual patterns, and detects attacker fingerprints before damage occurs.",
    code: `# Suspicious Activity:
Multiple requests detected from IP: 192.168.1.22
Potential Attack Type: XSS Injection Attempt`,
  },
  {
    icon: <ShieldCheck size={34} />,
    title: "Security Risk Score",
    description:
      "Every scan generates a risk score helping you instantly understand your security posture and required fixes.",
    code: `# Risk Report Summary:
Total Issues Found: 3
Security Score: 82 / 100 (Moderate Risk)`,
  },
  {
    icon: <Zap size={34} />,
    title: "Fast & Lightweight Engine",
    description:
      "Built with next-gen optimization ensuring minimal load, fast threat detection and highly efficient scanning.",
    code: `# Engine Performance:
Scan Completed in: 2.19 sec
System Impact: < 3% CPU usage`,
  },
];

export default function Features() {
  return (
    <section
      id="features"
      className="relative py-28 px-6 bg-[#020617] text-gray-300 border-t border-purple-500/10"
    >
      {/* Glow Layer */}
      <div className="absolute inset-0 bg-gradient-to-b from-purple-900/10 to-transparent pointer-events-none"></div>

      <motion.div
        initial={{ opacity: 0, y: 25 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        transition={{ duration: 0.7 }}
        className="max-w-7xl mx-auto text-center relative z-10"
      >
        <h2 className="text-3xl md:text-4xl font-bold text-white">
          Advanced <span className="text-purple-400">Cyber Security</span> Features
        </h2>
        <p className="mt-4 text-gray-400 max-w-2xl mx-auto text-sm md:text-base">
          Designed to detect vulnerabilities like SQL Injection, XSS, CSRF, and Dependency exploits before attackers can exploit them.
        </p>

        {/* Feature Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8 mt-16">
          {features.map((item, index) => (
            <motion.div
              key={index}
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: index * 0.15 }}
              viewport={{ once: true }}
              className="group p-6 border border-purple-500/10 rounded-xl bg-[#0a0f1e]/30 hover:bg-[#0f1530]/40 hover:border-purple-400/30 duration-300 shadow-lg shadow-black/20 cursor-default"
            >
              <div className="flex justify-center items-center text-purple-400 group-hover:scale-110 duration-300">
                {item.icon}
              </div>

              <h3 className="mt-5 text-lg font-semibold text-white">
                {item.title}
              </h3>

              <p className="mt-3 text-sm text-gray-400 leading-relaxed">
                {item.description}
              </p>

              {/* Animated Code Output */}
              <motion.pre
                initial={{ opacity: 0 }}
                whileHover={{ opacity: 1 }}
                transition={{ duration: 0.3 }}
                className="mt-4 text-left p-3 h-28 overflow-auto text-xs rounded-lg bg-black/30 border border-purple-400/10 text-purple-300 font-mono"
              >
{item.code}
              </motion.pre>
            </motion.div>
          ))}
        </div>

        {/* Bottom Extra highlight */}
        <motion.div
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          className="mt-20 max-w-3xl mx-auto p-6 bg-[#0b1121]/40 border border-purple-500/20 rounded-2xl shadow-lg"
        >
          <div className="flex items-center gap-3 justify-center text-purple-300 text-lg font-semibold">
            <TerminalSquare size={26} /> Built for Developers & Security Teams
          </div>
          <p className="mt-3 text-gray-400 text-sm md:text-base leading-relaxed">
            Our AI-powered engine provides developers instant visibility into vulnerabilities without waiting for security audits. Ideal for API testing, source code validation, and automated CI/CD security checks.
          </p>
        </motion.div>
      </motion.div>
    </section>
  );
}
