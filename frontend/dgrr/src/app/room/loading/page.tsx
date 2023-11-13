'use client';
import { publishMessage } from '@/components/Game/stomp';
import { useAppSelector } from '@/store/hooks';
import { saveRoomInfo } from '@/store/roomSlice';
import { roomStompConfig } from '@/types/room';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';
import { useDispatch } from 'react-redux';

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

  return <div>방 만드는 중~~</div>;
};

export default RommLoadingPage;
