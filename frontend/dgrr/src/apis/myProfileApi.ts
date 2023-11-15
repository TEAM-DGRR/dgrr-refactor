import axios from 'axios';
import { setUrl } from '@/utils/setUrl';

const apiUrl = `${setUrl}/member`;

export interface MemberProps {
  memberId: number;
  nickname: string;
  profileImage: string;
  description: string;
}

export interface GameHistoryProps {
  gameHistoryId: number;
  gameRoomId: number;
  gameResult: string;
  gameType: string;
  gameTime: number;
  holdingTime: number;
  ratingChange: number;
  highlightImage: string;
  createdAt: string;
  opponentNickname: string;
  opponentProfileImage: string;
  opponentDescription: string;
}

export interface GetMyInfoProps {
  member: MemberProps;
  ranking: {
    season: number;
    rating: number;
    rank: number;
    tier: string;
    score: number; // score 속성 추가
  };
  gameHistoryList: GameHistoryProps[];
}

export interface UpdateMyInfoProps {
  nickname: string | undefined;
  profileImage: string | undefined;
  description: string | undefined;
}

export const getMyInfoApi = async () => {
  const res = await axios.get<GetMyInfoProps>(`${apiUrl}/member-id`);
  return res.data;
};

export const getGameHistoryApi = async () => {
  const res = await axios.get<GameHistoryProps[]>(`${apiUrl}/game-history/member-id`);
  return res.data;
};

export const updateMyInfoApi = async (props: UpdateMyInfoProps) => {
  const res = await axios.put<MemberProps>(`${apiUrl}`, props);
  return res.data;
};

export const checkNicknameApi = async (nickname: string) => {
  const res = await axios.get(`${apiUrl}/nickname-check/${nickname}`);
  return res.data;
};
