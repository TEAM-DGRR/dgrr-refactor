'use client';
import axios from 'axios';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';

const LogoutPage = () => {
  const router = useRouter();

  useEffect(() => {
    delete axios.defaults.headers.common["Authorization"];
    localStorage.removeItem("token");
    router.push("/");
  });
  return <div>로그아웃 대기 중...</div>;
};

export default LogoutPage;
