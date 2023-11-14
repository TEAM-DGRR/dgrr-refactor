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
    deleteMember: (state, action) => {
      console.log(action.payload);
      console.log('확인작업 중', action.payload.waitingMember);
      console.log('멤버 제거');
      const findId = action.payload.waitingMember.waitingMemberId;
      console.log(findId);
      const indexToRemove = state.roomInfo.findIndex(
        (member) => member.waitingMember.waitingMemberId === findId
      );
      console.log(indexToRemove);

      if (indexToRemove !== -1) {
        console.log('지운다');
        state.roomInfo.splice(indexToRemove, 1);
        console.log(state.roomInfo[0]);
      }
    },
  },
});

export default roomSlice.reducer;
export const { roomReset, saveRoomInfo, saveRoomCode, setReady, deleteMember } = roomSlice.actions;
