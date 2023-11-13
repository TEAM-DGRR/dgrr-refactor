'use client';

import { ChangeEvent, useEffect, useState } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import { FuncButton } from '@/components/FuncButton';
import DataInput from '@/components/elements/DataInput';
import ImageInput from '@/components/elements/ImageInput';
import Swal from 'sweetalert2';
import axios from 'axios';
import { setUrl } from '../../utils/setUrl';
import ButtonClickAudio from '@/components/audio/ButtonClickAudio';
import { checkNicknameApi } from '@/apis/myProfileApi';

type UploadImg = {
  file: File | { buffer: Buffer; size: number; type: string };
  thumbnail: string;
  type: string;
};

const SignUp = () => {
  const playsound = ButtonClickAudio();
  const router = useRouter();
  const [nickname, setNickName] = useState('');
  // 닉네임 중복 여부
  const [nicknameExists, setNicknameExists] = useState(false);
  const [description, setDescription] = useState('');
  const [profileImage, setProfileImage] = useState<UploadImg | null>({
    file: {
      buffer: Buffer.from([]),
      size: 0,
      type: 'application/octet-stream',
    },
    thumbnail: '/images/default_profile_image.png',
    type: '',
  });

  // useState('/images/default_profile_image.png');

  const searchParams = useSearchParams();
  let kakaoId = searchParams.get('id');

  const checkNickname = async () => {
    try {
      const response = await checkNicknameApi(nickname);
      // 여기서 response를 처리할 추가적인 로직을 작성할 수 있습니다.
    } catch (error: any) {
      // 400 에러가 발생한 경우에 대한 처리
      if (error.response && error.response.status === 400) {
        setNicknameExists(true);
      }
    }
  };

  // 닉네임 중복 확인
  useEffect(() => {
    setNicknameExists(false);
    checkNickname();
  }, [nickname]);

  // 닉네임 입력값 반영
  const handleAddNickname = (event: ChangeEvent<HTMLInputElement>) => {
    setNickName(event.target.value);
  };

  // 상태 메시지 입력값 반영
  const handleAddDescirpt = (event: ChangeEvent<HTMLInputElement>) => {
    setDescription(event.target.value);
  };

  // 사진 업로드
  const uploadImg = (e: React.ChangeEvent<HTMLInputElement>) => {
    const fileList = e.target.files;

    if (fileList !== null && fileList.length > 0) {
      const formData = new FormData();
      formData.append('file', fileList[0]);
      axios
        .post(`${setUrl}/file/upload`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        })
        .then((res: any) => {
          setProfileImage({
            file: fileList[0],
            thumbnail: res.data,
            type: fileList[0].type.slice(0, 5),
          });
        })
        .catch((err: any) => {
          console.log(err);
        });
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

  // 회원가입 버튼
  // 닉네임이 입력되지 않았거나 중복되면 경고 모달창이 뜸
  const handleSignUpButton = () => {
    playsound();
    if (nicknameExists !== true && nickname !== '') {
      axios
        .post(
          `${setUrl}/member`,
          {
            kakaoId: kakaoId,
            nickname: nickname,
            profileImage: profileImage?.thumbnail,
            description: description,
          },
          {
            withCredentials: false,
          }
        )
        .then((res: any) => {
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
    <div className='w-screen max-w-[500px]'>
      <div className='mt-12 ms-6 text-xl font-bold'>데구르르에 오신 것을 환영해요!</div>
      <div className='flex justify-center'>
        <ImageInput myProfileImage={profileImage?.thumbnail} profileImageUpdate={uploadImg} />
      </div>
      <div className='mt-1 mb-11'>
        <DataInput
          inputType='NICKNAME'
          pageType='SIGNUP'
          nicknameExists={nicknameExists}
          onChange={handleAddNickname}
        />
        <DataInput inputType='DESCRIPTION' pageType='SIGNUP' onChange={handleAddDescirpt} />
      </div>
      <div className='flex justify-center h-14'>
        <FuncButton isBlue clickEvent={handleSignUpButton} value='시작하기' />
      </div>
    </div>
  );
};

export default SignUp;
