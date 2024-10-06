import type { Config } from 'tailwindcss';
import typography from '@tailwindcss/typography';

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
      typography: ({ theme }) => ({
        DEFAULT: {
          css: {
            maxWidth: '65ch',
            'h1, h2, h3, h4': {
              'border-bottom': '1px solid #e5e7eb',
              'padding-bottom': '0.5rem',
              'margin-bottom': '1rem',
            },
            'code::before': {
              content: '""',
            },
            'code::after': {
              content: '""',
            },
            code: {
              // backgroundColor: theme('colors.gray.100'),
              // color: theme('colors.gray.400'),
              fontWeight: '400',
              'border-radius': '0.25rem',
              padding: '0.25rem 0.5rem',
            },
            pre: {
              // backgroundColor: theme('colors.gray.800'),
              // color: theme('colors.gray.100'),
              'border-radius': '0.5rem',
              padding: '1rem',
            },
            'pre code': {
              // backgroundColor: 'transparent',
              color: 'inherit',
              fontSize: '0.875rem',
              lineHeight: '1.7142857',
            },
          },
        },
      }),
    },
  },
  plugins: [typography],
};
export default config;
