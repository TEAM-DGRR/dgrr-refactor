import { Client, StompHeaders } from '@stomp/stompjs';
import { Publisher, StreamManager, Subscriber } from 'openvidu-browser';

export type GameType = {
  client: Client | undefined;
  gameInfo: IGameConfig;
  gameResult: IGameResult;
  publisher: Publisher | undefined;
  subscriber: Subscriber | undefined;
  websocket: WebSocket | undefined;
  roundResult: string;
};

export interface IGameConfig {
  // 게임 정보
  turn: 'FIRST' | 'SECOND';
  gameRoomId: string;
  openviduToken: string;

  // 유저 정보
  myInfo: IMemberInfo;
  enemyInfo: IMemberInfo;
}

// Stomp
export const stompConfig = {
  DESTINATION_URI: {
    // 랜덤 매칭
    MATCHING_URI: '/send/matching',
    // 캡처 화면 전송
    IMAGE_DATA_URI: '/send/capture',
    // 1라운드 시작
    FIRST_ROUND_START_URI: '/send/firstroundstart',
    // 2라운드 시작
    SECOND_ROUND_START_URI: '/send/secondroundstart',
    // 게임 종료 전송
    END_URI: '/send/game-end',
    // 게임 탈주 전송
    EXIT_URI: '/send/game-leave',

    // 게임 시작 정보
    GAME_URI: '/user/recv/game-start',
    // 표정/얼굴 인식 정보
    STATUS_URI: '/user/recv/capture-result',
    // 게임 결과 정보
    RESULT_URI: '/user/recv/game-result',
    // 1라운드 시작 정보
    FIRST_ROUND_GO_URI: '/user/recv/firstroundstart',
    // 1라운드 시간초과 정보
    FIRST_ROUND_NO_LAUGH_URI: '/user/recv/firstroundend-no-laugh',
    // 1라운드 웃음 정보
    FIRST_ROUND_LAUGH_URI: '/user/recv/firstroundend-laugh',
    // 2라운드 시작 정보
    SECOND_ROUND_GO_URI: '/user/recv/secondroundstart',
    // 2라운드 시간초과 정보
    SECOND_ROUND_NO_LAUGH_URI: '/user/recv/secondroundend-no-laugh',
    // 2라운드 웃음 정보
    SECOND_ROUND_LAUGH_URI: '/user/recv/secondroundend-laugh',
    // 에러 처리
    ERROR_URI: '/user/recv/errors',
    // 상대방 탈주 정보
    ENEMY_LEFT_URI: '/user/recv/enemy-left',
  },
  CAPTURE_INTERVAL: 500,
};

//OpenVidu
export const openViduConfig = {
  APPLICATION_SERVER_URL: '',
  PUBLISHER_PROPERTIES: {
    audioSource: undefined,
    videoSource: undefined,
    publishAudio: true,
    publishVideo: true,
    resolution: '1280x720',
    frameRate: 60,
    insertMode: 'APPEND',
    mirror: true,
  },
};

// 유저 관련
export interface IMemberInfo {
  nickname: string;
  profileImage: string;
  description: string;
  rating: number;
  rank: 'BRONZE' | 'SILVER' | 'GOLD';
}

// 이미지 분석 결과
export interface IImageResult {
  success: string;
  emotion: string;
  probability: number;
  smileProbability: number;
}

// 라운드 정보
export interface IGameStatus {
  status: 'START';
  result: 'LAUGH' | 'TIME OUT' | 'NO_LAUGH';
}

// 게임 결과
export interface IGameResult {
  // 유저 정보
  myInfo: IMemberInfo;
  enemyInfo: IMemberInfo;

  // 게임 결과
  highlightImage: string;
  gameResult: 'WIN' | 'LOSE' | 'DRAW';

  // 점수 변화
  afterRating: number;
  afterTier: 'BRONZE' | 'SILVER' | 'GOLD';
}

export interface ChildMethods {
  getVideoElement: () => HTMLVideoElement | null;
}

export interface IGamePlayProps {
  stompClient: Client | undefined;
  setStompClient: React.Dispatch<React.SetStateAction<Client | undefined>>;
  isStompConnected: boolean;
  getGameConfiguration: (client: Client) => Promise<IGameConfig>;
  setGameConfig: React.Dispatch<React.SetStateAction<IGameConfig>>;
  connectStompClient: (headers: StompHeaders) => Promise<Client>;
}

export interface IUserVideoComponent {
  streamManager: StreamManager | undefined;
}

export interface ChildMethods {
  getVideoElement: () => HTMLVideoElement | null;
}
