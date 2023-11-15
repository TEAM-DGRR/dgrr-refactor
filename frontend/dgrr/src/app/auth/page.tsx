'use client';
import axios from 'axios';
import { setUrl } from '../../utils/setUrl';
import { useSearchParams, useRouter } from 'next/navigation';
import { useEffect } from 'react';
import Image from 'next/image';
import character from '@/../../public/images/floating_bread_cropped.gif';

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
  return (
    <div className='bg-main-blue w-screen h-screen max-w-[500px] pt-32'>
      <div className='flex flex-col justify-between'>
        <div className='flex justify-center mb-10'>
          <Image alt='캐릭터' src={character} />
        </div>
        <div className='flex justify-center font-bold text-xl text-white mt-5'>
          로그인 중
          <span className='animate-blink'>.</span>
          <span className='animate-blink2'>.</span>.<span className='animate-blink3'>.</span>
        </div>
      </div>
    </div>
  );
};

export default AuthPage;
