// "use client";

// import { useState } from "react";
// import { motion } from "framer-motion";

// export default function ScannerForm() {
//   const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

//   const [apiUrl, setApiUrl] = useState("");
//   const [code, setCode] = useState("");
//   const [result, setResult] = useState("");
//   const [loading, setLoading] = useState(false);

//   const handleScan = async (e) => {
//     e.preventDefault();
//     if (!apiUrl && !code.trim()) {
//       return alert("⚠ Please enter an API URL or paste code to scan.");
//     }

//     setLoading(true);
//     setResult("🔍 Scanning for vulnerabilities...");

//     try {
//       // Determine what to send to backend
//       // const payload = code.trim()
//       //   ? { code, language: "code" }
//       //   : { code: apiUrl, language: "api" }; // send API URL as 'code'

//       const userInput = code.trim() !== "" ? code : apiUrl;
//      const payload = {
//       code: userInput,
//       language: "" // empty → backend auto-detect
//       };


//       const response = await fetch(`${BACKEND_URL}/api/scan/raw`, {
//         method: "POST",
//         headers: { "Content-Type": "application/json" },
//         body: JSON.stringify(payload),
//       });

      

//       if (!response.ok) {
//         const text = await response.text();
//         throw new Error(`Backend error: ${text}`);
//       }

//       const data = await response.json();
//       // Display result as formatted JSON
//       setResult(JSON.stringify(data, null, 2));
//     } catch (err) {
//       console.error(err);
//       setResult(
//         "❌ Error connecting to backend. Make sure Spring Boot is running and NEXT_PUBLIC_BACKEND_URL is correct."
//       );
//     }

//     setLoading(false);
//   };

//   return (
//     <section id="scanner" className="py-20 max-w-6xl mx-auto px-6">
//       <motion.h2
//         initial={{ opacity: 0, y: -20 }}
//         whileInView={{ opacity: 1, y: 0 }}
//         className="text-3xl font-bold text-center text-cyan-400"
//       >
//         Vulnerability Scanner
//       </motion.h2>

//       <p className="text-center text-gray-300 mt-2">
//         Enter your API endpoint or paste your source code to detect threats like{" "}
//         <span className="text-red-400"> SQL Injection</span>,{" "}
//         <span className="text-yellow-300"> XSS</span>,{" "}
//         <span className="text-blue-300"> CSRF</span> & more.
//       </p>

//       <motion.form
//         onSubmit={handleScan}
//         initial={{ opacity: 0, scale: 0.95 }}
//         whileInView={{ opacity: 1, scale: 1 }}
//         className="mt-10 grid md:grid-cols-2 gap-8"
//       >
//         {/* API URL Input */}
//         <div>
//           <label className="text-gray-200 text-sm mb-2 block">API URL</label>
//           <input
//             type="text"
//             value={apiUrl}
//             onChange={(e) => setApiUrl(e.target.value)}
//             placeholder="https://api.example.com/login"
//             className="w-full p-3 rounded-lg bg-gray-900 border border-gray-700 text-gray-200 placeholder-gray-500"
//           />
//         </div>

//         {/* Code Textarea */}
//         <div>
//           <label className="text-gray-200 text-sm mb-2 block">Paste Code</label>
//           <textarea
//             rows={6}
//             value={code}
//             onChange={(e) => setCode(e.target.value)}
//             placeholder={`fetch("https://example.com/login", {
//   method: "POST",
//   body: "username=admin'--&password=123"
// });`}
//             className="w-full p-3 rounded-lg bg-gray-900 border border-gray-700 text-gray-200 font-mono placeholder-gray-500"
//           />
//         </div>

//         {/* Scan Button */}
//         <div className="md:col-span-2 text-center">
//           <button
//             type="submit"
//             disabled={loading}
//             className="px-10 py-3 bg-cyan-500 hover:bg-cyan-600 rounded-lg font-semibold"
//           >
//             {loading ? "Scanning..." : "🔍 Scan Now"}
//           </button>
//         </div>
//       </motion.form>

//       {/* Scan Result */}
//       {result && (
//         <motion.div
//           initial={{ opacity: 0 }}
//           whileInView={{ opacity: 1 }}
//           className="mt-8 text-center bg-gray-800 border border-gray-700 p-4 rounded-lg text-gray-200 overflow-x-auto font-mono"
//         >
//           <pre>{result}</pre>
//         </motion.div>
//       )}
//     </section>
//   );
// }





// THIS IS RUNNING FOR OUTPUT IN HUMANREADBALE FORM


"use client";

import { useState } from "react";
import { motion } from "framer-motion";

export default function ScannerForm() {
  const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

  const [apiUrl, setApiUrl] = useState("");
  const [code, setCode] = useState("");
  const [result, setResult] = useState("");
  const [loading, setLoading] = useState(false);

  /** Format JSON result into human‑readable output */
  const formatScanResult = (data) => {
    if (!data || !data.vulnerabilities) return "No vulnerabilities found.";

    let output = `Scan Result for file: ${data.filename}\n`;
    output += `Language: ${data.language}\n\n`;

    data.vulnerabilities.forEach((vuln, index) => {
      output += `${index + 1}) Vulnerability\n`;
      output += `   Type: ${vuln.type}\n`;
      output += `   Severity: ${vuln.severity}\n`;
      output += `   Line: ${vuln.line}\n`;
      output += `   Reason: ${vuln.reason}\n`;
      output += `   Fix: ${vuln.suggestion}\n\n`;
    });

    return output;
  };

  const handleScan = async (e) => {
    e.preventDefault();
    if (!apiUrl.trim() && !code.trim()) {
      return alert("Please enter an API URL or paste code to scan.");
    }

    setLoading(true);
    setResult("Scanning for vulnerabilities...");

    try {
      const userInput = code.trim() !== "" ? code : apiUrl;
      const payload = { code: userInput, language: "" };

      const response = await fetch(`${BACKEND_URL}/api/scan/raw`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(`Backend error: ${text}`);
      }

      const data = await response.json();
      setResult(formatScanResult(data));
    } catch (err) {
      console.error(err);
      setResult(
        "Error connecting to backend. Ensure Spring Boot is running and NEXT_PUBLIC_BACKEND_URL is correct."
      );
    }

    setLoading(false);
  };

// Download PDF
  const downloadPDF = () => {
    if (!result || !result.raw) return;
    const doc = new jsPDF();
    let y = 10;
    doc.setFontSize(14);
    doc.text(`Scan Result for file: ${result.raw.filename}`, 10, y);
    y += 10;
    doc.setFontSize(12);
    doc.text(`Language: ${result.raw.language}`, 10, y);
    y += 10;

    result.formatted.forEach((v) => {
      doc.text(`${v.index}) ${v.type} - ${v.severity}`, 10, y);
      y += 7;
      doc.text(`Line: ${v.line}`, 10, y);
      y += 7;
      doc.text(`Reason: ${v.reason}`, 10, y);
      y += 7;
      doc.text(`Fix: ${v.suggestion}`, 10, y);
      y += 10;
    });

    doc.save(`${result.raw.filename}-scan-report.pdf`);
  
  };

  return (
    <section id="scanner" className="py-20 max-w-6xl mx-auto px-6">
      <motion.h2
        initial={{ opacity: 0, y: -20 }}
        whileInView={{ opacity: 1, y: 0 }}
        className="text-3xl font-bold text-center text-cyan-400"
      >
        Vulnerability Scanner
      </motion.h2>

      <p className="text-center text-gray-300 mt-2">
        Enter your API endpoint or paste source code to detect risks like
        <span className="text-red-400"> SQL Injection</span>,
        <span className="text-yellow-300"> XSS</span>,
        <span className="text-blue-300"> CSRF</span> & more.
      </p>

      <motion.form
        onSubmit={handleScan}
        initial={{ opacity: 0, scale: 0.95 }}
        whileInView={{ opacity: 1, scale: 1 }}
        className="mt-10 grid md:grid-cols-2 gap-8"
      >
        {/* API URL Input */}
        <div>
          <label className="text-gray-200 text-sm mb-2 block">API URL</label>
          <input
            type="text"
            value={apiUrl}
            onChange={(e) => {
              setApiUrl(e.target.value);
              if (e.target.value) setCode(""); // auto clear code
            }}
            placeholder="https://api.example.com/login"
            className="w-full p-3 rounded-lg bg-gray-900 border border-gray-700 text-gray-200 placeholder-gray-500"
          />
        </div>

        {/* Code Textarea */}
        <div>
          <label className="text-gray-200 text-sm mb-2 block">Paste Code</label>
          <textarea
            rows={6}
            value={code}
            onChange={(e) => {
              setCode(e.target.value);
              if (e.target.value) setApiUrl(""); // auto clear URL
            }}
            placeholder={`fetch("https://example.com/login", {
  method: "POST",
  body: "username=admin'--&password=123"
});`}
            className="w-full p-3 rounded-lg bg-gray-900 border border-gray-700 text-gray-200 font-mono placeholder-gray-500"
          />
        </div>

        {/* Scan Button */}
        <div className="md:col-span-2 text-center">
          <button
            type="submit"
            disabled={loading}
            className="px-10 py-3 bg-cyan-500 hover:bg-cyan-600 rounded-lg font-semibold"
          >
            {loading ? "Scanning..." : "Scan Now"}
          </button>
        </div>
      </motion.form>

      {/* Scan Result */}
      {result && (
        <motion.div
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          className="mt-8 text-center bg-gray-800 border border-gray-700 p-4 rounded-lg text-gray-200 overflow-x-auto font-mono text-left"
        >
          <pre>{result}</pre>
        </motion.div>
      )}
        
    </section>
  );
}





// IT HAS DOWNLOAD BUTTON ALSO WITH EASY LANGUAGE BUT IT HAVE PROBLEM OF SCANNER FORM CLASS(SOMETIME)

// "use client";

// import { useState } from "react";
// import { motion } from "framer-motion";
// import { jsPDF } from "jspdf";

// export default function ScannerForm() {
//   const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

//   const [apiUrl, setApiUrl] = useState("");
//   const [code, setCode] = useState("");
//   const [result, setResult] = useState(null);
//   const [loading, setLoading] = useState(false);

//   // Format JSON into human-readable bullet-style output
//   const formatScanResult = (data) => {
//     if (!data || !data.vulnerabilities || data.vulnerabilities.length === 0) {
//       return "No vulnerabilities found.";
//     }

//     return data.vulnerabilities.map((v, i) => ({
//       index: i + 1,
//       type: v.type,
//       severity: v.severity,
//       line: v.line,
//       reason: v.reason,
//       suggestion: v.suggestion,
//     }));
//   };

//   const handleScan = async (e) => {
//     e.preventDefault();
//     if (!apiUrl.trim() && !code.trim()) {
//       return alert("Please enter an API URL or paste code to scan.");
//     }

//     setLoading(true);
//     setResult(null);

//     try {
//       const userInput = code.trim() !== "" ? code : apiUrl;
//       const payload = { code: userInput, language: "" };

//       const response = await fetch(`${BACKEND_URL}/api/scan/raw`, {
//         method: "POST",
//         headers: { "Content-Type": "application/json" },
//         body: JSON.stringify(payload),
//       });

//       if (!response.ok) {
//         const text = await response.text();
//         throw new Error(`Backend error: ${text}`);
//       }

//       const data = await response.json();
//       setResult({ raw: data, formatted: formatScanResult(data) });
//     } catch (err) {
//       console.error(err);
//       setResult({
//         raw: null,
//         formatted: [
//           {
//             index: 1,
//             type: "ERROR",
//             severity: "High",
//             line: "-",
//             reason:
//               "Error connecting to backend. Ensure Spring Boot is running and NEXT_PUBLIC_BACKEND_URL is correct.",
//             suggestion: "",
//           },
//         ],
//       });
//     }

//     setLoading(false);
//   };

//   // Download PDF
//   const downloadPDF = () => {
//     if (!result || !result.raw) return;
//     const doc = new jsPDF();
//     let y = 10;
//     doc.setFontSize(14);
//     doc.text(`Scan Result for file: ${result.raw.filename}`, 10, y);
//     y += 10;
//     doc.setFontSize(12);
//     doc.text(`Language: ${result.raw.language}`, 10, y);
//     y += 10;

//     result.formatted.forEach((v) => {
//       doc.text(`${v.index}) ${v.type} - ${v.severity}`, 10, y);
//       y += 7;
//       doc.text(`Line: ${v.line}`, 10, y);
//       y += 7;
//       doc.text(`Reason: ${v.reason}`, 10, y);
//       y += 7;
//       doc.text(`Fix: ${v.suggestion}`, 10, y);
//       y += 10;
//     });

//     doc.save(`${result.raw.filename}-scan-report.pdf`);
//   };

//   // Download TXT
//   const downloadTXT = () => {
//     if (!result || !result.raw) return;
//     let text = `Scan Result for file: ${result.raw.filename}\nLanguage: ${result.raw.language}\n\n`;
//     result.formatted.forEach((v) => {
//       text += `${v.index}) ${v.type} - ${v.severity}\n`;
//       text += `Line: ${v.line}\n`;
//       text += `Reason: ${v.reason}\n`;
//       text += `Fix: ${v.suggestion}\n\n`;
//     });

//     const blob = new Blob([text], { type: "text/plain" });
//     const link = document.createElement("a");
//     link.href = URL.createObjectURL(blob);
//     link.download = `${result.raw.filename}-scan-report.txt`;
//     link.click();
//   };

//   return (
//     <section id="scanner" className="py-20 max-w-6xl mx-auto px-6">
//       <motion.h2
//         initial={{ opacity: 0, y: -20 }}
//         whileInView={{ opacity: 1, y: 0 }}
//         className="text-3xl font-bold text-center text-cyan-400"
//       >
//         Vulnerability Scanner
//       </motion.h2>

//       <p className="text-center text-gray-300 mt-2">
//         Enter your API endpoint or paste your source code to detect risks like{" "}
//         <span className="text-red-400">SQL Injection</span>,{" "}
//         <span className="text-yellow-300">XSS</span>,{" "}
//         <span className="text-blue-300">CSRF</span> & more.
//       </p>

//       <motion.form
//         onSubmit={handleScan}
//         initial={{ opacity: 0, scale: 0.95 }}
//         whileInView={{ opacity: 1, scale: 1 }}
//         className="mt-10 grid md:grid-cols-2 gap-8"
//       >
//         {/* API URL Input */}
//         <div>
//           <label className="text-gray-200 text-sm mb-2 block">API URL</label>
//           <input
//             type="text"
//             value={apiUrl}
//             onChange={(e) => {
//               setApiUrl(e.target.value);
//               if (e.target.value) setCode("");
//             }}
//             placeholder="https://api.example.com/login"
//             className="w-full p-3 rounded-lg bg-gray-900 border border-gray-700 text-gray-200 placeholder-gray-500"
//           />
//         </div>

//         {/* Code Textarea */}
//         <div>
//           <label className="text-gray-200 text-sm mb-2 block">Paste Code</label>
//           <textarea
//             rows={6}
//             value={code}
//             onChange={(e) => {
//               setCode(e.target.value);
//               if (e.target.value) setApiUrl("");
//             }}
//             placeholder={`fetch("https://example.com/login", {
//   method: "POST",
//   body: "username=admin'--&password=123"
// });`}
//             className="w-full p-3 rounded-lg bg-gray-900 border border-gray-700 text-gray-200 font-mono placeholder-gray-500"
//           />
//         </div>

//         {/* Scan Button */}
//         <div className="md:col-span-2 text-center">
//           <button
//             type="submit"
//             disabled={loading}
//             className="px-10 py-3 bg-cyan-500 hover:bg-cyan-600 rounded-lg font-semibold"
//           >
//             {loading ? "Scanning..." : "Scan Now"}
//           </button>
//         </div>
//       </motion.form>

//       {/* Scan Result */}
//       {result && (
//         <motion.div
//           initial={{ opacity: 0 }}
//           whileInView={{ opacity: 1 }}
//           className="mt-8 bg-gray-800 border border-gray-700 p-4 rounded-lg overflow-x-auto text-left"
//         >
//           {result.raw && (
//             <h3 className="font-bold mb-2">Scan Result for file: {result.raw.filename}</h3>
//           )}
//           {result.raw && <p className="mb-4">Language: {result.raw.language}</p>}

//           {result.formatted.map((v) => {
//             let severityColor = "text-green-400";
//             if (v.severity.toLowerCase() === "high" || v.severity.toLowerCase() === "critical") {
//               severityColor = "text-red-500 font-bold";
//             } else if (v.severity.toLowerCase() === "medium") {
//               severityColor = "text-yellow-400 font-semibold";
//             }
//             return (
//               <div key={v.index} className="border-b border-gray-700 py-2">
//                 <p className={severityColor}>
//                   {v.index}) {v.type} - {v.severity}
//                 </p>
//                 <p>Line: {v.line}</p>
//                 <p>Reason: {v.reason}</p>
//                 <p>Fix: {v.suggestion}</p>
//               </div>
//             );
//           })}

//           {/* Download Buttons */}
//           {result.raw && (
//             <div className="mt-4 flex gap-4">
//               <button
//                 onClick={downloadPDF}
//                 className="px-6 py-2 bg-blue-500 hover:bg-blue-600 rounded-lg"
//               >
//                 Download PDF
//               </button>
//               <button
//                 onClick={downloadTXT}
//                 className="px-6 py-2 bg-gray-600 hover:bg-gray-700 rounded-lg"
//               >
//                 Download TXT
//               </button>
//             </div>
//           )}
//         </motion.div>
//       )}
//     </section>
//   );
// }
