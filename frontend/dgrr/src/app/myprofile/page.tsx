'use client';

import React, { useEffect, useState } from 'react';
import Header from '@/components/elements/Header';
import Rank from '@/components/elements/Rank';

const MyProfile = () => {
  const [myInfo, setMyInfo] = useState({
    nickname: '',
    description: '',
    profileImage: '',
    rating: 0,
    rank: '',
  });

  useEffect(() => {
    setMyInfo({'nickname': '가나다라마바사아자차카타', 'description': '행복한 하루 보내길', 'profileImage': '/images/nongdam.jpg', 'rating': 1700, 'rank': 'Sliver'})
  }, [])

  return (
    <div>
      <Header headerType='PROFILE' />
      {/* 내 정보 */}
      <div className='h-[220px] flex justify-center items-center'>
        <div>
          <div className='flex justify-center'>
            {myInfo.profileImage !== '' ? (
              <img src={myInfo.profileImage} alt='profileImage' className='w-[80px] aspect-square rounded-full' />
            ) : (
              <img src='/images/default_profile_image.png' alt='profileImage' className='w-[80px] aspect-square rounded-full' />
              )}
          </div>
          <div className='text-center mt-6 mb-3 text-lg font-semibold'>
            {myInfo.nickname}
          </div>
          <div className='text-center text-sm text-[#767676]'>
            {myInfo.description}
          </div>
        </div>
      </div>
      <Rank rank={myInfo.rank} rating={myInfo.rating} />
    </div>
  )
}

export default MyProfile;