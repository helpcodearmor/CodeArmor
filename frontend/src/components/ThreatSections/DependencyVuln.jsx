"use client";
import { motion } from "framer-motion";
import { PlugZap } from "lucide-react";

export default function DependencyVuln() {
  return (
    <motion.section
      id="dependency-vuln"
      initial={{ opacity: 0, y: 40 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true }}
      transition={{ duration: 0.7 }}
      className="py-20 px-6 bg-[#030712] border-t border-purple-500/20"
    >
      <div className="max-w-6xl mx-auto grid md:grid-cols-2 gap-12 items-center">

        {/* Text Content */}
        <div>
          <h2 className="text-3xl md:text-4xl font-bold text-purple-400 flex items-center gap-3">
            <PlugZap size={36} /> Dependency Vulnerabilities
          </h2>

          <p className="mt-4 text-gray-300 leading-relaxed text-sm md:text-base">
            Modern applications rely on hundreds of open-source libraries and packages. 
            If even one of those dependencies contains a security flaw, attackers may 
            exploit it to gain unauthorized system access, steal data, or execute 
            harmful commands.
          </p>

          <p className="mt-3 text-gray-400 leading-relaxed text-sm md:text-base">
            ⚠️ <strong>Example Scenario:</strong> A web application downloads a package update that contains 
            a hidden backdoor. Once deployed, this vulnerability allows attackers to run 
            malicious scripts remotely and compromise sensitive information.
          </p>

          <p className="mt-3 text-gray-400 text-sm">
            🔐 <strong>Prevention:</strong> Regular dependency audits, version locking, SBOM (Software Bill of Materials), 
            and using trusted package sources only.
          </p>
        </div>

        {/* Animated Info Card */}
        <motion.div
          whileHover={{ scale: 1.03 }}
          className="p-6 bg-purple-900/20 border border-purple-500/20 rounded-xl shadow-lg shadow-purple-900/20"
        >
          <div className="space-y-3 text-purple-300 text-xs md:text-sm tracking-wide">
            <p>📦 Updated library contains hidden vulnerability</p>
            <p>🛠 Application installs update automatically</p>
            <p>⚠️ Attackers exploit dependency to gain access</p>
            <p>🔒 Secure DevOps & Audit tools help mitigate risks</p>
          </div>
        </motion.div>

      </div>
    </motion.section>
  );
}
