'use client';

import { FuncButton } from '@/components/FuncButton';
import { publishMessage } from '@/components/Game/stomp';
import Header from '@/components/elements/Header';
import { saveGameInfo } from '@/store/gameSlice';
import { useAppSelector } from '@/store/hooks';
import { deleteMember, setReady } from '@/store/roomSlice';
import { stompConfig } from '@/types/game';
import { roomStompConfig } from '@/types/room';
import { useRouter } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';
import { useDispatch } from 'react-redux';
import character from '@/../public/images/logo_character.png';
import Image from 'next/image';
import Toast from '@/components/elements/Toast';

const RoomPage = () => {
  const client = useAppSelector((state) => state.game.client);
  const roomcode = useAppSelector((state) => state.room.roomCode);
  const owner = useAppSelector((state) => state.room.roomInfo[0].waitingMember);
  const [memberId, setMemberId] = useState('');
  const readyState = useAppSelector((state) => state.room.roomReady);
  const { ROOM_DESTINATION_URI } = roomStompConfig;
  const { READY_SUB_URI, EXIT_SUB_URI, READY_SEND_URI, ROOM_START_URI } = ROOM_DESTINATION_URI;
  const dispatch = useDispatch();
  const router = useRouter();
  const { DESTINATION_URI } = stompConfig;
  const { GAME_URI } = DESTINATION_URI;
  const value = readyState ? '취소하기' : '준비하기';
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const canvasRef = useRef(null);
  const enemy = useAppSelector((state) => state.room.roomInfo[1]?.waitingMember);

  const subscribeRoom = () => {
    client?.subscribe(READY_SUB_URI, (message) => {
      const content = JSON.parse(message.body);
      console.log('준비상황: ', content);
      dispatch(setReady(content.waitingMember.ready));
    });
    client?.subscribe(EXIT_SUB_URI, (message) => {
      console.log('나갔음: ', message.body);
      if (owner.waitingMemberId !== memberId) {
        Toast.fire({
          html: '방장이 방을 나가<br>권한이 위임되었습니다.',
        });
      }
      dispatch(deleteMember(JSON.parse(message.body)));
    });
    client?.subscribe(GAME_URI, (message) => {
      console.log('게임정보 받는 메세지: ', message.body);
      // 게임 정보 저장
      dispatch(saveGameInfo(JSON.parse(message.body)));
      // 게임 정보가 왔다면 매칭 페이지로 이동
      router.push('/game/match');
    });
  };

  const startGame = () => {
    if (client && readyState) {
      publishMessage(client, ROOM_START_URI, '');
    }
  };

  const readyOnOff = () => {
    if (client) {
      publishMessage(client, READY_SEND_URI, '');
    }
  };

  useEffect(() => {
    if (client) {
      subscribeRoom();
    }
    const memberId = localStorage.getItem('memberId');
    if (memberId) {
      setMemberId(memberId);
    }
    navigator.mediaDevices
      .getUserMedia({ video: true })
      .then((stream) => {
        if (videoRef.current) {
          videoRef.current.srcObject = stream;
        }
      })
      .catch((err) => console.log('웹캠 에러:', err));
  }, [client, memberId, owner]);

  return (
    <div className='w-screen h-screen max-w-[500px] min-h-[565px] bg-black relative'>
      <Header headerType='WAITING' roomCode={roomcode} />
      <div className='userVideo mx-3'>
        <video ref={videoRef} className='object-none h-full mx-auto' autoPlay muted />
        <canvas ref={canvasRef} width='640' height='480' style={{ display: 'none' }} />
      </div>
      <div className='absolute left-1/2 -ml-8'>
        {enemy ? (
          owner.waitingMemberId !== memberId ? (
            <FuncButton value={value} small={true} clickEvent={readyOnOff} />
          ) : readyState ? (
            <FuncButton value='시작하기' small={true} clickEvent={startGame} />
          ) : (
            <button
              disabled
              className={`rounded-lg border-2 max-w-xs py-5 hover:brightness-110 w-20 cursor-hover bg-slate-300`}
            >
              <p className={`text-zinc-800 text-center text-base font-bold uppercase leading-none`}>
                시작하기
              </p>
            </button>
          )
        ) : (
          ''
        )}
      </div>
      <div className='userVideo mt-3 mx-3'>
        <p>{owner.nickname}</p>
        {enemy ? (
          <div className='bg-white grid place-items-center h-full mx-auto w-full max-w-[412px]'>
            <Image
              src={owner.waitingMemberId === memberId ? enemy.profileImage : owner.profileImage}
              alt='상대프로필'
              width={160}
              height={160}
              className='rounded-full border border-black'
            />
            <p className='font-bold text-2xl mt-6'>
              {owner.waitingMemberId === memberId ? enemy.nickname : owner.nickname}
            </p>
          </div>
        ) : (
          <div className='bg-white grid place-items-center w-full h-full mx-auto max-w-[412px]'>
            <Image src={character} alt='식빵' className='w-40 h-40 animate-spin' />
            <p className='font-bold text-2xl mt-6'>상대를 기다리는 중입니다</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default RoomPage;
