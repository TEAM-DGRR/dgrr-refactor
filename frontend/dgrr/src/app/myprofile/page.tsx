'use client';

import React, { useEffect, useState } from 'react';
import Header from '@/components/elements/Header';
import Link from 'next/link';
import Rank from '@/components/elements/Rank';
import { IoChevronForwardOutline } from 'react-icons/io5';
import RecentRecordItem from '@/components/elements/RecentRecordItem';
import { getMyInfoApi } from '@/apis/myProfileApi';

const MyProfile = () => {
  // Back에서 정보를 이 형태로 보내줌
  const [myInfo, setMyInfo] = useState({
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
  });

  const handleLogin = () => {
    console.log('Logout');
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await getMyInfoApi();
        console.log('데이터 가져오기 성공:', response);
        await setMyInfo(response)
  
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
      <Header headerType='PROFILE' />
      <div className='h-[220px] flex justify-center items-center'>
        <div>
          {/* 프로필 사진 */}
          <div className='flex justify-center'>
            {myInfo.member.profileImage !== '' ? (
              <img
                src={myInfo.member.profileImage}
                alt='profileImage'
                className='w-[80px] aspect-square rounded-full'
              />
            ) : (
              <img
                src='/images/default_profile_image.png'
                alt='profileImage'
                className='w-[80px] aspect-square rounded-full'
              />
            )}
          </div>
          {/* 닉네임 */}
          <div className='text-center mt-6 mb-3 text-lg font-semibold'>
            {myInfo.member.nickname}
          </div>
          {/* 프로필 메시지 */}
          <div className='text-center text-sm text-[#767676]'>{myInfo.member.description}</div>
        </div>
      </div>
      {/* 내 티어 */}
      <Rank pageType='PROFILE' tier={myInfo.ranking.tier} rating={myInfo.ranking.score} />
      {/* 최근 전적 */}
      <div className='h-[220px] p-6'>
        <div className='flex justify-between items-center mb-4'>
          <div className='text-lg font-semibold'>최근 전적</div>
          {/* 전적이 아직 하나도 없다면 더 보기 버튼 생기지 않음 */}
          {myInfo.gameHistoryList.length !== 0 ? (
            <Link href='/myprofile/recent-record'>
              <div className='flex cursor-pointer gap-x-[3px] hover:text-main-blue'>
                <div className='text-sm font-bold inline-block'>더 보기</div>
                <div className='inline-block'>
                  <IoChevronForwardOutline fontSize={'18px'} />
                </div>
              </div>
            </Link>
          ) : null}
        </div>
        {myInfo.gameHistoryList.length !== 0 ? (
          <div>
            {/* 전적이 3개를 초과하면 3개만 보이도록 함 */}
            {myInfo.gameHistoryList.length > 3 ? (
              <div>
                {myInfo.gameHistoryList.slice(0, 3).map((item, index) => (
                  <RecentRecordItem pageType='PROFILE' item={item} key={index} />
                ))}
              </div>
            ) : (
              <div>
                {myInfo.gameHistoryList.map((item, index) => (
                  <RecentRecordItem pageType='PROFILE' item={item} key={index} />
                ))}
              </div>
            )}
          </div>
        ) : (
          <div className='w-full h-full flex justify-center items-center'>
            <div className='text-lg text-[#868E96]'>전적이 없습니다🧐</div>
          </div>
        )}
      </div>
      <div
        onClick={handleLogin}
        className='flex justify-center mt-[53px] mb-2 text-sm font-bold cursor-pointer hover:text-[#E83F57]'
      >
        로그아웃
      </div>
    </div>
  );
};

export default MyProfile;
