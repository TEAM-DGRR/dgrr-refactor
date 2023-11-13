'use client';

import { ChangeEvent, useEffect, useState } from 'react';
import Header from '@/components/elements/Header';
import ImageInput from '@/components/elements/ImageInput';
import DataInput from '@/components/elements/DataInput';
import Swal from 'sweetalert2';
import { UpdateMyInfoProps, getMyInfoApi, updateMyInfoApi } from '@/apis/myProfileApi';
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
  const [isNicknameChanged, setIsNicknameChanged] = useState(false);
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
        axios.defaults.headers.common['Authorization'] = localStorage.getItem('token');
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

  const checkNickname = () => {
    if (nowNickname === myNickName) {
      didNotChangeModal();
    } else {
      if (nowNickname.length === 0) {
        requestNewNicknameModal();
      } else {
        axios
          .get(`${setUrl}/member/nickname-check/${nowNickname}`)
          .then((res: any) => {
            console.log(JSON.stringify(res));
            youCanUseThisNicknameModal();
            setIsNicknameChanged(false);
          })
          .catch((err: any) => {
            requestNewNicknameModal();
          });
      }
    }
  };

  // 닉네임 입력값 반영
  const handleNicknameChange = (event: ChangeEvent<HTMLInputElement>) => {
    setNowNickName(event.target.value);
    setIsNicknameChanged(true);
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

  // 닉네임 체크 안 하면 모달 뜸
  const requestNicknameCheckWarningModal = () => {
    Swal.fire({
      width: 400,
      title: `닉네임 중복 확인 후 눌러주세요.`,
      icon: 'error',
      confirmButtonColor: '#469FF6',
      confirmButtonText: '확인',
      customClass: {
        confirmButton: 'custom-confirm-button',
      },
    });
  };

  // 닉네임 사용 가능
  const youCanUseThisNicknameModal = () => {
    Swal.fire({
      width: 400,
      title: `사용 가능한 닉네임입니다.`,
      icon: 'success',
      confirmButtonColor: '#469FF6',
      confirmButtonText: '확인',
      customClass: {
        confirmButton: 'custom-confirm-button',
      },
    });
  };

  // 닉네임이 바뀌지 않음
  const didNotChangeModal = () => {
    Swal.fire({
      width: 400,
      title: `이전에 사용하던 닉네임입니다.`,
      icon: 'question',
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
    if (isNicknameChanged) {
      requestNicknameCheckWarningModal();
    } else {
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
        <div className='px-6 mb-7 '>
          <div className='mb-2 font-semibold'>닉네임</div>

          <div>
            <div className='text-xs text-[#767676] mb-[10px]'>
              한글/영어/숫자 최소 2자~최대 12자 가능
            </div>
            <div className='flex'>
              <input
                type='text'
                value={nowNickname}
                onChange={handleNicknameChange}
                minLength={2}
                maxLength={12}
                className='bg-[#F4F4F6] w-full text-xs p-4 rounded-lg focus:outline-none focus:ring focus:ring-main-blue'
              />
              <span onClick={checkNickname} className='min-w-fit ml-3 pl-2 pr-2 cursor-hover flex items-center bg-gray-300 rounded-lg hover:brightness-110'>
                <div className='text-black text-center text-sm font-bold'>중복 확인</div>
              </span>
            </div>
          </div>

          {/* 닉네임이 중복되면 경고 문구 뜸 */}
          {nicknameExists === true ? (
            <div className='text-xs h-4 pt-2 ms-1 text-red-500'>이미 존재하는 닉네임입니다</div>
          ) : (
            <div className='text-xs h-4 pt-2'></div>
          )}
        </div>
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
