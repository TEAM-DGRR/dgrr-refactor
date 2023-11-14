'use client';

import Header from '@/components/elements/Header';
import ModalWithX from '@/components/elements/ModalWithX';
import RecentRecordItem from '@/components/elements/RecentRecordItem';
import { useEffect, useState } from 'react';
import { getMyInfoApi } from '@/apis/myProfileApi';
import ButtonClickAudio from '@/components/audio/ButtonClickAudio';
import axios from 'axios';
import Toast from '@/components/elements/Toast';
import { useRouter } from 'next/navigation';

const RecentRecord = () => {
  
  const router = useRouter();
  const [modalStatus, setModalStatus] = useState(false);
  const [selectedItemIndex, setSelectedItemIndex] = useState(0);
  const playsound = ButtonClickAudio();

  const openModal = (index: any) => {
    playsound();
    setSelectedItemIndex(index);
    setModalStatus(true);
  };
  const closeModal = () => {
    playsound();
    setModalStatus(false);
    setSelectedItemIndex(0);
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
    axios.defaults.headers.common['Authorization'] = localStorage.getItem('token');
    const fetchData = async () => {
      try {
        const response = await getMyInfoApi();
        // console.log('데이터 가져오기 성공:', response);
        await setMyInfo(response);

        // response의 PromiseResult를 추출
        const { gameHistoryList, member, ranking } = response;
      } catch (error) {
        console.error('데이터 가져오기 실패:', error);
      }
    };

    fetchData();
  }, []);

  return (
    <div className='w-screen max-w-[500px]'>
      <Header headerType='OTHER'>최근 전적</Header>
      <div className='px-4 pt-2'>
        {myInfo.gameHistoryList.map((item, index) => (
          <div key={index}>
            {item.gameResult === 'WIN' && item.highlightImage ? (
              <div
                onClick={() => openModal(index)}
                className='cursor-hover hover:bg-blue-200 hover:rounded-lg'
              >
                <RecentRecordItem pageType='RECENT-RECORD' item={item} />
              </div>
            ) : item.gameResult === 'LOSE' && item.highlightImage ? (
              <div
                onClick={() => openModal(index)}
                className='cursor-hover hover:bg-red-200 hover:rounded-lg'
              >
                <RecentRecordItem pageType='RECENT-RECORD' item={item} />
              </div>
            ) : (
              <div>
                <RecentRecordItem pageType='RECENT-RECORD' item={item} />
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
