'use client';

import { ChangeEvent, useEffect, useState } from 'react';
import Header from '@/components/elements/Header';
import ImageInput from '@/components/elements/ImageInput';
import DataInput from '@/components/elements/DataInput';
import Swal from 'sweetalert2';

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
  };

  const [nowNickname, setNowNickName] = useState('');
  const [nowDescription, setNowDescription] = useState('');

  // 닉네임 입력값 반영
  const handleNicknameChange = (event: ChangeEvent<HTMLInputElement>) => {
    setNowNickName(event.target.value);
  };

  // 상태 메시지 입력값 반영
  const handleDescirptChange = (event: ChangeEvent<HTMLInputElement>) => {
    setNowDescription(event.target.value);
  };

  // 저장 버튼 눌렀을 때 작동하는 함수
  // (나중에 API 연결)
  const handleSaveButton = () => {
    console.log('Save');
  };

  const openWithdrawModal = () => {
    Swal.fire({
      width: 400,
      title: `데구르르에서
      탈퇴하시겠습니까?`,
      text: '기록과 사진이 모두 삭제됩니다😥',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#469FF6',
      cancelButtonColor: '#E83F57',
      confirmButtonText: '확인',
      cancelButtonText: '취소',
      customClass: {
        confirmButton: 'custom-confirm-button',
        cancelButton: 'custom-cancle-button',
      },
    }).then((result) => {
      // 나중에 token 삭제하는 API 넣어야 함
      // 확인 버튼 누르면 회원 탈퇴되고, 로그인 전 화면으로 이동함
      if (result.isConfirmed) {
        console.log('Withdraw DGRR');
        const newPathname = '/';
        window.location.href = newPathname;
      }
    });
  };

  useEffect(() => {
    console.log(sampleData);
    setNowNickName(sampleData.member.nickname);
    setNowDescription(sampleData.member.description);
  }, []);

  return (
    <div>
      <Header headerType="OTHER">프로필 수정</Header>
      <div>
        <ImageInput myProfileImage={sampleData.member.profileImage} />
        <DataInput
          inputType="NICKNAME"
          pageType="PROFILE_EDIT"
          onChange={handleNicknameChange}
          value={nowNickname}
        />
        <DataInput
          inputType="DESCRIPTION"
          pageType="PROFILE_EDIT"
          onChange={handleDescirptChange}
          value={nowDescription}
        />
      </div>
      <div className="px-6">
        <div
          onClick={handleSaveButton}
          className="bg-main-blue rounded-lg w-full max-w-xs p-4 hover:brightness-110"
        >
          <div className="text-white text-center text-base font-bold cursor-pointer uppercase leading-none">
            저장
          </div>
        </div>
      </div>
      <div
        onClick={openWithdrawModal}
        className="flex justify-center mt-40 mb-3 text-xs font-semibold cursor-pointer hover:text-[#E83F57]"
      >
        회원 탈퇴
      </div>
    </div>
  );
};

export default Edit;
