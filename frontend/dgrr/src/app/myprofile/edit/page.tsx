'use client';

import DataInput from '@/components/elements/DataInput';
import Header from '@/components/elements/Header';
import { ChangeEvent, useEffect, useState } from 'react';

const Edit = () => {
  // 나중에 삭제할 더미 데이터
  const sampleData = {
    member: {
      memberId: 1,
      nickname: '가나다라마바사아자',
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
    ],
  }

  const [nowNickname, setNowNickName] = useState('')
  const [nowDescription, setNowDescription] = useState('')
  
  // 닉네임 입력값 반영
  const handleNicknameChange = (event: ChangeEvent<HTMLInputElement>) => {
    setNowNickName(event.target.value);
  };

  // 상태 메시지 입력값 반영
  const handleDescirptChange = (event: ChangeEvent<HTMLInputElement>) => {
    setNowDescription(event.target.value);
  };

  useEffect(() => {
    console.log(sampleData)
    setNowNickName(sampleData.member.nickname)
    setNowDescription(sampleData.member.description)
  }, []);


  return (
    <div>
      <Header headerType='OTHER'>프로필 수정</Header>
      <div>
        <DataInput inputType='NICKNAME' pageType='SIGNUP' onChange={handleNicknameChange} />
        <DataInput inputType='NICKNAME' pageType='PROFILE_EDIT' onChange={handleNicknameChange} value={nowNickname} />
        <DataInput inputType='DESCRIPTION' pageType='SIGNUP' onChange={handleDescirptChange} />
        <DataInput inputType='DESCRIPTION' pageType='PROFILE_EDIT' onChange={handleDescirptChange} value={nowDescription} />
      </div>
    </div>
  )
}

export default Edit;