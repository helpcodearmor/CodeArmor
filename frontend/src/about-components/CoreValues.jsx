// "use client";
// import { Github, Linkedin } from "lucide-react";
// import { motion } from "framer-motion";

// const teamMembers = [
//   {
//     name: "Aniket Gupta",
//     role: "Frontend Developer",
//     image: "/team/aniket.jpg", 
//     desc: "Specializes in SQL Injection & Web App Vulnerability Analysis. Passionate about building automated threat detection systems.",
//     linkedin: "https://linkedin.com/",
//     github: "https://github.com/",
//   },
//   {
//     name: "Avnish Mishra",
//     role: "Backend Developer",
//     image: "/team/rahul.jpg",
//     desc: "Expert in backend systems & secure API development, ensuring smooth and safe communication between scanner and servers.",
//     linkedin: "https://linkedin.com/",
//     github: "https://github.com/",
//   },
// ];

// export default function TeamSection() {
//   return (
//     <section className="py-20 bg-gradient-to-b from-black to-cyan-950 text-white" id="team">
//       <div className="max-w-7xl mx-auto px-6">
//         {/* Section Title */}
//         <motion.div
//           initial={{ opacity: 0, y: 30 }}
//           whileInView={{ opacity: 1, y: 0 }}
//           transition={{ duration: 0.6 }}
//           className="text-center"
//         >
//           <h2 className="text-4xl font-bold mb-4 tracking-wide">
//             Meet Our Cyber Security Experts
//           </h2>
//           <p className="text-gray-300 max-w-2xl mx-auto">
//             Our dedicated team ensures powerful vulnerability detection including
//             SQLi, XSS, Broken Authentication & more.
//           </p>
//         </motion.div>

//         {/* Team Members */}
//         <div className="grid grid-cols-1 md:grid-cols-2 gap-10 mt-14">
//           {teamMembers.map((member, index) => (
//             <motion.div
//               key={index}
//               initial={{ opacity: 0, scale: 0.9 }}
//               whileInView={{ opacity: 1, scale: 1 }}
//               transition={{ duration: 0.6 }}
//               whileHover={{ scale: 1.03 }}
//               className="bg-gray-900/60 border border-cyan-400/20 rounded-2xl p-8 text-center shadow-lg shadow-cyan-700/20"
//             >
//               <img
//                 src={member.image}
//                 alt={member.name}
//                 className="w-28 h-28 mx-auto rounded-full border-2 border-cyan-400 object-cover"
//               />

//               <h3 className="mt-4 text-2xl font-semibold">{member.name}</h3>
//               <p className="text-cyan-400 text-sm mt-1">{member.role}</p>

//               <p className="text-gray-300 text-sm mt-4 leading-relaxed">
//                 {member.desc}
//               </p>

//               {/* Social Icons */}
//               <div className="flex justify-center gap-6 mt-6">
//                 <a href={member.linkedin} target="_blank">
//                   <Linkedin className="w-6 h-6 hover:text-cyan-400 transition" />
//                 </a>
//                 <a href={member.github} target="_blank">
//                   <Github className="w-6 h-6 hover:text-cyan-400 transition" />
//                 </a>
//               </div>
//             </motion.div>
//           ))}
//         </div>
//       </div>
//     </section>
//   );
// }






"use client";
import { motion } from "framer-motion";
import { Fingerprint, Shield, Zap, Eye } from "lucide-react";

const values = [
  {
    icon: <Shield className="w-10 h-10 text-cyan-400" />,
    title: "Trust & Protection",
    desc: "Security is not optional. CodeArmor is designed to ensure your digital assets remain protected from modern cyber threats.",
  },
  {
    icon: <Eye className="w-10 h-10 text-purple-400" />,
    title: "Transparency",
    desc: "Every vulnerability scan, risk score, and security insight is shared transparently so developers can take informed action.",
  },
  {
    icon: <Zap className="w-10 h-10 text-pink-400" />,
    title: "Speed & Performance",
    desc: "CodeArmor’s optimized engine performs deep scans in seconds, providing fast and accurate results without slowing systems.",
  },
  {
    icon: <Fingerprint className="w-10 h-10 text-blue-400" />,
    title: "Innovation",
    desc: "We continuously innovate using AI & automation to stay ahead of cyber attackers in a rapidly evolving digital world.",
  },
];

export default function CoreValuesSection() {
  return (
    <section
      id="values"
      className="py-20 bg-gradient-to-b from-[#020617] to-[#0a0f24] text-white"
    >
      <div className="max-w-7xl mx-auto px-6">

        {/* Heading */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="text-center"
        >
          <h2 className="text-4xl font-bold mb-4 tracking-wide">
            Our Core Values
          </h2>
          <p className="text-gray-300 max-w-2xl mx-auto">
            The principles that guide CodeArmor in building reliable, powerful,
            and user-focused cyber-security solutions.
          </p>
        </motion.div>

        {/* Value Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-10 mt-14">
          {values.map((item, idx) => (
            <motion.div
              key={idx}
              initial={{ opacity: 0, scale: 0.9 }}
              whileInView={{ opacity: 1, scale: 1 }}
              transition={{ duration: 0.5 }}
              whileHover={{ scale: 1.05 }}
              className="bg-[#0d152e]/60 border border-cyan-400/20 p-8 rounded-2xl text-center shadow-lg shadow-cyan-700/10"
            >
              <div className="flex justify-center mb-4">{item.icon}</div>
              <h3 className="text-xl font-semibold mb-2">{item.title}</h3>
              <p className="text-gray-300 text-sm leading-relaxed">
                {item.desc}
              </p>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}
