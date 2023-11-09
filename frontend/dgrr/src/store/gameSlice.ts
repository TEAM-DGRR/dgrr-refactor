import { GameType } from '@/types/game';
import { createSlice } from '@reduxjs/toolkit';

const initialState: GameType = {
  client: undefined,
  gameInfo: {
    turn: 'FIRST',
    gameRoomId: '',
    openviduToken: '',
    myInfo: { nickname: '', profileImage: '', description: '', rating: 0, rank: 'BRONZE' },
    enemyInfo: { nickname: '', profileImage: '', description: '', rating: 0, rank: 'BRONZE' },
  },
  gameResult: {
    myInfo: { nickname: '', profileImage: '', description: '', rating: 0, rank: 'BRONZE' },
    enemyInfo: { nickname: '', profileImage: '', description: '', rating: 0, rank: 'BRONZE' },
    highlightImage: '',
    gameResult: 'DRAW',
    afterRating: 0,
    afterTier: 'BRONZE',
  },
  publisher: undefined,
  subscriber: undefined,
  websocket: undefined,
  roundResult: '',
};

const gameSlice = createSlice({
  name: 'gameSlice',
  initialState,
  reducers: {
    reset: () => initialState,
    saveGameInfo: (state, action) => {
      console.log(action.payload);
      const newGameInfo = action.payload;
      state.gameInfo = newGameInfo;
    },
    createClient: (state, action) => {
      // console.log(action.payload)
      state.client = action.payload;
    },
    saveGameResult: (state, action) => {
      console.log('총 결과: ', action.payload)
      state.gameResult = action.payload;
    },
    savePublisher: (state, action) => {
      console.log('퍼블리셔 오나요?', action.payload);
      state.publisher = action.payload;
    },
    saveSubscriber: (state, action) => {
      console.log('섭스도 오나요?', action.payload);
      state.subscriber = action.payload;
    },
    saveWebsocket: (state, action) => {
      console.log('웹소켓 오나요?', action.payload);
      state.websocket = action.payload;
    },
    saveRoundResult: (state, action) => {
      console.log('게임 결과 왜 안옴?: ', action.payload)
      state.roundResult = action.payload;
    },
  },
});

export default gameSlice.reducer;
export const {
  saveGameInfo,
  createClient,
  saveGameResult,
  savePublisher,
  saveSubscriber,
  saveWebsocket,
  saveRoundResult,
} = gameSlice.actions;
