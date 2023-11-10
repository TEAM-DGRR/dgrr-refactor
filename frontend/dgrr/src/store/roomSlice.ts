import { RoomType } from '@/types/room';
import { createSlice } from '@reduxjs/toolkit';

const initialState: RoomType = {
  roomInfo: [
    {
      roomId: '',
      waitingMember: {
        waitingMemberId: '',
        nickname: '',
        profileImage: '',
        ready: false,
      },
    },
  ],
  roomCode: '',
  roomReady: false,
};

const roomSlice = createSlice({
  name: 'roomSlice',
  initialState,
  reducers: {
    roomReset: () => {
      return initialState;
    },
    saveRoomInfo: (state, action) => {
      console.log('방 정보 저장: ', action.payload);
      state.roomInfo = action.payload;
    },
    saveRoomCode: (state, action) => {
      console.log('룸 코드: ', action.payload);
      state.roomCode = action.payload;
    },
    setReady: (state, action) => {
      console.log('준비 상태: ', action.payload);
      state.roomReady = action.payload;
    },
  },
});

export default roomSlice.reducer;
export const { roomReset, saveRoomInfo, saveRoomCode, setReady } = roomSlice.actions;
