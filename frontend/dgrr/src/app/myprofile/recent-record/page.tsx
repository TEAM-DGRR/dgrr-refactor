'use client';

import Header from '@/components/elements/Header';
import ModalWithX from '@/components/elements/ModalWithX';
import RecentRecordItem from '@/components/elements/RecentRecordItem';
import { useEffect, useState } from 'react';

const RecentRecord = () => {
  const [modalStatus, setModalStatus] = useState(false);
  const [selectedItemIndex, setSelectedItemIndex] = useState(0);
  const currentDate = new Date();

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
      rank: 0,
      tier: '',
    },
    gameHistoryList: [
      {
        gameHistoryId: 0,
        gameRoomId: 0,
        gameResult: '',
        gameType: '',
        gameTime: 0,
        holdingTime: 0,
        ratingChange: 0,
        highlightImage: '',
        createdAt: '',
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
        rank: 1,
        tier: 'BRONZE',
      },
      gameHistoryList: [
        {
          gameHistoryId: 1,
          gameRoomId: 123456,
          gameResult: 'WIN',
          gameType: 'RANDOM',
          gameTime: 30,
          holdingTime: 30,
          ratingChange: 415,
          highlightImage: '/images/sample_image1.png',
          createdAt: 'Thu Oct 02 2023 05:46:15 GMT+0900 (한국 표준시)',
          opponentNickname: '보라돌이',
          opponentProfileImage: '/images/sample_image1.png',
          opponentDescription: '2023-10-30',
        },
        {
          gameHistoryId: 2,
          gameRoomId: 123456,
          gameResult: 'DRAW',
          gameType: 'RANDOM',
          gameTime: 30,
          holdingTime: 30,
          ratingChange: 415,
          highlightImage: '',
          createdAt: 'Thu Sep 01 2023 05:46:15 GMT+0900 (한국 표준시)',
          opponentNickname: '뚜비',
          opponentProfileImage: '/images/sample_image2.png',
          opponentDescription: '2023-10-29',
        },
        {
          gameHistoryId: 3,
          gameRoomId: 123456,
          gameResult: 'LOSE',
          gameType: 'RANDOM',
          gameTime: 30,
          holdingTime: 30,
          ratingChange: 415,
          highlightImage: '/images/sample_image3.png',
          createdAt: 'Thu Nov 02 2023 09:46:15 GMT+0900 (한국 표준시)',
          opponentNickname: '나나',
          opponentProfileImage: '/images/sample_image3.png',
          opponentDescription: '2023-10-28',
        },
        {
          gameHistoryId: 4,
          gameRoomId: 123456,
          gameResult: 'WIN',
          gameType: 'RANDOM',
          gameTime: 30,
          holdingTime: 30,
          ratingChange: 415,
          highlightImage: '/images/sample_image4.png',
          createdAt: 'Thu Oct 15 2023 09:46:15 GMT+0900 (한국 표준시)',
          opponentNickname: '뽀',
          opponentProfileImage: '/images/sample_image4.png',
          opponentDescription: '2023-10-27',
        },
      ],
    });
  }, []);

  return (
    <div className='w-screen max-w-[500px]'>
      <Header headerType="OTHER">최근 전적</Header>
      <div className="px-6 pt-2">
        {myInfo.gameHistoryList.map((item, index) => (
          <div key={index}>
            {item.gameResult !== 'DRAW' ? (
              <div onClick={() => openModal(index)} className="cursor-pointer hover:px-1 hover:bg-blue-200 hover:rounded-lg">
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
        item={myInfo.gameHistoryList[selectedItemIndex]}
      />
    </div>
  );
};

export default RecentRecord;
