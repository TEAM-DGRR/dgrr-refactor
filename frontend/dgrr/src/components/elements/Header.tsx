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
import { useAppSelector } from '@/store/hooks';
import { publishMessage } from '../Game/stomp';
import { stompConfig } from '@/types/game';
import WarningAlert from './WarningAlert';
import ButtonClickAudio from '../audio/ButtonClickAudio';

export type headerType =
  | 'MAIN'
  | 'GAMESTART'
  | 'PROFILE'
  | 'WAITING'
  | 'GAME'
  | 'OTHER'
  | 'MATCHING';

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
  const client = useAppSelector((state) => state.game.client);
  const gameRoomId = useAppSelector((state) => state.game.gameInfo.gameRoomId);
  const ws = useAppSelector((state) => state.game.websocket);
  const { DESTINATION_URI } = stompConfig;
  const { EXIT_URI, EXIT_MATCHING } = DESTINATION_URI;
  const playsound = ButtonClickAudio();

  // 뒤로 가기
  const handleMoveBack = () => {
    playsound();
    router.back();
    history.pushState({}, '', pathname);
    console.log('Go Back');
  };

  const exitGame = async () => {
    playsound();
    const askExit = await WarningAlert();

    if (askExit) {
      if (client) {
        publishMessage(client, EXIT_URI, gameRoomId);
        client.deactivate();
        disconnectWs();
        router.push('/main');
      }
    }
  };
  
  const exitMatching = async () => {
    playsound();
    if (client) {
      //여기 밑에 수정
      publishMessage(client, EXIT_MATCHING, "");
      client.deactivate();
      disconnectWs();
      router.push('/game/list');
    }
  };

  // 웹소켓 해제
  const disconnectWs = () => {
    if (ws) {
      ws.close();
      console.log('연결 해제');
    }
  };

  // 코드 복사
  const handleCopyCode = async (roomCode: number | undefined) => {
    playsound();
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
    // MATCHING: 매칭 중에서의 헤더(뒤로 가기 버튼)
    // OTHER : 랭킹 조회, 프로필 수정, 최근 전적에서의 헤더(뒤로 가기 버튼)
    <div className='max-w-[500px]'>
      {headerType === 'MAIN' ? (
        <div className='bg-main-blue h-[60px] top-0 right-0 flex justify-between items-center'>
          <div className='flex ms-4 hover:text-white'>
            <Link href='/ranking' className='cursor-hover' onClick={playsound}>
              <IoTrophyOutline fontSize={'27px'} />
            </Link>
          </div>
          <div className='me-4 hover:text-white'>
            <Link href='/myprofile' className='cursor-hover' onClick={playsound}>
              <IoPersonOutline fontSize={'27px'} />
            </Link>
          </div>
        </div>
      ) : headerType === 'GAMESTART' ? (
        <div className='h-[60px] top-0 right-0 flex items-center'>
          <div className='flex gap-2 ms-2'>
            <div onClick={handleMoveBack} className='cursor-hover hover:text-white'>
              <IoChevronBackOutline fontSize={'27px'} />
            </div>
          </div>
        </div>
      ) : headerType === 'PROFILE' ? (
        <div className='h-[60px] top-0 right-0 flex justify-between items-center'>
          <div className='flex gap-2 ms-2'>
            <div className='cursor-hover hover:text-main-blue'>
              <Link href='/main' className='cursor-hover' onClick={playsound}>
                <IoChevronBackOutline fontSize={'27px'} />
              </Link>
            </div>
            <div className='inline-block text-lg font-semibold '>마이 프로필</div>
          </div>
          <Link href='/myprofile/edit' className='cursor-hover' onClick={playsound}>
            <div className='me-4 hover:text-main-blue'>
              <IoPencilSharp fontSize={'25px'} />
            </div>
          </Link>
        </div>
      ) : headerType === 'WAITING' ? (
        <div className='bg-black h-[60px] top-0 right-0 gap-2 pe-4 flex justify-end items-center'>
          <div
            onClick={() => handleCopyCode(roomCode)}
            className='cursor-hover text-white hover:text-main-blue'
          >
            <IoCopyOutline fontSize={'27px'} />
          </div>
          <div className='cursor-hover text-white hover:text-main-blue' onClick={playsound}>
            <IoExitOutline fontSize={'30px'} />
          </div>
        </div>
      ) : headerType === 'GAME' ? (
        <div className='bg-black h-[60px] top-0 right-0 gap-[107px] pe-4 flex justify-end items-center'>
          <div className='text-white text-3xl font-semibold'>0:11</div>
          <div className='cursor-hover text-white hover:text-main-blue' onClick={exitGame}>
            <IoExitOutline fontSize={'30px'} />
          </div>
        </div>
      ) : headerType === 'MATCHING' ? (
        <div className='h-[60px] top-0 right-0 flex items-center'>
          <div className='flex gap-2 ms-2'>
            <div onClick={exitMatching} className='cursor-hover hover:text-main-blue'>
              <IoChevronBackOutline fontSize={'27px'} />
            </div>
            <div className='inline-block text-lg font-semibold'>{children}</div>
          </div>
        </div>
      ) : (
        <div className='h-[60px] top-0 right-0 flex items-center'>
          <div className='flex gap-2 ms-2'>
            <div onClick={handleMoveBack} className='cursor-hover hover:text-main-blue'>
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
