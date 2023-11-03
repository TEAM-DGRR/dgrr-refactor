'use client';
import Header from '@/components/elements/Header';
import Image from 'next/image';
import ex from '@/../public/images/logo_character.png';
import doorImg from '@/../public/images/door.svg';
import keyImg from '@/../public/images/key.svg';
import welcomeImg from '@/../public/images/welcome.svg';
import Link from 'next/link';
import { FindRoomModal } from './FindRoomModal';
import { useState } from 'react';

const ListPage = () => {
  const gameList = [
    { value: '방 만들기', imgLink: doorImg, navLink: 'game/room' },
    { value: '방 찾기', imgLink: keyImg, navLink: '' },
    { value: '랜덤 매칭', imgLink: welcomeImg, navLink: 'game/loading' },
  ];
  const commonClass =
    'w-4/5 max-w-xs py-3 mx-auto space-y-3 bg-white rounded-lg border-2 border-black flex flex-col items-center hover:brightness-125';
  const handleModal = () => {
    setIsModal(!isModal);
  };
  const [isModal, setIsModal] = useState(false);

  return (
    <div className="bg-main-blue w-screen h-screen max-w-[500px] min-h-[565px]">
      <Header headerType="GAMESTART" />
      {isModal && <FindRoomModal handleModal={handleModal} />}

      <div className="pt-6 space-y-6">
        {gameList.map((item, index) =>
          index === 1 ? (
            <div className={`${commonClass} cursor-pointer`} key={index} onClick={handleModal}>
              <Image src={item.imgLink} alt="이미지예시" className="w-20 h-20" />
              <p className="font-bold text-base">{item.value}</p>
            </div>
          ) : (
            <Link href={item.navLink} className={commonClass} key={index}>
              <Image src={item.imgLink} alt="이미지예시" className="w-20 h-20" />
              <p className="font-bold text-base">{item.value}</p>
            </Link>
          )
        )}
      </div>
    </div>
  );
};

export default ListPage;
