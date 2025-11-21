"use client";
import { motion } from "framer-motion";
import { Database } from "lucide-react";

export default function SqlInjection() {
  return (
    <motion.section
      id="sql-injection"
      initial={{ opacity: 0, y: 40 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true }}
      transition={{ duration: 0.7 }}
      className="py-20 px-6 bg-[#030712] border-t border-red-500/20"
    >
      <div className="max-w-6xl mx-auto grid md:grid-cols-2 gap-12 items-center">
        
        {/* Text Content */}
        <div>
          <h2 className="text-3xl md:text-4xl font-bold text-red-400 flex items-center gap-3">
            <Database size={36} /> SQL Injection
          </h2>

          <p className="mt-4 text-gray-300 leading-relaxed text-sm md:text-base">
            SQL Injection is a cyber-attack technique where malicious SQL commands are inserted
            into database queries. When applications rely on unsafe input handling, attackers can
            force the database to reveal, modify, or delete sensitive information.
          </p>

          <p className="mt-3 text-gray-400 text-sm">
            🛡 Prevention: Use Parameterized Queries, Input Validation, WAF, and Least-Privilege Access.
          </p>
        </div>

        {/* Visual Query Flow Card (No User Input Example) */}
        <motion.div
          whileHover={{ scale: 1.03 }}
          className="p-6 bg-red-900/20 border border-red-500/20 rounded-xl shadow-lg shadow-red-900/20"
        >
          <pre className="text-red-300 text-xs md:text-sm leading-relaxed whitespace-pre-wrap">
{`📌 Normal Query Flow
-------------------------
User Request → Server → Database
SQL Query: SELECT * FROM products WHERE id = 42;

📌 Injected Query Flow
-------------------------
Attacker Request → Server → Database
SQL Query: SELECT * FROM products WHERE id = 42 OR 1=1;

⚠ Impact:
• Extract Entire Product List
• Tamper or Delete Records
• Bypass Authorization`}
          </pre>
        </motion.div>

      </div>
    </motion.section>
  );
}

