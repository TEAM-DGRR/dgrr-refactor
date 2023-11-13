'use client';
import Image from 'next/image';
import React, { useState, useEffect } from 'react';
import ProgressBar from '@ramonak/react-progress-bar';
import { IoHelpCircleOutline } from 'react-icons/io5';
import ButtonClickAudio from '../audio/ButtonClickAudio';

type pageType = 'GAMERESULT' | 'PROFILE';

interface RankProps {
  pageType: pageType;
  tier: string;
  rating: number;
}

const Rank = ({ pageType, tier, rating }: RankProps) => {
  const [nowRating, setNowRating] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const playsound = ButtonClickAudio();
  const myTier =
    tier === 'Gold'
      ? { name: 'Gold', image: '/images/Gold.png', tierColor: '#FCC858' }
      : tier === 'Sliver'
      ? { name: 'Sliver', image: '/images/Sliver.png', tierColor: '#B8B8B8' }
      : { name: 'Bronze', image: '/images/Bronze.png', tierColor: '#C4872F' };

  const textColor =
    tier === 'Gold' ? 'text-[#FCC858]' : tier === 'Sliver' ? 'text-[#B8B8B8]' : 'text-[#C4872F]';

  const tierList = [
    { image: '/images/Gold.png', title: '골드 Gold', explain: '1800 ~' },
    { image: '/images/Sliver.png', title: '실버 Sliver', explain: '1600 ~ 1799' },
    { image: '/images/Bronze.png', title: '브론즈 Bronze', explain: '0 ~ 1599' },
  ];

  const handleModalOpen = () => {
    playsound();
    setIsModalOpen(true);
  };

  const handleModalClose = () => {
    setIsModalOpen(false);
  };

  useEffect(() => {
    setNowRating(rating);
  }, [rating]);

  // ProgressBar 컴포넌트에 대한 기본 매개변수 설정
  const progressBarProps = {
    // 지금 게이지바가 얼마나 찼는지
    completed: nowRating,
    // 게이지바 전체 용량
    maxCompleted: 1800,
    // 게이지바 안에 들어갈 문구
    customLabel: ' ',
    width: '250px',
    height: '25px',
  };

  return (
    <div>
      {pageType === 'PROFILE' ? (
        <div className='h-[280px] py-6 my-2'>
          <div className='flex items-center pb-4'>
            <div className='text-lg font-semibold ps-6 me-1'>내 티어</div>
            <div
              className='inline-block cursor-hover hover:text-main-blue'
              onClick={handleModalOpen}
              onMouseLeave={handleModalClose}
            >
              <IoHelpCircleOutline fontSize={'18px'} />
            </div>
            <div>
              {/* 물음표 누르면 티어 정보 모달이 뜸 */}
              <div>
                {isModalOpen === true ? (
                  <div className='z-10 max-w-[500px] w-full h-full fixed top-[-65px] flex items-center'>
                    <div className='w-2/3 2sm:ms-3 h-fit bg-white rounded-lg border-2 border-black p-3'>
                      <div className='bg-white rounded-lg space-y-2'>
                        {tierList.map((tier, index) => [
                          <div
                            className='bg-[#e7e0ec] py-[10px] px-[8px]  rounded-md flex items-center gap-x-3'
                            key={index}
                          >
                            <Image
                              src={tier.image}
                              alt='티어 이미지'
                              width={500}
                              height={500}
                              className='w-[38px] aspect-square ms-3'
                            />
                            <div>
                              <div className='text-sm font-semibold'>{tier.title}</div>
                              <div className='text-xs'>{tier.explain}</div>
                            </div>
                          </div>,
                        ])}
                      </div>
                    </div>
                  </div>
                ) : null}
              </div>
            </div>
          </div>
          <div className={`mt-1 mb-3 text-3xl font-bold flex justify-center ${textColor}`}>{rating}</div>
          <div className='flex justify-center'>
            <Image
              src={myTier.image}
              alt={myTier.name}
              width={500}
              height={500}
              className='w-[80px] aspect-square'
            />
          </div>
          <div className='flex justify-center mt-[19px]'>
            <ProgressBar
              {...progressBarProps} // 기본 매개변수를 전달
              bgColor={myTier.tierColor}
            />
          </div>
        </div>
      ) : (
        <div>
          <div className='flex justify-center'>
            <Image
              src={myTier.image}
              alt={myTier.name}
              width={500}
              height={500}
              className='w-[90px] aspect-square'
            />
          </div>
          <div className='flex justify-center mt-[25px]'>
            <ProgressBar
              {...progressBarProps} // 기본 매개변수를 전달
              bgColor={myTier.tierColor}
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default Rank;

// ProgressBar 컴포넌트를 사용할 때 나타나는 경고를 숨김
console.error = () => {};
