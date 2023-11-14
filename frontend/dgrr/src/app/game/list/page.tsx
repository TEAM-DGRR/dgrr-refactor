'use client';
import Header from '@/components/elements/Header';
import Image from 'next/image';
import doorImg from '@/../public/images/door.svg';
import keyImg from '@/../public/images/key.svg';
import welcomeImg from '@/../public/images/welcome.svg';
import Link from 'next/link';
import { FindRoomModal } from './FindRoomModal';
import { useEffect, useState } from 'react';
import { Client, StompHeaders } from '@stomp/stompjs';
import { useDispatch } from 'react-redux';
import { createClient, saveOrigin } from '@/store/gameSlice';
import axios from 'axios';
import { saveRoomCode } from '@/store/roomSlice';
import { useRouter } from 'next/navigation';

const ListPage = () => {
  const gameList = [
    { value: '방 만들기', imgLink: doorImg, navLink: '/room/loading' },
    { value: '방 찾기', imgLink: keyImg, navLink: '' },
    { value: '랜덤 매칭', imgLink: welcomeImg, navLink: '/game/loading' },
  ];
  const commonClass =
    'w-4/5 max-w-xs py-3 mx-auto space-y-3 bg-white rounded-lg border-2 border-black flex flex-col items-center hover:brightness-125';
  const handleModal = () => {
    setIsModal(!isModal);
  };
  const [isModal, setIsModal] = useState(false);
  const dispatch = useDispatch();
  const [memberId, setMemberId] = useState('');
  const backUrl = process.env.NEXT_PUBLIC_BACK_URL;
  const router = useRouter();

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
      // console.log(client);
      // redux에 client 저장
      dispatch(createClient(client));
    };
  };

  const makeRoom = (headers: StompHeaders) => {
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
      // console.log(client);
      // redux에 client 저장
      dispatch(createClient(client));
      dispatch(saveOrigin('room'));
    };

    if (client) {
      axios
        .post(`${backUrl}/waiting-room`)
        .then((res) => {
          // console.log(res.data);
          if (res.data) {
            const roomCode = res.data;
            dispatch(saveRoomCode(roomCode));
          }
        })
        .catch((err) => {
          console.log('내용: ', err.message);
          router.push('/');
        });
    }
  };

  const joinRoom = () => {
    setIsModal(!isModal);
    connectStomp({ Authorization: memberId });
    dispatch(saveOrigin('room'));
  };
  useEffect(() => {
    const memberId = localStorage.getItem('memberId');
    if (memberId) {
      setMemberId(memberId);
    }
  }, []);

  return (
    <div className='bg-main-blue w-screen h-screen max-w-[500px] min-h-[565px]'>
      <Header headerType='GAMESTART' />
      {isModal && <FindRoomModal handleModal={handleModal} />}

      <div className='pt-6 space-y-10 grid'>
        {gameList.map((item, index) =>
          index === 1 ? (
            <div className={`${commonClass} cursor-hover`} key={index} onClick={joinRoom}>
              <Image src={item.imgLink} alt='이미지예시' className='w-20 h-20' />
              <p className='font-bold text-base'>{item.value}</p>
            </div>
          ) : (
            <button
              key={index}
              onClick={
                index === 0
                  ? () => makeRoom({ Authorization: memberId })
                  : () => connectStomp({ Authorization: memberId })
              }
              className={`${commonClass} cursor-hover`}
            >
              <Link
                href={item.navLink}
                className='w-full h-full flex flex-col items-center cursor-hover'
              >
                <Image src={item.imgLink} alt='이미지예시' className='w-20 h-20' />
                <p className='font-bold text-base'>{item.value}</p>
              </Link>
            </button>
          )
        )}
      </div>
    </div>
  );
};

export default ListPage;
