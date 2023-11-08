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
    gameResult: 'Draw',
    afterRating: 0,
    afterRank: 'BRONZE',
  },
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
      state.gameResult = action.payload;
    },
  },
});

export default gameSlice.reducer;
export const { saveGameInfo, createClient, saveGameResult } = gameSlice.actions;
