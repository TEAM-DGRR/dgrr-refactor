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
        createdAt: '2023-10-31T16:00:05',
        opponentNickname: '보라돌이',
        opponentProfileImage: '/images/sample_image1.png',
        opponentDescription: '2023-10-30',
      },
    ],
  };

  const [nowNickname, setNowNickName] = useState(sampleData.member.nickname);
  const [nowDescription, setNowDescription] = useState(sampleData.member.description);
  const [nowProfileImage, setNowProfileImage] = useState(sampleData.member.profileImage);
  const [nicknameExists, setNicknameExists] = useState(false);

  useEffect(() => {
    if (nowNickname === '농담곰의 농담') {
      setNicknameExists(true);
    } else {
      setNicknameExists(false);
    }
  }, [nowNickname]);

  // 닉네임 입력값 반영
  const handleNicknameChange = (event: ChangeEvent<HTMLInputElement>) => {
    setNowNickName(event.target.value);
  };

  // 상태 메시지 입력값 반영
  const handleDescirptChange = (event: ChangeEvent<HTMLInputElement>) => {
    setNowDescription(event.target.value);
  };

  // 프로필 이미지 업로드해서 변경하는 코드
  const changeProfileImage = (event: any) => {
    const reader = new FileReader();
    reader.onload = function (event) {
      if (event && event.target && typeof event.target.result === 'string') {
        setNowProfileImage(event.target.result);
      }
    };

    if (event && event.target && event.target.files && event.target.files[0]) {
      reader.readAsDataURL(event.target.files[0]);
    }
  };

  const requestNewNicknameModal = () => {
    Swal.fire({
      width: 400,
      title: `닉네임이 중복되거나
      입력되지 않았어요😥`,
      icon: 'error',
      confirmButtonColor: '#469FF6',
      confirmButtonText: '확인',
      customClass: {
        confirmButton: 'custom-confirm-button',
      },
    });
  };

  // 저장 버튼
  // 닉네임이 입력되지 않았거나 중복되면 경고 모달창이 뜸
  // (나중에 API 연결)
  const handleSaveButton = () => {
    console.log('Save');
    console.log(nowNickname, nowDescription)
    if (nicknameExists !== true && nowNickname !== '') {
      const newPathname = '/myprofile';
      window.location.href = newPathname;
    } else {
      requestNewNicknameModal();
    }
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
    <div className='w-screen max-w-[500px]'>
      <Header headerType='OTHER'>프로필 수정</Header>
      <div>
        <ImageInput myProfileImage={nowProfileImage} profileImageUpdate={changeProfileImage} />
        <DataInput
          inputType='NICKNAME'
          pageType='PROFILE_EDIT'
          onChange={handleNicknameChange}
          value={nowNickname}
          nicknameExists={nicknameExists}
        />
        <DataInput
          inputType='DESCRIPTION'
          pageType='PROFILE_EDIT'
          onChange={handleDescirptChange}
          value={nowDescription}
        />
      </div>
      <div className='px-6'>
        <div
          onClick={handleSaveButton}
          className='bg-main-blue rounded-lg w-full max-w-[500px] p-4 hover:brightness-110'
        >
          <div className='text-white text-center text-base font-bold cursor-hover uppercase leading-none'>
            저장
          </div>
        </div>
      </div>
      <div
        onClick={openWithdrawModal}
        className='flex justify-center mt-40 mb-3 text-xs font-semibold cursor-hover hover:text-[#E83F57]'
      >
        회원 탈퇴
      </div>
    </div>
  );
};

export default Edit;
