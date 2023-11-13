'use client';

import { ChangeEvent, useEffect, useRef, useState } from 'react';
import Header from '@/components/elements/Header';
import ImageInput from '@/components/elements/ImageInput';
import DataInput from '@/components/elements/DataInput';
import Swal from 'sweetalert2';
import {
  UpdateMyInfoProps,
  checkNicknameApi,
  getMyInfoApi,
  updateMyInfoApi,
} from '@/apis/myProfileApi';
import { setUrl } from '@/utils/setUrl';
import axios from 'axios';
import ButtonClickAudio from '@/components/audio/ButtonClickAudio';

type UploadImg = {
  file: File | { buffer: Buffer; size: number; type: string };
  thumbnail: string;
  type: string;
};

const Edit = () => {
  const playsound = ButtonClickAudio();
  // 수정하기 전, 원래 닉네임
  const [myNickName, setMyNickname] = useState('');
  // 수정한 닉네임
  const [nowNickname, setNowNickName] = useState('');
  // 닉네임 존재 여부 확인
  const [nicknameExists, setNicknameExists] = useState(false);
  const [nowDescription, setNowDescription] = useState('');
  // const fileInputRef = useRef<HTMLInputElement | null>(null);
  const [nowProfileImage, setNowProfileImage] = useState<UploadImg | null>({
    file: {
      buffer: Buffer.from([]),
      size: 0,
      type: 'application/octet-stream',
    },
    thumbnail: '',
    type: '',
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await getMyInfoApi();
        setMyNickname(response.member.nickname);
        setNowNickName(response.member.nickname);
        setNowDescription(response.member.description);
        setNowProfileImage({
          file: new File([], ''),
          thumbnail: response.member.profileImage,
          type: '',
        });
        // response의 PromiseResult를 추출
        const { gameHistoryList, member, ranking } = response;
      } catch (error) {
        console.error('데이터 가져오기 실패:', error);
      }
    };

    fetchData();
  }, []);

  const checkNickname = async () => {
    try {
      const response = await checkNicknameApi(nowNickname);
      // 여기서 response를 처리할 추가적인 로직을 작성할 수 있습니다.
    } catch (error: any) {
      // 400 에러가 발생한 경우에 대한 처리
      if (myNickName !== nowNickname && error.response && error.response.status === 400) {
        setNicknameExists(true);
      }
    }
  };

  // 닉네임 중복 확인
  useEffect(() => {
    setNicknameExists(false);
    checkNickname();
  }, [nowNickname]);

  // 닉네임 입력값 반영
  const handleNicknameChange = (event: ChangeEvent<HTMLInputElement>) => {
    setNowNickName(event.target.value);
  };

  // 상태 메시지 입력값 반영
  const handleDescirptChange = (event: ChangeEvent<HTMLInputElement>) => {
    setNowDescription(event.target.value);
  };

  // 사진 업로드
  const uploadImg = (e: React.ChangeEvent<HTMLInputElement>) => {
    const fileList = e.target.files;

    if (fileList !== null && fileList.length > 0) {
      // 미리보기용으로 이미지 state에 저장
      const formData = new FormData();
      formData.append('file', fileList[0]);
      axios
        .post(`${setUrl}/file/upload`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        })
        .then((res: any) => {
          setNowProfileImage({
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

  // 닉네임이 중복되었거나 없을 때, 모달 뜸
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
  const handleSaveButton = async () => {
    playsound();
    const data: UpdateMyInfoProps = {
      nickname: nowNickname,
      profileImage: nowProfileImage?.thumbnail,
      description: nowDescription,
    };
    try {
      await updateMyInfoApi(data);
      // 여기서 정상적인 처리를 수행
      const newPathname = '/myprofile';
      window.location.href = newPathname;
    } catch (error) {
      // 에러가 발생한 경우
      requestNewNicknameModal();
    }
  };

  // 회원탈퇴 확인 모달
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

  return (
    <div className='w-screen max-w-[500px]'>
      <Header headerType='OTHER'>프로필 수정</Header>
      <div>
        <ImageInput myProfileImage={nowProfileImage?.thumbnail} profileImageUpdate={uploadImg} />
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
      {/* <div
        onClick={openWithdrawModal}
        className='flex justify-center mt-40 mb-3 text-xs font-semibold cursor-hover hover:text-[#E83F57]'
      >
        회원 탈퇴
      </div> */}
    </div>
  );
};

export default Edit;
