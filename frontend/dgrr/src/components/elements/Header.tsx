'use client';

import React, { useState } from 'react';
import { usePathname } from 'next/navigation';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import {
  IoTrophyOutline,
  IoPersonOutline,
  IoChevronBackOutline,
  IoCopyOutline,
  IoExitOutline,
  IoPencilSharp,
} from 'react-icons/io5';

export type headerType = 'MAIN' | 'GAMESTART' | 'PROFILE' | 'WAITING' | 'GAME' | 'OTHER';

interface HeaderProps {
  headerType: headerType;
  // WAITING에서만 roomCode 필요(roomCode 형식은 number)
  roomCode?: number;
  // OTHER에서만 페이지 제목에 해당하는 children 필요
  children?: React.ReactNode;
}

const Header = ({ headerType, roomCode, children }: HeaderProps) => {
  const router = useRouter();
  const pathname = usePathname();

  // 뒤로 가기
  const handleMoveBack = () => {
    router.back();
    history.pushState({}, '', pathname);
    console.log('Go Back');
  };

  // 코드 복사
  const handleCopyCode = async (roomCode: number | undefined) => {
    if (roomCode !== undefined) {
      try {
        // 숫자를 문자열로 변환 후 클립보드에 복사
        await navigator.clipboard.writeText(roomCode.toString());
        console.log(`${roomCode} 복사 성공`);
      } catch (error) {
        console.log('복사 실패😥');
      }
    }
  };

  return (
    // MAIN : 메인 화면에서의 헤더(랭킹 버튼, 마이 프로필 버튼)
    // GAMESTART : 게임 시작에서의 헤더(뒤로 가기 버튼)
    // PROFILE : 마이 프로필에서의 헤더(뒤로 가기 버튼, 수정하기 버튼)
    // WAITING : 대기실에서의 헤더(클립보드 복사 버튼, 나가기 버튼)
    // GAME : 게임 화면에서의 헤더(시간, 나가기 버튼)
    // OTHER : 랭킹 조회, 프로필 수정, 최근 전적에서의 헤더(뒤로 가기 버튼)
    <div className='max-w-[500px]'>
      {headerType === 'MAIN' ? (
        <div className='bg-main-blue h-[60px] top-0 right-0 flex justify-between items-center'>
          <div className='flex ms-4 cursor-pointer hover:text-white'>
            <IoTrophyOutline fontSize={'27px'} />
          </div>
          <div className='me-4 cursor-pointer hover:text-white'>
            <Link href='/myprofile'>
              <IoPersonOutline fontSize={'27px'} />
            </Link>
          </div>
        </div>
      ) : headerType === 'GAMESTART' ? (
        <div className='h-[60px] top-0 right-0 flex items-center'>
          <div className='flex gap-2 ms-2'>
            <div onClick={handleMoveBack} className='cursor-pointer hover:text-white'>
              <IoChevronBackOutline fontSize={'27px'} />
            </div>
          </div>
        </div>
      ) : headerType === 'PROFILE' ? (
        <div className='h-[60px] top-0 right-0 flex justify-between items-center'>
          <div className='flex gap-2 ms-2'>
            <div onClick={handleMoveBack} className='cursor-pointer hover:text-main-blue'>
              <IoChevronBackOutline fontSize={'27px'} />
            </div>
            <div className='inline-block text-lg font-semibold'>마이 프로필</div>
            </div>
            <Link href='/myprofile/edit'>
              <div className='me-4 hover:text-main-blue'>
                <IoPencilSharp fontSize={'25px'} />
              </div>
            </Link>
        </div>
      ) : headerType === 'WAITING' ? (
        <div className='bg-black h-[60px] top-0 right-0 gap-2 pe-4 flex justify-end items-center'>
          <div
            onClick={() => handleCopyCode(roomCode)}
            className='cursor-pointer text-white hover:text-main-blue'
          >
            <IoCopyOutline fontSize={'27px'} />
          </div>
          <div className='cursor-pointer text-white hover:text-main-blue'>
            <IoExitOutline fontSize={'30px'} />
          </div>
        </div>
      ) : headerType === 'GAME' ? (
        <div className='bg-black h-[60px] top-0 right-0 gap-[107px] pe-4 flex justify-end items-center'>
          <div className='text-white text-3xl font-semibold'>0:11</div>
          <div className='cursor-pointer text-white hover:text-main-blue'>
            <IoExitOutline fontSize={'30px'} />
          </div>
        </div>
      ) : (
        <div className='h-[60px] top-0 right-0 flex items-center'>
          <div className='flex gap-2 ms-2'>
            <div onClick={handleMoveBack} className='cursor-pointer hover:text-main-blue'>
              <IoChevronBackOutline fontSize={'27px'} />
            </div>
            <div className='inline-block text-lg font-semibold'>{children}</div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Header;
