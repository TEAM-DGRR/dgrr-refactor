'use client';

import Header from '@/components/elements/Header';
import RankingItem from '@/components/elements/RankingItem';
import axios from 'axios';
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

  const fetchRankingData = async () => {
    const season = currentSeason ? 'CURRENT' : 'LAST';
    axios
      .get(`${process.env.NEXT_PUBLIC_BACK_URL}/ranking/member-id/${season}`)
      .then((response) => {
        setRankingData(response.data);
      });
  };

  const clickCurrent = () => {
    setCurrentSeason(true);
  };

  const clickPast = () => {
    setCurrentSeason(false);
  };

  useEffect(() => {
    axios.defaults.headers.common['Authorization'] = localStorage.getItem('token');
    fetchRankingData();
  }, [currentSeason]);

  return (
    <div className='w-screen max-w-[500px]'>
      <Header headerType='OTHER'>랭킹 조회</Header>
      {currentSeason === true ? (
        <div className='flex justify-center mt-3 mb-8'>
          <div className='w-11/12'>
            <div className='h-10 flex'>
              <div
                onClick={clickPast}
                className='flex items-center justify-center w-1/2 h-full bg-main-blue rounded-t-lg text-sm font-bold cursor-hover hover:brightness-90'
              >
                전 시즌 랭킹
              </div>
              <div className='flex items-center justify-center w-1/2 h-full bg-[#EAEEFF] rounded-t-lg text-sm font-bold'>
                현 시즌 랭킹
              </div>
            </div>
            <div className='h-[110px] bg-[#EAEEFF] flex justify-center items-center'>
              <RankingItem item={rankingData.memberRank} itemType='MY' />
            </div>
            <div className='max-h-[1020px] py-[10px] bg-[#DADADA] rounded-b-lg'>
              {rankingData.rankings.map((item, index) => [
                <div className='flex justify-center' key={index}>
                  <RankingItem item={item} itemType='ALL' />
                </div>,
              ])}
            </div>
          </div>
        </div>
      ) : (
        <div className='flex justify-center mt-3 mb-8'>
          <div className='w-11/12'>
            <div className='h-10 flex'>
              <div className='flex items-center justify-center w-1/2 h-full bg-[#EAEEFF] rounded-t-lg text-sm font-bold'>
                전 시즌 랭킹
              </div>
              <div
                onClick={clickCurrent}
                className='flex items-center justify-center w-1/2 h-full bg-main-blue rounded-t-lg text-sm font-bold cursor-hover hover:brightness-90'
              >
                현 시즌 랭킹
              </div>
            </div>
            {rankingData.memberRank.rank !== null && (
              <div className='h-[110px] bg-[#EAEEFF] flex justify-center items-center'>
                <RankingItem item={rankingData.memberRank} itemType='MY' />
              </div>
            )}
            <div className='max-h-[1020px] py-[10px] bg-[#DADADA] rounded-b-lg'>
              {rankingData.rankings.length > 0 ? (
                <div>
                  {rankingData.rankings.map((item, index) => [
                    <div className='flex justify-center' key={index}>
                      <RankingItem item={item} itemType='ALL' />
                    </div>,
                  ])}
                </div>
              ) : (
                <div className='flex justify-center h-screen py-[10px] rounded-b-lg font-semibold items-center'>
                  이전 시즌 랭킹 정보가 없습니다.
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Ranking;
