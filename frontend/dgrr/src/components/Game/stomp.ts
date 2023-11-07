import { Client, StompHeaders } from '@stomp/stompjs';
import { IGameConfig, stompConfig } from '@/types/game';

const { DESTINATION_URI } = stompConfig;
// eslint-disable-next-line
const { MATCHING_URI, GAME_URI, IMAGE_DATA_URI, STATUS_URI, END_URI, RESULT_URI } = DESTINATION_URI;

export const gameSubscribe = (client: Client, destination: string) => {
  console.log('경로 확인: ', destination);
  console.log('client 확인: ', client);
  console.log('client 연결 확인: ', client.connected);

  client.subscribe(destination, (message) => {
    console.log('메세지:', message);
    console.log(message.body);
  });
};

// 게임 정보 받아오기
export const getGameConfig = (client: Client) => {
  return new Promise<IGameConfig>((resolve) => {
    console.log(GAME_URI, '를 구독합니다. ');
    client.subscribe(GAME_URI, (message) => {
      console.log(' 게임 메시지 : ' + message);
      const gameConfig = JSON.parse(message.body);
      resolve(gameConfig);
    });
  });
};

// 라운드 시작 정보 받아오기
export const getRoundStart = (client: Client, destination: string) => {
  return new Promise((resolve) => {
    console.log(destination, '구독');
  });
};

export const onStompError = (client: Client, callback: Function) => {
  client.onStompError = (frame) => {
    callback();
    console.log('Broker reported error: ' + frame.headers['message']);
    console.log('Additional details: ' + frame.body);
  };
};

export const publishMessage = (client: Client, destination: string, body: string) => {
  client.publish({ destination, body });
};
