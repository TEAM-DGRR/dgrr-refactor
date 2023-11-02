'use client';
import character from '@/../public/images/logo_character.png';
import title from '@/../public/images/logo_title.png';
import Image from 'next/image';
import { LinkButton } from '@/components/LinkButton';
import { FuncButton } from '@/components/FuncButton';
import Header from '@/components/elements/Header';

const MainPage = () => {
  // 나중에 설명 모달 on/off 로직 추가할 예정
  const handleModal = () => {};
  return (
    <div className='bg-main-blue w-screen h-screen min-h-[580px] max-w-[360px]'>
      <Header headerType='MAIN' />
      <div className='flex flex-col justify-between h-5/6 pt-10'>
        <div>
          {/* 식빵이 이미지 */}
          <div className='flex justify-center mb-5'>
            <Image alt='캐릭터' src={character} className='w-40 h-40 hover:animate-spin' />
          </div>
          {/* 데구르르 로고 */}
          <div className='flex justify-center'>
            <Image alt='타이틀' src={title} className='w-56 ms-3' />
          </div>
        </div>
        <div className='space-y-6'>
          {/* 게임 설명 버튼 */}
          <div className='flex justify-center'>
            <FuncButton value='게임 설명' clickEvent={handleModal} />
          </div>
          {/* 게임 시작 버튼 */}
          <div className='flex justify-center'>
            <div className='w-4/5'>
              <LinkButton value='게임 시작' moveLink='game/list' />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MainPage;
