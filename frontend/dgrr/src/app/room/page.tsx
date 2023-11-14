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
  const enemy = useAppSelector((state) => state.room.roomInfo[1]?.waitingMember);

  const subscribeRoom = () => {
    client?.subscribe(READY_SUB_URI, (message) => {
      const content = JSON.parse(message.body);
      // console.log('준비상황: ', content);
      dispatch(setReady(content.waitingMember.ready));
    });
    client?.subscribe(EXIT_SUB_URI, (message) => {
      // console.log('나갔음: ', message.body);
      const content = JSON.parse(message.body);
      if (owner.waitingMemberId == content.waitingMember.waitingMemberId) {
        Toast.fire({
          html: '방장이 방을 나가<br>권한이 위임되었습니다.',
        });
      } else {
        Toast.fire('상대가 방을 나갔습니다');
      }
      dispatch(deleteMember(content));
    });
    client?.subscribe(GAME_URI, (message) => {
      // console.log('게임정보 받는 메세지: ', message.body);

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
    const token = localStorage.getItem('token');
    if (token) {
      const parseJwt = (token: any) => {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace('-', '+').replace('_', '/');
        return JSON.parse(window.atob(base64));
      };
      const id = parseJwt(token).id;
      localStorage.setItem('memberId', id);
    } else {
      Toast.fire('로그인이 필요합니다!', '', 'warning');
      // 토큰 없으면 로그인 화면으로 보내기
      router.push('/');
    }
    if (client) {
      subscribeRoom();
    }
    const memberId = localStorage.getItem('memberId');
    if (memberId) {
      setMemberId(memberId);
    }
  }, [client, memberId, owner]);

  return (
    <div className='w-screen h-screen max-w-[500px] min-h-[565px] bg-black relative'>
      <Header headerType='WAITING' roomCode={roomcode} />
      <div className='userVideo mb-3 mx-3'>
        {enemy ? (
          <div className='bg-white grid place-items-center h-full mx-auto w-full max-w-[412px]'>
            <img
              src={owner.waitingMemberId === memberId ? enemy.profileImage : owner.profileImage}
              alt='상대프로필'
              className='w-40 h-40 rounded-full border border-black'
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
      <div className='absolute left-1/2 -ml-8'>
        {enemy ? (
          owner.waitingMemberId !== memberId ? (
            <FuncButton value={value} small={true} clickEvent={readyOnOff} />
          ) : readyState ? (
            <FuncButton value='시작하기' small={true} clickEvent={startGame} />
          ) : (
            <button
              disabled
              className={`rounded-lg border-2 max-w-xs py-5 w-20 cursor-default bg-slate-300`}
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
      <div className='userVideo bg-white grid place-items-center h-full mx-auto w-full max-w-[412px]'>
        <img
          src={owner.waitingMemberId === memberId && owner.profileImage}
          alt='내 이미지'
          className='w-40 h-40 rounded-full border border-black'
        />
        <p className='font-bold text-2xl mt-6'>
          {owner.waitingMemberId === memberId && owner.nickname}
        </p>
      </div>
    </div>
  );
};

export default RoomPage;
