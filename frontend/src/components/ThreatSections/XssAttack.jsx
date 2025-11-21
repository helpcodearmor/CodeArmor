"use client";
import { motion } from "framer-motion";
import { ShieldAlert } from "lucide-react";

export default function XssAttack() {
  return (
    <motion.section
      id="xss-attack"
      initial={{ opacity: 0, y: 40 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true }}
      transition={{ duration: 0.7 }}
      className="py-20 px-6 bg-[#030712] border-t border-yellow-500/20"
    >
      <div className="max-w-6xl mx-auto grid md:grid-cols-2 gap-12 items-center">

        {/* Text Content */}
        <div>
          <h2 className="text-3xl md:text-4xl font-bold text-yellow-400 flex items-center gap-3">
            <ShieldAlert size={36} /> Cross-Site Scripting (XSS)
          </h2>

          <p className="mt-4 text-gray-300 leading-relaxed text-sm md:text-base">
            XSS (Cross-Site Scripting) is a web security flaw that allows attackers to 
            inject harmful scripts into websites viewed by other users. These scripts 
            run inside the victim’s browser without their knowledge.
          </p>

          <p className="mt-3 text-gray-400 leading-relaxed text-sm md:text-base">
            💀 <strong>Scenario:</strong> A visitor posts a comment on a blog. Instead of normal text, 
            the attacker secretly inserts a script that steals session cookies. When another 
            user reads that post, the script silently runs and sends their login session to 
            the attacker — giving unauthorized access to their account.
          </p>

          <p className="mt-3 text-gray-400 text-sm">
            🔐 <strong>Prevention:</strong> Output Encoding, Content Security Policy (CSP), HTML Escaping, 
            and Sanitizing User Input.
          </p>
        </div>

        {/* Animated Story Illustration */}
        <motion.div
          whileHover={{ scale: 1.03 }}
          className="p-6 bg-yellow-900/20 border border-yellow-500/20 rounded-xl shadow-lg shadow-yellow-900/20"
        >
          <div className="space-y-3 text-yellow-300 text-xs md:text-sm tracking-wide">
            <p>🗨️ Attacker posts a malicious comment...</p>
            <p>👀 Other users read the post...</p>
            <p>⚠️ Hidden script runs in the background...</p>
            <p>🔓 Session cookie stolen → Account Hijacked!</p>
          </div>
        </motion.div>

      </div>
    </motion.section>
  );
}
