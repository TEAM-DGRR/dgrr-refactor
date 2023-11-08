'use client';
import character from '@/../../public/images/logo_character.png';
import Image from 'next/image';
import { FuncButton } from '@/components/FuncButton';
import { useEffect, useState } from 'react';
import { ExplainModal } from '@/components/elements/ExplainModal';
import { useAppSelector } from '@/store/hooks';
import { stompConfig } from '@/types/game';
import { useDispatch } from 'react-redux';
import { saveGameInfo } from '@/store/gameSlice';
import { publishMessage } from '@/components/Game/stomp';
import { useRouter } from 'next/navigation';

const GameLoading = () => {
  const [openModal, setOpenModal] = useState(false);
  const handleModal = () => {
    setOpenModal(!openModal);
  };

  const client = useAppSelector((state) => state.game.client);
  const { DESTINATION_URI } = stompConfig;
  const { GAME_URI, MATCHING_URI } = DESTINATION_URI;
  const dispatch = useDispatch();
  const router = useRouter();

  // 랜덤매칭
  const gameMatch = () => {
    if (client) {
      publishMessage(client, MATCHING_URI, '');
    }
  };

  const subscribeGame = () => {
    client?.subscribe(GAME_URI, (message) => {
      console.log('게임정보 받는 메세지: ', message.body);
      // 게임 정보 저장
      dispatch(saveGameInfo(JSON.parse(message.body)));
      // 게임 정보가 왔다면 매칭 페이지로 이동
      router.push('/game/match');
    });
  };

  useEffect(() => {
    subscribeGame();
    gameMatch();
  }, []);

  return (
    <div className="grid place-items-center bg-main-blue w-screen h-screen max-w-[500px]">
      {openModal && <ExplainModal onClose={handleModal} />}
      <Image alt="캐릭터" src={character} className="w-40 h-40" />
      <h1>게임 찾는 중...</h1>
      <FuncButton value="게임 설명" clickEvent={handleModal} small={true} />
    </div>
  );
};

export default GameLoading;
