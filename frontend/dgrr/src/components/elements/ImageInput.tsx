'use client';

import { useState } from 'react';
import { IoCamera } from 'react-icons/io5';

interface ImageInputProps {
  myProfileImage: string;
}

const ImageInput = ({ myProfileImage }: ImageInputProps) => {
  const [nowProfileImage, setNowProfileImage] = useState(myProfileImage);

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

  return (
    <div className='px-6 py-8'>
      <div className='w-[121px] relative'>
        <img
          className='w-[121px] aspect-square rounded-full'
          src={nowProfileImage}
          alt='기본 사진'
        />
        <label
          htmlFor='fileInput'
          className='absolute bottom-0 right-0 bg-[#E0E0E0] w-[25px] aspect-square rounded-full flex justify-center items-center cursor-pointer hover:text-main-blue'
        >
          <IoCamera fontSize={'17px'} />
        </label>
        {/* display를 none으로 함으로써, 원래 파일 업로드 버튼을 없앰 */}
        <input
          type='file'
          id='fileInput'
          style={{ display: 'none' }}
          accept='img/*'
          onChange={changeProfileImage}
        />
      </div>
    </div>
  );
};

export default ImageInput;
