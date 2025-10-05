import nextPlugin from '@next/eslint-plugin-next';
import reactPlugin from 'eslint-plugin-react';
import hooksPlugin from 'eslint-plugin-react-hooks';
import ts from '@typescript-eslint/eslint-plugin';
import tsParser from '@typescript-eslint/parser';
import eslintPluginPrettierRecommended, { ignores } from 'eslint-plugin-prettier/recommended';

export default [
  {
    ignores: [
      'tailwind.config.ts',
      'next.config.ts',
      'node_modules/**',
      '.next/**',
      'out/**',
      'public/**',
      ...ignores,
    ],
  },
  {
    plugins: {
      react: reactPlugin,
    },
    rules: {
      ...reactPlugin.configs['jsx-runtime'].rules,
    },
    settings: {
      react: {
        version: 'detect', // You can add this if you get a warning about the React version when you lint.
      },
    },
  },
  {
    plugins: {
      'react-hooks': hooksPlugin,
    },
    rules: hooksPlugin.configs.recommended.rules,
  },
  {
    plugins: {
      '@next/next': nextPlugin,
    },
    rules: {
      ...nextPlugin.configs.recommended.rules,
      ...nextPlugin.configs['core-web-vitals'].rules,
    },
  },
  {
    files: ['**/*.{ts,tsx}'],
    languageOptions: {
      parser: tsParser,
      parserOptions: {
        ecmaFeatures: { modules: true },
        ecmaVersion: 'latest',
        project: './tsconfig.json',
      },
    },
    plugins: {
      '@typescript-eslint': ts,
    },
    rules: {
      ...ts.configs['eslint-recommended'].rules,
      ...ts.configs['recommended'].rules,
    },
  },
  eslintPluginPrettierRecommended, // eslint-config-prettier 기능을 포함한다.
  {
    files: ['**/*.d.ts'],
    rules: {
      '@typescript-eslint/no-unused-vars': 'off',
    },
  },
];
