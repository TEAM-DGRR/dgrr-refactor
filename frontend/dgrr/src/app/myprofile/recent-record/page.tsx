'use client';

import Header from '@/components/elements/Header';
import ModalWithX from '@/components/elements/ModalWithX';
import RecentRecordItem from '@/components/elements/RecentRecordItem';
import { useEffect, useState } from 'react';

const RecentRecord = () => {
  const [modalStatus, setModalStatus] = useState(false);
  const [selectedItemIndex, setSelectedItemIndex] = useState(0);

  const openModal = (index: any) => {
    setSelectedItemIndex(index);
    setModalStatus(true);
    console.log('Open Modal');
  };
  const closeModal = () => {
    setModalStatus(false);
    setSelectedItemIndex(0);
    console.log('Close Modal');
  };

  // Back에서 정보를 이 형태로 보내줌
  const [myInfo, setMyInfo] = useState({
    member: {
      memberId: 0,
      nickname: '',
      profileImage: '',
      description: '',
    },
    ranking: {
      season: 0,
      score: 0,
      rank: '',
    },
    gameDetailList: [
      {
        gameDetailId: 0,
        gameRoomId: 0,
        gameResult: '',
        gameType: '',
        gameTime: 0,
        holdingTime: 0,
        laughAmount: 0,
        highlightImage: '',
        opponentNickname: '',
        opponentProfileImage: '',
        opponentDescription: '',
      },
    ],
  });

  // 나중에 삭제할 더미 데이터
  useEffect(() => {
    setMyInfo({
      member: {
        memberId: 1,
        nickname: '가나다라마바사아자차카타',
        profileImage: '/images/nongdam.jpg',
        description: '행복한 하루 보내길',
      },
      ranking: {
        season: 1,
        score: 1500,
        rank: 'BRONZE',
      },
      gameDetailList: [
        {
          gameDetailId: 1,
          gameRoomId: 123456,
          gameResult: 'WIN',
          gameType: 'RANDOM',
          gameTime: 30,
          holdingTime: 30,
          laughAmount: 415,
          highlightImage: '/images/sample_image1.png',
          opponentNickname: '보라돌이',
          opponentProfileImage: '/images/sample_image1.png',
          opponentDescription: '2023-10-30',
        },
        {
          gameDetailId: 2,
          gameRoomId: 123456,
          gameResult: 'DRAW',
          gameType: 'RANDOM',
          gameTime: 30,
          holdingTime: 30,
          laughAmount: 415,
          highlightImage: '',
          opponentNickname: '뚜비',
          opponentProfileImage: '/images/sample_image2.png',
          opponentDescription: '2023-10-29',
        },
        {
          gameDetailId: 3,
          gameRoomId: 123456,
          gameResult: 'LOSE',
          gameType: 'RANDOM',
          gameTime: 30,
          holdingTime: 30,
          laughAmount: 415,
          highlightImage: '/images/sample_image3.png',
          opponentNickname: '나나',
          opponentProfileImage: '/images/sample_image3.png',
          opponentDescription: '2023-10-28',
        },
        {
          gameDetailId: 4,
          gameRoomId: 123456,
          gameResult: 'WIN',
          gameType: 'RANDOM',
          gameTime: 30,
          holdingTime: 30,
          laughAmount: 415,
          highlightImage: '/images/sample_image4.png',
          opponentNickname: '뽀',
          opponentProfileImage: '/images/sample_image4.png',
          opponentDescription: '2023-10-27',
        },
      ],
    });
  }, []);

  return (
    <div>
      <Header headerType='OTHER'>최근 전적</Header>
      <div className='px-6 pt-2'>
        {myInfo.gameDetailList.map((item, index) => (
          <div key={index}>
            {item.gameResult !== 'DRAW' ? (
              <div onClick={() => openModal(index)} className='cursor-pointer'>
                <RecentRecordItem item={item} />
              </div>
            ) : (
              <div>
                <RecentRecordItem item={item} />
              </div>
            )}
          </div>
        ))}
      </div>
      <ModalWithX
        closeModal={closeModal}
        modalStatus={modalStatus}
        item={myInfo.gameDetailList[selectedItemIndex]}
      />
    </div>
  );
};

export default RecentRecord;
