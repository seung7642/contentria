// import createMDX from '@next/mdx';
import type { NextConfig } from 'next';

const nextConfig: NextConfig = {
  pageExtensions: ['js', 'jsx', 'md', 'mdx', 'ts', 'tsx'],
  images: {
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'lh3.googleusercontent.com',
        pathname: '/**',
      },
    ],
    // 주로 사용할 이미지 크기 설정
    imageSizes: [16, 32, 48, 96, 128, 256, 384],
    deviceSizes: [640, 750, 828, 1080, 1200, 1920, 2048, 3840],
  },

  // 헤더 설정 추가
  async headers() {
    return [
      {
        source: '/:path*',
        headers: [
          {
            key: 'Cross-Origin-Opener-Policy',
            value: 'same-origin-allow-popups',
          },
          {
            key: 'Cross-Origin-Embedder-Policy',
            value: 'credentialless',
          },
          {
            key: 'Content-Security-Policy',
            value: "frame-ancestors 'self' https://accounts.google.com",
          },
        ],
      },
    ];
  },

  async rewrites() {
    return [
      {
        source: '/@:blogSlug',
        destination: '/user/:blogSlug',
      },
      {
        source: '/@:blogSlug/:postSlug',
        destination: '/user/:blogSlug/posts/:postSlug',
      },
    ];
  },
};

// const withMDX = createMDX({});

// export default withMDX(nextConfig);
export default nextConfig;
