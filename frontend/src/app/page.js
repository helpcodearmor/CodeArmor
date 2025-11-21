import Navbar from "@/components/Navbar";
import HeroSection from "@/components/HeroSection";
import ScannerForm from "@/components/ScannerForm";
import Features from "@/components/Features";
import SqlInjection from "@/components/ThreatSections/SqlInjection";
import XssAttack from "@/components/ThreatSections/XssAttack";
import CsrfAttack from "@/components/ThreatSections/CsrfAttack";
import DependencyVuln from "@/components/ThreatSections/DependencyVuln";
import Footer from "@/components/Footer";

export default function HomePage() {
  return (
    <>
      <Navbar />
      <HeroSection />
      <ScannerForm />
      <Features />
      <SqlInjection />
      <XssAttack />
      <CsrfAttack />
      <DependencyVuln />
      <Footer />
    </>
  );
}
