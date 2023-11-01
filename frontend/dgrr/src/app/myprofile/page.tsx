'use client';

import React, { useEffect, useState } from 'react';
import Header from '@/components/elements/Header';
import Link from 'next/link';
import Rank from '@/components/elements/Rank';
import { IoChevronForwardOutline } from 'react-icons/io5';
import RecentRecordItem from '@/components/elements/RecentRecordItem';

const MyProfile = () => {
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

  const handleLogin = () => {
    console.log('Logout');
  };

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
          highlightImage: '/images/sample_image4.pnge',
          opponentNickname: '뽀',
          opponentProfileImage: '/images/sample_image4.png',
          opponentDescription: '2023-10-27',
        },
      ],
    });
  }, []);

  return (
    <div>
      <Header headerType="PROFILE" />
      <div className="h-[220px] flex justify-center items-center">
        <div>
          {/* 프로필 사진 */}
          <div className="flex justify-center">
            {myInfo.member.profileImage !== '' ? (
              <img
                src={myInfo.member.profileImage}
                alt="profileImage"
                className="w-[80px] aspect-square rounded-full"
              />
            ) : (
              <img
                src="/images/default_profile_image.png"
                alt="profileImage"
                className="w-[80px] aspect-square rounded-full"
              />
            )}
          </div>
          {/* 닉네임 */}
          <div className="text-center mt-6 mb-3 text-lg font-semibold">
            {myInfo.member.nickname}
          </div>
          {/* 프로필 메시지 */}
          <div className="text-center text-sm text-[#767676]">{myInfo.member.description}</div>
        </div>
      </div>
      {/* 내 티어 */}
      <Rank rank={myInfo.ranking.rank} rating={myInfo.ranking.score} />
      {/* 최근 전적 */}
      <div className="h-[220px] p-6">
        <div className="flex justify-between items-center mb-4">
          <div className="text-lg font-semibold">최근 전적</div>
          {/* 전적이 아직 하나도 없다면 더 보기 버튼 생기지 않음 */}
          {myInfo.gameDetailList.length !== 0 ? (
            <Link href="/myprofile/recent-record">
              <div className="flex cursor-pointer gap-x-[3px] hover:text-main-blue">
                <div className="text-sm font-bold inline-block">더 보기</div>
                <div className="inline-block">
                  <IoChevronForwardOutline fontSize={'18px'} />
                </div>
              </div>
            </Link>
          ) : null}
        </div>
        {myInfo.gameDetailList.length !== 0 ? (
          <div>
            {/* 전적이 3개를 초과하면 3개만 보이도록 함 */}
            {myInfo.gameDetailList.length > 3 ? (
              <div>
                {myInfo.gameDetailList.slice(0, 3).map((item, index) => (
                  <RecentRecordItem item={item} key={index} />
                ))}
              </div>
            ) : (
              <div>
                {myInfo.gameDetailList.map((item, index) => (
                  <RecentRecordItem item={item} key={index} />
                ))}
              </div>
            )}
          </div>
        ) : (
          <div className="w-full h-full flex justify-center items-center">
            <div className="text-lg text-[#868E96]">전적이 없습니다🧐</div>
          </div>
        )}
      </div>
      <div
        onClick={handleLogin}
        className="flex justify-center mt-[53px] mb-2 text-sm font-bold cursor-pointer hover:text-[#E83F57]"
      >
        로그아웃
      </div>
    </div>
  );
};

export default MyProfile;
