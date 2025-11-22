import "./globals.css";
import Head from "next/head";

export const metadata = {
  title: "CodeArmor",
  description: "Created by Aniket, Avnish",
  icons: {
    icon: "/CodeArmorlogo.svg?v=2",
    shortcut: "/CodeArmorlogo.svg",
    apple: "/CodeArmorlogo.svg",
  },
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <head>
        <link rel="icon" href="/CodeArmorlogo.svg?v=2" type="image/svg+xml" />
      </head>
      <body>{children}</body>
    </html>
  );
}

