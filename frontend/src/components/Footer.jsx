// "use client";
// import { motion } from "framer-motion";
// import Link from "next/link";
// import { ShieldCheck, Github, Twitter, Mail, ArrowUp } from "lucide-react";

// export default function Footer() {
//   return (
//     <footer className="relative bg-[#030712] text-gray-300 py-14 px-6 border-t ">
//       {/* Cyber Glow Background */}
//       <div className="absolute inset-0 pointer-events-none bg-gradient-to-b from-transparent to-purple-900/10"></div>

//       <motion.div
//         initial={{ opacity: 0, y: 20 }}
//         whileInView={{ opacity: 1, y: 0 }}
//         viewport={{ once: true }}
//         transition={{ duration: 0.7 }}
//         className="max-w-7xl mx-auto relative z-10"
//       >
//         <div className="grid grid-cols-1 md:grid-cols-4 gap-10">

//           {/* Brand / About */}
//           <div>
//             <div className="flex items-center gap-2">
//               <ShieldCheck className="text-purple-400" size={28} />
//               <h2 className="text-xl font-bold text-white">CodeArmor</h2>
//             </div>
//             <p className="mt-3 text-sm leading-relaxed text-gray-400">
//               AI-powered cyber security scanner that detects vulnerabilities
//               such as SQL Injection, XSS, CSRF, and Dependency exploits. Protect
//               your web applications in real-time.
//             </p>
//           </div>

//           {/* Quick Links */}
//           <div>
//             <h3 className="text-lg font-semibold text-white">Quick Links</h3>
//             <ul className="mt-3 space-y-2 text-sm">
//               <li><Link href="#features" className="hover:text-purple-400 duration-200">Features</Link></li>
//               <li><Link href="#scanner" className="hover:text-purple-400 duration-200">Start Scan</Link></li>
//               <li><Link href="#pricing" className="hover:text-purple-400 duration-200">Pricing</Link></li>
//               <li><Link href="/TeamSection" className="hover:text-purple-400 duration-200">Testimonials</Link></li>
//             </ul>
//           </div>

//           {/* Security Topics */}
//           <div>
//             <h3 className="text-lg font-semibold text-white">Vulnerability Scans</h3>
//             <ul className="mt-3 space-y-2 text-sm">
//               <li>SQL Injection Detection</li>
//               <li>Cross-Site Scripting (XSS)</li>
//               <li>CSRF Attack Monitoring</li>
//               <li>Dependency & Package Risks</li>
//               <li>Broken Authentication</li>
//               <li>Misconfiguration Analysis</li>
//             </ul>
//           </div>

//           {/* Contact & Socials */}
//           <div>
//             <h3 className="text-lg font-semibold text-white">Contact</h3>
//             <ul className="mt-3 space-y-2 text-sm">
//               <li className="flex items-center gap-2">
//                 <Mail size={16} className="text-purple-300" />
//                 supportCodeArmor@gmail.com
//               </li>
//               {/* <li className="flex items-center gap-2 hover:text-purple-400 duration-200 cursor-pointer">
//                 <Github size={16} /> GitHub
//               </li>
//               <li className="flex items-center gap-2 hover:text-purple-400 duration-200 cursor-pointer">
//                 <Twitter size={16} /> Twitter
//               </li> */}
//             </ul>
//           </div>
//         </div>

//         {/* Divider Line */}
//         <div className="border-t border-white/10 mt-10 pt-6 text-sm flex flex-col md:flex-row justify-between items-center gap-4">
//           <p>© {new Date().getFullYear()} CodeArmor — All rights reserved.</p>

//           {/* Back to Top */}
//           <a
//             href="#hero"
//             className="flex items-center gap-2 text-purple-300 hover:text-purple-400 duration-200"
//           >
//             Back to Top <ArrowUp size={16} />
//           </a>
//         </div>
//       </motion.div>
//     </footer>
//   );
// }





"use client";
import { motion } from "framer-motion";
import Link from "next/link";
import { ShieldCheck, Mail, ArrowUp } from "lucide-react";

export default function Footer() {
  return (
    <footer className="relative bg-[#030712] text-gray-300 py-14 px-6 border-t">
      {/* Cyber Glow Background */}
      <div className="absolute inset-0 pointer-events-none bg-gradient-to-b from-transparent to-purple-900/10"></div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        transition={{ duration: 0.7 }}
        className="max-w-7xl mx-auto relative z-10"
      >

        {/* Grid (Auto-adjusted to 3 columns after removing Quick Links) */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-10">

          {/* Brand / About */}
          <div>
            <div className="flex items-center gap-2">
              <ShieldCheck className="text-purple-400" size={28} />
              <h2 className="text-xl font-bold text-white">CodeArmor</h2>
            </div>
            <p className="mt-3 text-sm leading-relaxed text-gray-400">
              AI-powered cyber security scanner that detects vulnerabilities
              such as SQL Injection, XSS, CSRF, and dependency exploits. 
              Protect your web applications in real-time.
            </p>
          </div>

          {/* Security Topics */}
          <div>
            <h3 className="text-lg font-semibold text-white">Vulnerability Scans</h3>
            <ul className="mt-3 space-y-2 text-sm">
              <li>SQL Injection Detection</li>
              <li>Cross-Site Scripting (XSS)</li>
              <li>CSRF Attack Monitoring</li>
              <li>Dependency & Package Risks</li>
              <li>Broken Authentication</li>
              <li>Misconfiguration Analysis</li>
            </ul>
          </div>

          {/* Contact */}
          <div>
            <h3 className="text-lg font-semibold text-white">Contact</h3>
            <ul className="mt-3 space-y-2 text-sm">
              <li className="flex items-center gap-2">
                <Mail size={16} className="text-purple-300" />
                helpCodeArmor@gmail.com
              </li>
            </ul>
          </div>
        </div>

        {/* Divider Line */}
        <div className="border-t border-white/10 mt-10 pt-6 text-sm flex flex-col md:flex-row justify-between items-center gap-4">
          <p>© {new Date().getFullYear()} CodeArmor — All rights reserved.</p>

          {/* Back to Top */}
          <a
            href="#hero"
            className="flex items-center gap-2 text-purple-300 hover:text-purple-400 duration-200"
          >
            Back to Top <ArrowUp size={16} />
          </a>
        </div>

      </motion.div>
    </footer>
  );
}
