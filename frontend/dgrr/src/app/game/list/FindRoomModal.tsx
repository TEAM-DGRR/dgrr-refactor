'use client';
import { FormEvent, useState } from 'react';
import { IoCloseOutline } from 'react-icons/io5';

type FindModalProps = {
  handleModal: () => void;
};
export const FindRoomModal = ({ handleModal }: FindModalProps) => {
  const [roomNum, setRoomNum] = useState('');
  const onChangeRoomNum = (e: FormEvent<HTMLInputElement>) => {
    setRoomNum(e.currentTarget.value);
  };
  // 나중에 방 참여 API 연결 예정
  const submitRoomNum = () => {};

  return (
    <div className='z-10 bg-black/30 w-full h-full max-w-[500px] fixed top-0 flex justify-center items-center'>
      <div className='w-80 h-fit bg-white rounded-lg border-2 border-black p-4'>
        <div className='flex justify-between mb-3'>
          <div className='w-6'></div>
          <span className='font-bold text-base'>방 찾기</span>
          <button onClick={handleModal}>
            <IoCloseOutline size='24' />
          </button>
        </div>
        <form onSubmit={submitRoomNum}>
          <label>
            <input
              className='bg-[#F4F4F6] w-full text-xs p-4 rounded-lg mb-3'
              placeholder='참여 코드를 입력해주세요.'
              onChange={onChangeRoomNum}
            />
          </label>
          <div className='grid place-items-center'>
            <button className='bg-main-blue text-white rounded-lg py-2 px-4 w-fit'>확인</button>
          </div>
        </form>
      </div>
    </div>
  );
};
