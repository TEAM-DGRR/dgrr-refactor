'use client';
import character from '@/../public/images/logo_character.png';
import title from '@/../public/images/logo_title.png';
import { FuncButton } from '@/components/FuncButton';
import { LinkButton } from '@/components/LinkButton';
import ButtonClickAudio from '@/components/audio/ButtonClickAudio';
import { ExplainModal } from '@/components/elements/ExplainModal';
import Header from '@/components/elements/Header';
import Image from 'next/image';
import { useEffect, useState } from 'react';

const MainPage = () => {
  const [openModal, setOpenModal] = useState(false);

  const playSound = ButtonClickAudio();
  const handleModal = () => {
    playSound();
    setOpenModal(!openModal);
  };

  useEffect(() => {
    // 카메라 권한 요청
    const requestCameraPermission = async () => {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
        // 사용 후 스트림을 닫음
        stream.getTracks().forEach((track) => track.stop());
      } catch (error) {
        console.error('카메라 접근 권한 요청 실패:', error);
      }
    };

    requestCameraPermission();
  }, []);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      const parseJwt = (token: any) => {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace('-', '+').replace('_', '/');
        return JSON.parse(window.atob(base64));
      };
      const id = parseJwt(token).id;
      localStorage.setItem('memberId', id);
    }
  }, []);
  return (
    <div className='bg-main-blue w-screen h-screen min-h-[580px] max-w-[500px]'>
      <Header headerType='MAIN' />
      {openModal && <ExplainModal onClose={handleModal} />}

      <div className='flex flex-col justify-between h-5/6 pt-10'>
        <div>
          {/* 식빵이 이미지 */}
          <div className='flex justify-center mb-5'>
            <Image alt='캐릭터' src={character} className='w-40 h-40 hover:animate-spin' />
          </div>
          {/* 데구르르 로고 */}
          <div className='flex justify-center 2sm:px-20 px-14'>
            <Image alt='타이틀' src={title} className='ms-3' />
          </div>
        </div>
        <div className='space-y-6'>
          {/* 게임 설명 버튼 */}
          <div className='flex justify-center'>
            <FuncButton value='게임 설명' clickEvent={handleModal} />
          </div>
          {/* 게임 시작 버튼 */}
          <div className='flex justify-center'>
            <div className='w-4/5 2sm:w-[320px]'>
              <LinkButton value='게임 시작' moveLink='/game/list' clickEvent={playSound} />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MainPage;
