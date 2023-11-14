/** @type {import('next').NextConfig} */
const nextConfig = { reactStrictMode: false };

module.exports = {
  nextConfig,
  swcMinify: true,
  compiler: {
    styledComponents: true,
  },
};
