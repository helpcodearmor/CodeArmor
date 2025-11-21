"use client";
import { motion } from "framer-motion";
import { Fingerprint } from "lucide-react";

export default function CsrfAttack() {
  return (
    <motion.section
      id="csrf"
      initial={{ opacity: 0, y: 40 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true }}
      transition={{ duration: 0.7 }}
      className="py-20 px-6 bg-[#030b17] border-t border-blue-500/20"
    >
      <div className="max-w-6xl mx-auto grid md:grid-cols-2 gap-12 items-center">

        <div>
          <h2 className="text-3xl md:text-4xl font-bold text-blue-300 flex items-center gap-3">
            <Fingerprint size={36} /> CSRF (Cross-Site Request Forgery)
          </h2>

          <p className="mt-4 text-gray-300 text-sm md:text-base leading-relaxed">
            CSRF forces authenticated users to execute unwanted actions on a web
            application. Attackers can trigger fund transfers, password changes,
            or data deletion without user consent.
          </p>

          <p className="mt-3 text-gray-400 text-sm">
            🔰 Prevention: CSRF Tokens, SameSite Cookies, Secure Session Validation.
          </p>
        </div>

        <motion.div
          whileHover={{ scale: 1.03 }}
          className="p-6 bg-blue-900/20 border border-blue-500/20 rounded-xl shadow-lg shadow-blue-900/20"
        >
          <pre className="text-blue-300 text-xs md:text-sm overflow-x-auto">
{`// Add CSRF Protection
<form method="POST">
  <input type="hidden" name="csrf_token" value="{token}" />
</form>`}
          </pre>
        </motion.div>
      </div>
    </motion.section>
  );
}
