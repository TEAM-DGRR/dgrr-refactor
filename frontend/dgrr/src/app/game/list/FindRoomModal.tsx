'use client';
import { publishMessage } from '@/components/Game/stomp';
import { useAppSelector } from '@/store/hooks';
import { saveRoomInfo } from '@/store/roomSlice';
import { roomStompConfig } from '@/types/room';
import { useRouter } from 'next/navigation';
import { FormEvent, useState } from 'react';
import { IoCloseOutline } from 'react-icons/io5';
import { useDispatch } from 'react-redux';

type FindModalProps = {
  handleModal: () => void;
};
export const FindRoomModal = ({ handleModal }: FindModalProps) => {
  const [roomCode, setRoomCode] = useState('');
  const onChangeRoomNum = (e: FormEvent<HTMLInputElement>) => {
    setRoomCode(e.currentTarget.value);
  };
  const dispatch = useDispatch();
  const router = useRouter();
  const client = useAppSelector((state) => state.game.client);
  const { ROOM_DESTINATION_URI } = roomStompConfig;
  const { ENTER_SUB_URI, ENTER_SEND_URI } = ROOM_DESTINATION_URI;

  const submitRoomNum = (event: FormEvent) => {
    event.preventDefault();
    if (client) {
      client.subscribe(ENTER_SUB_URI, (message) => {
        const content = JSON.parse(message.body);
        console.log('방 정보: ', content);
        dispatch(saveRoomInfo(content));
        router.push('/room');
      });
      publishMessage(client, ENTER_SEND_URI, roomCode);
    }
  };

  return (
    <div className='z-10 bg-black/30 w-full h-full max-w-[500px] fixed top-0 flex justify-center items-center'>
      <div className='w-80 h-fit bg-white rounded-lg border-2 border-black p-4'>
        <div className='flex justify-between mb-3'>
          <div className='w-6'></div>
          <span className='font-bold text-base'>방 찾기</span>
          <button onClick={handleModal} className='cursor-hover'>
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
            <button className='bg-main-blue text-white rounded-lg py-2 px-4 w-fit cursor-hover'>확인</button>
          </div>
        </form>
      </div>
    </div>
  );
};
