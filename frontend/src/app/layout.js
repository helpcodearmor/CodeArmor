import "./globals.css";

export const metadata = {
  title: "CodeArmor",
  description: "Created by Aniket, Avnish",

  // ✅ Add favicon here
  icons: {
    icon: "/CodeArmorLogo.svg",        // You can replace with /logo.png or any image
    shortcut: "/CodeArmorLogo.svg",
    apple: "/CodeArmorLogo.svg",       // Optional for iOS
  },
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
