'use client';
import character from '@/../../public/images/floating_bread_cropped.gif';
import Image from 'next/image';
import Header from '@/components/elements/Header';
import { FuncButton } from '@/components/FuncButton';
import { useEffect, useState, useRef } from 'react';
import { ExplainModal } from '@/components/elements/ExplainModal';
import { useAppSelector } from '@/store/hooks';
import { stompConfig } from '@/types/game';
import { useDispatch } from 'react-redux';
import { saveGameInfo, saveOrigin } from '@/store/gameSlice';
import { publishMessage } from '@/components/Game/stomp';
import { useRouter } from 'next/navigation';
import Toast from '@/components/elements/Toast';

const GameLoading = () => {
  const [openModal, setOpenModal] = useState(false);
  const [seconds, setSeconds] = useState(0);
  const handleModal = () => {
    setOpenModal(!openModal);
  };

  const client = useAppSelector((state) => state.game.client);
  const { DESTINATION_URI } = stompConfig;
  const { GAME_URI, MATCHING_URI } = DESTINATION_URI;
  const dispatch = useDispatch();
  const router = useRouter();
  const audioRef = useRef<HTMLAudioElement | null>(null);

  // 랜덤매칭
  const gameMatch = () => {
    if (client) {
      publishMessage(client, MATCHING_URI, '');
    }
  };

  const subscribeGame = () => {
    client?.subscribe(GAME_URI, (message) => {
      // console.log('게임정보 받는 메세지: ', message.body);
      // 게임 정보 저장
      dispatch(saveGameInfo(JSON.parse(message.body)));
      // 게임 정보가 왔다면 매칭 페이지로 이동
      router.push('/game/match');
    });
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
    audioRef.current = new Audio('/audio/game-loading.mp3');
    audioRef.current.play();
    const interval = setInterval(() => {
      setSeconds((prev) => prev + 1);
    }, 1000);
    subscribeGame();
    gameMatch();
    dispatch(saveOrigin('random'));
    return () => {
      // 페이지를 벗어날 때 오디오 정지
      audioRef.current?.pause();
      clearInterval(interval);
    };
  }, [client]);

  return (
    <div className='bg-main-blue w-screen h-screen min-h-[580px] max-w-[500px]'>
      <Header headerType='MATCHING' />
      {openModal && <ExplainModal onClose={handleModal} />}
      <div className='flex flex-col justify-between h-3/5 pt-10'>
        <div className='flex justify-center mb-10'>
          <Image alt='캐릭터' src={character} />
        </div>
        <div className='flex justify-center font-bold'>
          <h1>게임 찾는 중</h1>
          <span className='animate-blink'>.</span>
          <span className='animate-blink2'>.</span>.<span className='animate-blink3'>.</span>
        </div>
        <div className='flex justify-center font-bold mt-10'>
          <div className='Timer'>{seconds}s</div>
        </div>
        <div className='flex justify-center mt-40'>
          <FuncButton value='게임 설명' clickEvent={handleModal} />
        </div>
      </div>
    </div>
  );
};

export default GameLoading;
