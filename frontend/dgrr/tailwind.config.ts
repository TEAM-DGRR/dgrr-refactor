import type { Config } from 'tailwindcss';

const config: Config = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
        'gradient-conic': 'conic-gradient(from 180deg at 50% 50%, var(--tw-gradient-stops))',
      },
      colors: {
        'start-yellow': '#FFF401',
        'end-yellow': '#FFD873',
        'main-blue': '#469FF6',
      },
      screens: {
        '4sm': '280px',
        '3sm': '360px',
        '2sm': '395px',
        sm: '640px',
        md: '768px',
        lg: '1024px',
        xl: '1280px',
        '2xl': '1536px',
      },
      cursor: {
        'default': `url(/images/cursor_default.cur), auto;`,
        'hover': `url(/images/cursor_hover.cur), auto;`,
      },
      keyframes: {
        blink: {
          from: {
            display:'none',
          },
          to: {
            display: 'flow'
          }
        },
        slideDown: {
          from: {
            transform: 'translateY(-100%)',
          },
          to: {
            transform: 'translateY(0)',
          },
        }
      },
      animation: {
        blink1: 'blink 1s ease-in-out infinite',
        blink2: 'blink 2s ease-in-out infinite',
        blink: 'blink 1s ease-in-out infinite',
        slideDown: 'slideDown 3s ease-in-out',
      }
    },
  },
  plugins: [],
};
export default config;
