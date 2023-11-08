'use client';

import { IoCamera } from 'react-icons/io5';

interface ImageInputProps {
  myProfileImage: string;
  profileImageUpdate: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

const ImageInput = ({ myProfileImage, profileImageUpdate }: ImageInputProps) => {
  return (
    <div className='px-6 py-8'>
      <div className='w-[121px] relative'>
        <img
          className='w-[121px] aspect-square rounded-full'
          src={myProfileImage}
          alt='기본 사진'
        />
        <label
          htmlFor='fileInput'
          className='absolute bottom-0 right-0 bg-[#E0E0E0] w-[25px] aspect-square rounded-full flex justify-center items-center cursor-hover hover:text-main-blue'
        >
          <IoCamera fontSize={'17px'} />
        </label>
        {/* display를 none으로 함으로써, 원래 파일 업로드 버튼을 없앰 */}
        <input
          type='file'
          id='fileInput'
          style={{ display: 'none' }}
          accept='img/*'
          onChange={profileImageUpdate}
        />
      </div>
    </div>
  );
};

export default ImageInput;
