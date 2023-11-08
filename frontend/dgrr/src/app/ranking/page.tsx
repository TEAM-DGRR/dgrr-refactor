'use client';

import Header from '@/components/elements/Header';
import RankingItem from '@/components/elements/RankingItem';
import { useEffect, useState } from 'react';

const Ranking = () => {
  const [currentSeason, setCurrentSeason] = useState(true);
  const [rankingData, setRankingData] = useState({
    memberRank: {
      memberId: 0,
      rating: 0,
      rank: 0,
      nickname: '',
      profileImage: '',
    },
    rankings: [
      {
        memberId: 0,
        rating: 0,
        rank: 0,
        nickname: '',
        profileImage: '',
      },
    ],
  });

  const clickCurrent = () => {
    setCurrentSeason(true);
  };

  const clickPast = () => {
    setCurrentSeason(false);
  };

  useEffect(() => {
    setRankingData({
      memberRank: {
        nickname: 'myNickname',
        profileImage: '/images/nongdam.jpg',
        rank: 1,
        memberId: 1,
        rating: 1900,
      },
      rankings: [
        {
          nickname: 'myNickname',
          profileImage: '/images/nongdam.jpg',
          rank: 1,
          memberId: 1,
          rating: 1900,
        },
        {
          nickname: '영호 키는 2미터',
          profileImage: '/images/sample_image1.png',
          rank: 2,
          memberId: 2,
          rating: 1800,
        },
        {
          nickname: '오늘 밤 주인공은 현아',
          profileImage: '/images/sample_image2.png',
          rank: 3,
          memberId: 3,
          rating: 1800,
        },
        {
          nickname: '긍정왕 미정킹',
          profileImage: '/images/sample_image3.png',
          rank: 4,
          memberId: 4,
          rating: 1800,
        },
        {
          nickname: '뉴비 민영',
          profileImage: '',
          rank: 5,
          memberId: 5,
          rating: 1400,
        },
        {
          nickname: '잼민킹 수연',
          profileImage: '/images/sample_image4.png',
          rank: 6,
          memberId: 6,
          rating: 1800,
        },
        {
          nickname: '승규는 예비군',
          profileImage: '/images/sample_image1.png',
          rank: 7,
          memberId: 7,
          rating: 1800,
        },
        {
          nickname: '가나다라마바사아자차카타',
          profileImage: '',
          rank: 8,
          memberId: 8,
          rating: 1700,
        },
        {
          nickname: '가나다라마바사아자차카타',
          profileImage: '',
          rank: 9,
          memberId: 9,
          rating: 1500,
        },
        {
          nickname: '가나다라마바사아자차카타',
          profileImage: '',
          rank: 10,
          memberId: 10,
          rating: 1600,
        },
      ],
    });
  }, []);

  return (
    <div className='w-screen max-w-[500px]'>
      <Header headerType='OTHER'>랭킹 조회</Header>
      {currentSeason === true ? (
        <div className='flex justify-center mt-3 mb-8'>
          <div className='w-11/12'>
            <div className='h-10 flex'>
              <div className='flex items-center justify-center w-1/2 h-full bg-[#EAEEFF] rounded-t-lg text-sm font-bold'>
                현 시즌 랭킹
              </div>
              <div
                onClick={clickPast}
                className='flex items-center justify-center w-1/2 h-full bg-main-blue rounded-t-lg text-sm font-bold cursor-hover hover:brightness-90'
              >
                전 시즌 랭킹
              </div>
            </div>
            <div className='h-[110px] bg-[#EAEEFF] flex justify-center items-center'>
              <RankingItem item={rankingData.memberRank} itemType='MY' />
            </div>
            <div className='h-[1020px] py-[10px] bg-[#DADADA] rounded-b-lg'>
              <div className=''>
                {rankingData.rankings.map((item, index) => [
                  <div className='flex justify-center' key={index}>
                    <RankingItem item={item} itemType='ALL' />
                  </div>
                ])}
              </div>
            </div>
          </div>
        </div>
      ) : (
        <div className='flex justify-center mt-3 mb-8'>
          <div className='w-11/12'>
            <div className='h-10 flex'>
              <div
                onClick={clickCurrent}
                className='flex items-center justify-center w-1/2 h-full bg-main-blue rounded-t-lg text-sm font-bold cursor-hover cursor-hover hover:brightness-90'
              >
                현 시즌 랭킹
              </div>
              <div className='flex items-center justify-center w-1/2 h-full bg-[#EAEEFF] rounded-t-lg text-sm font-bold'>
                전 시즌 랭킹
              </div>
            </div>
            <div className='h-[110px] bg-[#EAEEFF] flex justify-center items-center'>
              <RankingItem item={rankingData.memberRank} itemType='MY' />
            </div>
            <div className='h-[1020px] py-[10px] bg-[#DADADA] rounded-b-lg'>
              <div>
                {rankingData.rankings.map((item, index) => [
                  <div className='flex justify-center' key={index}>
                    <RankingItem item={item} itemType='ALL' />
                  </div>
                ])}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Ranking;
