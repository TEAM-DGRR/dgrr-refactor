'use client';

import { ChangeEvent, useEffect, useState } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import { FuncButton } from '@/components/FuncButton';
import DataInput from '@/components/elements/DataInput';
import ImageInput from '@/components/elements/ImageInput';
import Swal from 'sweetalert2';
import axios from 'axios';
import { setUrl } from '../../utils/setUrl';

const SignUp = () => {
  const router = useRouter();
  const [nickname, setNickName] = useState('');
  const [description, setDescription] = useState('');
  const [profileImage, setProfileImage] = useState('/images/default_profile_image.png');
  const [nicknameExists, setNicknameExists] = useState(false);
  const searchParams = useSearchParams();
  let kakaoId = searchParams.get('id');

  useEffect(() => {
    console.log('kakaoId: ' + kakaoId);
    if (nickname === '농담곰의 농담') {
      setNicknameExists(true);
    } else {
      setNicknameExists(false);
    }
  }, [nickname]);

  // 닉네임 입력값 반영
  const handleAddNickname = (event: ChangeEvent<HTMLInputElement>) => {
    setNickName(event.target.value);
  };

  // 상태 메시지 입력값 반영
  const handleAddDescirpt = (event: ChangeEvent<HTMLInputElement>) => {
    setDescription(event.target.value);
  };

  // 프로필 이미지 업로드해서 변경하는 코드
  const addProfileImage = (event: any) => {
    const reader = new FileReader();
    reader.onload = function (event) {
      if (event && event.target && typeof event.target.result === 'string') {
        setProfileImage(event.target.result);
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

  // 시작하기 버튼
  // 닉네임이 입력되지 않았거나 중복되면 경고 모달창이 뜸
  const handleSignUpButton = () => {
    if (nicknameExists !== true && nickname !== '') {
      // 후에 데이터 저장하는 API 넣기
      axios
        .post(
          `${setUrl}/member`,
          {
            kakaoId: kakaoId,
            nickname: nickname,
            profileImage: profileImage,
            description: description,
          },
          {
            withCredentials: false,
          }
        )
        .then((res: any) => {
          console.log(res.data.member);
          axios
            .get(`${setUrl}/kakao/login/${res.data.kakaoId}`)
            .then((res: any) => {
              localStorage.setItem('token', res.data.token);
              axios.defaults.headers.common['Authorization'] = `${res.data.token}`;
            })
            .catch((err: any) => {
              console.log(err);
            });
          router.push(`/main`);
        })
        .catch((err: any) => {
          console.log(err);
        });
    } else {
      requestNewNicknameModal();
    }
  };

  return (
    <div className="w-screen max-w-[500px]">
      <div className="mt-12 ms-6 text-xl font-bold">데구르르에 오신 것을 환영해요!</div>
      <div className="flex justify-center">
        <ImageInput myProfileImage={profileImage} profileImageUpdate={addProfileImage} />
      </div>
      <div className="mt-1 mb-11">
        <DataInput
          inputType="NICKNAME"
          pageType="SIGNUP"
          nicknameExists={nicknameExists}
          onChange={handleAddNickname}
        />
        <DataInput inputType="DESCRIPTION" pageType="SIGNUP" onChange={handleAddDescirpt} />
      </div>
      <div className="flex justify-center h-14">
        <FuncButton isBlue clickEvent={handleSignUpButton} value="시작하기" />
      </div>
    </div>
  );
};

export default SignUp;
