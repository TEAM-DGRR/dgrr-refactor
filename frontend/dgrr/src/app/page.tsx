'use client';
import { KAKAO_AUTH_URL } from '../metadata/OAuth';

export default function Home() {
  const handleLogin = () => {
    window.location.href = KAKAO_AUTH_URL;
  };

  return (
    <div className="bg-main-blue w-screen h-screen max-w-[500px]">
      <div className="flex flex-col justify-between py-36 h-full">
        <div className="px-3">
          <img src="/images/login_logo.png" alt="로그인 전 화면에서 사용할 로고" />
        </div>
        <div className="px-4" onClick={handleLogin}>
          <img
            src="/images/kakao_login.png"
            alt="카카오 로그인 버튼"
            className="cursor-hover hover:brightness-110"
          />
        </div>
      </div>
    </div>
  );
}
