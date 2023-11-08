'use client';
import Header from '@/components/elements/Header';
import Image from 'next/image';
import doorImg from '@/../public/images/door.svg';
import keyImg from '@/../public/images/key.svg';
import welcomeImg from '@/../public/images/welcome.svg';
import Link from 'next/link';
import { FindRoomModal } from './FindRoomModal';
import { useState } from 'react';
import { Client, StompHeaders } from '@stomp/stompjs';
import { useDispatch } from 'react-redux';
import { createClient } from '@/store/gameSlice';

const ListPage = () => {
  const gameList = [
    { value: '방 만들기', imgLink: doorImg, navLink: '/game/room' },
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
  
  const connectStomp = (headers: StompHeaders) => {
    const client = new Client({
      brokerURL: process.env.NEXT_PUBLIC_BROKER_URL,
      connectHeaders: {
        ...headers,
      },
      debug: (message) => {
        console.log('[Stomp Debug :: message]', message); // 웹소켓 디버깅 로그 추가
      },
    });

    // 클라이언트 활성화
    client.activate();

    client.onConnect = (frame) => {
      console.log('연결');
      console.log(client);
      // redux에 client 저장
      dispatch(createClient(client));
    };
  };

  return (
    <div className="bg-main-blue w-screen h-screen max-w-[500px] min-h-[565px]">
      <Header headerType="GAMESTART" />
      {isModal && <FindRoomModal handleModal={handleModal} />}

      <div className="pt-6 space-y-6">
        {gameList.map((item, index) =>
          index === 1 ? (
            <div className={`${commonClass} cursor-hover`} key={index} onClick={handleModal}>
              <Image src={item.imgLink} alt="이미지예시" className="w-20 h-20" />
              <p className="font-bold text-base">{item.value}</p>
            </div>
          ) : (
            <button
              key={index}
              onClick={() => connectStomp({ Authorization: '1' })}
              className={commonClass}
            >
              <Link href={item.navLink} className="w-full h-full flex flex-col items-center">
                <Image src={item.imgLink} alt="이미지예시" className="w-20 h-20" />
                <p className="font-bold text-base">{item.value}</p>
              </Link>
            </button>
          )
        )}
      </div>
    </div>
  );
};

export default ListPage;
