// src/app/404.page.tsx
'use client';
import React from 'react';
import Image from 'next/image';
import { LinkButton } from '@/components/LinkButton';

const NotFound = () => {
  return (
    <div
      className='bg-main-blue w-screen h-full max-w-[500px]'
      style={{ padding: '100px', textAlign: 'center' }}
    >
      <div className='font-bold text-5xl flex justify-center text-end-yellow my-10'>Not Found</div>
      <div className='flex justify-center px-3'>
        <Image src='/images/fix_bread.png' width={200} height={200} alt='not found 빵' />
      </div>
      <div className='font-bold'>
        <p>알 수 없는 경로입니다.</p>
        <p>돌아가서 게임을 즐겨주세요!</p>
      </div>
      <div className='flex justify-center my-10'>
        <div className='w-4/5 2sm:w-[320px]'>
          <LinkButton value='메인으로' moveLink='/' />
        </div>
      </div>
    </div>
  );
};

export default NotFound;
