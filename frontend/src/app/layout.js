import "./globals.css";

export const metadata = {
  title: "CodeArmor",
  description: "Created by Aniket, Avnish",
  icons: {
    icon: "/CodeArmorlogo.svg",
    shortcut: "/CodeArmorlogo.svg",
    apple: "/CodeArmorlogo.svg",
  },
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <head>
        <link rel="icon" href="/CodeArmorlogo.svg" type="image/svg+xml" />
      </head>
      <body>{children}</body>
    </html>
  );
}


