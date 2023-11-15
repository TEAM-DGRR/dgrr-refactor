'use client';
import Image from 'next/image';
import { IoCloseOutline } from 'react-icons/io5';
import { FuncButton } from '@/components/FuncButton';
import Rank from '@/components/elements/Rank';
import { useEffect, useState } from 'react';
import { useAppSelector } from '@/store/hooks';
import { useDispatch } from 'react-redux';
import { Client, StompHeaders } from '@stomp/stompjs';
import { createClient } from '@/store/gameSlice';
import { useRouter } from 'next/navigation';
import ButtonClickAudio from '@/components/audio/ButtonClickAudio';
import { reset } from '@/store/gameSlice';
import { disconnectSession } from '@/components/Game/openVidu';
import Toast from '@/components/elements/Toast';

const Result = () => {
  const [modalStatus, setModalStatus] = useState(false);
  const session = useAppSelector((state) => state.game.OVsession);
  const publisher = useAppSelector((state) => state.game.publisher);
  const [memberId, setMemberId] = useState('');
  const gameResult = useAppSelector((state) => state.game.gameResult);
  const origin = useAppSelector((state) => state.game.origin);

  const ratingProperty =
    gameResult.afterRating > gameResult.myInfo.rating
      ? {
          text: `${gameResult.afterRating}(+${gameResult.afterRating - gameResult.myInfo.rating})`,
          color: 'text-[#E83F57]',
        }
      : gameResult.afterRating < gameResult.myInfo.rating
      ? {
          text: `${gameResult.afterRating}(-${gameResult.myInfo.rating - gameResult.afterRating})`,
          color: 'text-[#5383E8]',
        }
      : {
          text: `${gameResult.afterRating}`,
          color: 'text-[#868E96]',
        };
  const playsound = ButtonClickAudio();

  const openModal = () => {
    playsound();
    setModalStatus(true);
  };
  const closeModal = () => {
    playsound();
    setModalStatus(false);
  };

  const clickOneMore = () => {
    dispatch(reset());
    playsound();
    connectStomp({ Authorization: memberId });
  };

  const clickGoToMain = () => {
    router.push('/main');
    playsound();
  };

  const dispatch = useDispatch();
  const router = useRouter();
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
    const memberId = localStorage.getItem('memberId');
    if (memberId) {
      setMemberId(memberId);
    }
    if (session) {
      disconnectSession(session, publisher);
    }
  }, [session]);
  const connectStomp = (headers: StompHeaders) => {
    const client = new Client({
      brokerURL: process.env.NEXT_PUBLIC_BROKER_URL,
      connectHeaders: {
        ...headers,
      },
      // debug: (message) => {
      //   console.log('[Stomp Debug :: message]', message); // 웹소켓 디버깅 로그 추가
      // },
    });

    // 클라이언트 활성화
    client.activate();

    client.onConnect = (frame) => {
      // console.log('연결');
      // redux에 client 저장
      dispatch(createClient(client));
      router.push('/game/loading');
    };
  };

  return (
    <div className='flex justify-center items-center bg-main-blue w-screen h-screen max-w-[500px]'>
      <div className='bg-white w-11/12 min-w-[264px] h-[575px] rounded-[12px] py-5 px-3'>
        <div className='text-[40px] font-bold text-center flex justify-between'>
          <div className='inline-block w-1/6'></div>
          {gameResult.gameResult === 'LOSE' ? <div>LOSS</div> : <div>{gameResult.gameResult}</div>}
          {/* 하이라이트 사진 미리보기 */}
          {gameResult.highlightImage ? (
            <img
              onClick={openModal}
              src={gameResult.highlightImage}
              alt='하이라이트 이미지'
              className='inline-block rounded-lg w-1/6 aspect-square animate-bounce cursor-hover'
            />
          ) : (
            <div className='inline-block w-1/6'></div>
          )}
        </div>
        <div className='mt-7'>
          <div className={`flex justify-center mb-3 text-2xl font-bold ${ratingProperty.color}`}>
            {ratingProperty.text}
          </div>
          <Rank pageType='GAMERESULT' rating={gameResult.afterRating} tier={gameResult.afterTier} />
        </div>
        <div>
          <div className='flex items-center justify-center mt-9 mb-7 gap-x-2'>
            <div>
              <img
                src={gameResult.enemyInfo.profileImage}
                alt='상대방 프로필 사진'
                className='w-10 aspect-square rounded-full'
              />
            </div>
            <div className='inline-block'>
              <div className='text-[15px] font-bold'>{gameResult.enemyInfo.nickname}</div>
              <div className='text-sm'>{gameResult.enemyInfo.description}</div>
            </div>
          </div>
        </div>
        <div className='space-y-3'>
          {origin !== 'room' && (
            <div className='flex justify-center'>
              <FuncButton clickEvent={clickOneMore} value='한 판 더?' />
            </div>
          )}
          <div className='flex justify-center'>
            <FuncButton clickEvent={clickGoToMain} value='메인으로' />
          </div>
        </div>
      </div>
      {/* 하이라이트 사진이 있는 모달 */}
      {modalStatus === true ? (
        <div className='z-10 bg-black/30 w-screen h-full max-w-[500px] fixed top-0 flex justify-center items-center'>
          <div className='w-72 h-fit bg-white rounded-lg border-2 border-black p-3'>
            <div className='flex justify-end mb-1' onClick={closeModal}>
              <button className='hover:text-[#E83F57] cursor-hover'>
                <IoCloseOutline fontSize={'24px'} />
              </button>
            </div>
            <div className='flex justify-center'>
              <img
                src={gameResult.highlightImage}
                alt='하이라이트 사진'
                className='w-full max-w-[280px]'
              />
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
};

export default Result;
