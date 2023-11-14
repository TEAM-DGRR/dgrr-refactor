'use client';
import { publishMessage } from '@/components/Game/stomp';
import Header from '@/components/elements/Header';
import { useAppSelector } from '@/store/hooks';
import { saveRoomInfo } from '@/store/roomSlice';
import { roomStompConfig } from '@/types/room';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import character from '@/../../public/images/floating_bread_cropped.gif';
import Image from 'next/image';

const RommLoadingPage = () => {
  const client = useAppSelector((state) => state.game.client);
  const { ROOM_DESTINATION_URI } = roomStompConfig;
  const { ENTER_SUB_URI, ENTER_SEND_URI } = ROOM_DESTINATION_URI;
  const dispatch = useDispatch();
  const router = useRouter();
  const roomCode = useAppSelector((state) => state.room.roomCode);

  const subscribeEnter = () => {
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
  useEffect(() => {
    subscribeEnter();
  }, [client, roomCode]);

  return (
    <div className='bg-main-blue w-screen h-screen min-h-[580px] max-w-[500px]'>
      <Header headerType='MATCHING' />
      <div className='flex flex-col justify-between h-3/5 pt-10'>
        <div className='flex justify-center mb-10'>
          <Image alt='캐릭터' src={character} />
        </div>
        <div className='flex justify-center font-bold'>
          <h1>방 만드는 중</h1>
          <span className='animate-blink'>.</span>
          <span className='animate-blink2'>.</span>.<span className='animate-blink3'>.</span>
        </div>
      </div>
    </div>
  );
};

export default RommLoadingPage;
