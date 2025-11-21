// "use client";
// import { useState } from "react";
// import Link from "next/link";
// import { motion } from "framer-motion";
// import { Menu, X } from "lucide-react";

// export default function Navbar() {
//   const [open, setOpen] = useState(false);

//   const navItems = [
//     { name: "Home", href: "/" },
//     { name: "About", href: "/About" },
//     // { name: "Scan History", href: "/History" },
//     // { name: "Login", href: "/Login" },
//   ];


//   return (
//     <motion.nav
//       initial={{ y: -40, opacity: 0 }}
//       animate={{ y: 0, opacity: 1 }}
//       transition={{ duration: 0.6 }}
//       className="fixed top-0 left-0 w-full z-50 bg-gray-900/70 backdrop-blur-lg border-b border-cyan-500/20 shadow-lg"
//     >
//       <div className="flex justify-between items-center px-6 md:px-12 py-3">
//         <Link
//           href="/"
//           className="text-2xl font-extrabold bg-gradient-to-r from-cyan-400 to-blue-600 bg-clip-text text-transparent"
//         >
//           Web-CodeArmor
//         </Link>

//         <div className="hidden md:flex gap-8 text-lg">
//           {navItems.map((i) => (
//             <Link key={i.href} href={i.href} className="relative group">
//               {i.name}
//               <span className="absolute left-0 -bottom-1 w-0 h-[2px] bg-cyan-400 transition-all duration-300 group-hover:w-full"></span>
//             </Link>
//           ))}
//         </div>

//         <button className="md:hidden text-cyan-400" onClick={() => setOpen(!open)}>
//           {open ? <X size={28} /> : <Menu size={28} />}
//         </button>
//       </div>

//       {open && (
//         <motion.div
//           initial={{ opacity: 0 }}
//           animate={{ opacity: 1 }}
//           className="md:hidden flex flex-col bg-gray-900/90 px-6 pb-4 border-b border-cyan-500/10"
//         >
//           {navItems.map((i) => (
//             <Link key={i.href} href={i.href} className="py-3" onClick={() => setOpen(false)}>
//               {i.name}
//             </Link>
//           ))}
//         </motion.div>
//       )}
//     </motion.nav>
//   );
// }






"use client";
import { useState, useCallback, useMemo } from "react";
import Link from "next/link";
import Image from "next/image";
import { usePathname } from "next/navigation";
import { Menu, X } from "lucide-react";

export default function Navbar() {
  const [open, setOpen] = useState(false);
  const pathname = usePathname();

  const navItems = useMemo(
    () => [
      { name: "Home", href: "/" },
      { name: "About", href: "/About" },
    ],
    []
  );

  const toggleMenu = useCallback(() => {
    setOpen((prev) => !prev);
  }, []);

  return (
    <nav className="fixed top-0 left-0 w-full z-50 bg-black/25 backdrop-blur-xl border-b border-white/10 shadow-lg">
      <div className="max-w-7xl mx-auto px-8 py-3 flex justify-between items-center">
        {/* Logo + Brand */}
        <Link 
          href="/" 
          className="flex items-center gap-3 select-none group transition-transform hover:scale-105 duration-300 focus:outline-none"
        >
          {/* LOGO - Medium rounded, no border */}
          <div className="w-14 h-14 rounded-2xl overflow-hidden flex items-center justify-center bg-gradient-to-br from-black to-black shadow-lg shadow-indigo-500/50 group-hover:shadow-indigo-500/70 transition-shadow duration-300">
            <Image
              src="/CodeArmorLogo.svg"
              alt="CodeArmor Logo"
              width={44}
              height={44}
              className="object-contain"
              priority
            />
          </div>

         


          {/* BRAND NAME - Enhanced visibility */}
          <div className="flex flex-col justify-center">
            <h1 className="text-3xl font-extrabold leading-none">
              <span className="text-white">Code</span>
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 to-purple-400">
                Armor
              </span>
            </h1>
          </div>
        </Link>

        {/* Desktop Menu */}
        <div className="hidden md:flex gap-10">
          {navItems.map(({ name, href }) => {
            const isActive = pathname === href;
            return (
              <Link
                key={name}
                href={href}
                className={`transition text-lg font-medium relative group ${
                  isActive ? "text-indigo-400" : "text-white/85 hover:text-indigo-400"
                }`}
              >
                {name}
                <span 
                  className={`absolute -bottom-1 left-0 h-0.5 bg-indigo-400 transition-all duration-300 ${
                    isActive ? "w-full" : "w-0 group-hover:w-full"
                  }`}
                />
              </Link>
            );
          })}
        </div>

        {/* Mobile Toggle */}
        <button
          onClick={toggleMenu}
          className="md:hidden text-white active:scale-90 transition-transform focus:outline-none focus:ring-0"
          aria-label="Toggle menu"
        >
          {open ? <X size={30} /> : <Menu size={30} />}
        </button>
      </div>

      {/* Mobile Menu */}
      <div
        className={`md:hidden overflow-hidden transition-all duration-300 ${
          open ? "max-h-64 opacity-100" : "max-h-0 opacity-0"
        }`}
      >
        <div className="flex flex-col px-8 py-6 space-y-5 bg-black/40 backdrop-blur-xl border-t border-white/10">
          {navItems.map(({ name, href }) => {
            const isActive = pathname === href;
            return (
              <Link
                key={name}
                href={href}
                onClick={() => setOpen(false)}
                className={`text-lg font-medium transition ${
                  isActive ? "text-indigo-400" : "text-white/90 hover:text-indigo-400"
                }`}
              >
                {name}
              </Link>
            );
          })}
        </div>
      </div>
    </nav>
  );
}