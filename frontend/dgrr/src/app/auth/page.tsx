'use client';
import axios from 'axios';
import { setUrl } from '../../utils/setUrl';
import { useSearchParams, useRouter } from 'next/navigation';
import { useEffect } from 'react';

const AuthPage = () => {
  const searchParams = useSearchParams();
  const router = useRouter();

  const onClick = () => {
    let code = searchParams.get('code');
    const kakaoLogin = async () => {
      await axios.get(`${setUrl}/kakao/kakaoCallback?code=${code}`).then((res) => {
        // console.log(res.data); // 디버그용
        if (res.data.key === 'signUp') {
          router.push(`/signup?id=${res.data.id}`);
        } else {
          const member = res.data.member;
          alert(`${member.nickname}님, 환영합니다.`);
          axios
            .get(`${setUrl}/kakao/login/${member.kakaoId}`)
            .then((res: any) => {
              localStorage.setItem('token', res.data.token);
              axios.defaults.headers.common['Authorization'] = `${res.data.token}`;

              // console.log("res.data: " + JSON.stringify(res.data)); // 디버그용
              router.push(`/main`);
            })
            .catch((err: any) => {
              console.log(err);
            });
        }
      });
    };
    kakaoLogin();
  };
  useEffect(() => {
    onClick();
  }, [searchParams]);
  return <div>로그인 중...</div>;
};

export default AuthPage;
