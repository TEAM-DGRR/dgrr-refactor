const CLIENT_ID = process.env.NEXT_PUBLIC_API_KEY;
const REDIRECT_URI = process.env.NEXT_PUBLIC_REDIRECT_URI;
const REDIRECT_LOGOUT_URI = process.env.NEXT_PUBLIC_REDIRECT_LOGOUT_URI;
export const KAKAO_AUTH_URL = `https://kauth.kakao.com/oauth/authorize?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code`;
export const KAKAO_LOGOUT_REDIRECT_URL = `https://kauth.kakao.com/oauth/logout?client_id=${CLIENT_ID}&logout_redirect_uri=${REDIRECT_LOGOUT_URI}`