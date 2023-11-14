'use client';
import { IoImageOutline } from 'react-icons/io5';
import useTimeStamp from '@/hooks/useTimeStamp';

export type pageType = 'PROFILE' | 'RECENT-RECORD';

interface RecentRecordItemProps {
  pageType: pageType;
  item: {
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
  };
}

const RecentRecordItem = ({ pageType, item }: RecentRecordItemProps) => {
  const itemHeight = pageType === 'RECENT-RECORD' ? 'h-[50px]' : 'h-[42px]';
  const borderColor =
    item.gameResult === 'WIN'
      ? 'border-[#5383E8]'
      : item.gameResult === 'LOSE'
      ? 'border-[#E83F57]'
      : 'border-[#868E96]';
  const backgroundColor =
    item.gameResult === 'WIN'
      ? 'bg-[#5383E8]'
      : item.gameResult === 'LOSE'
      ? 'bg-[#E83F57]'
      : 'bg-[#868E96]';
  const itemText = item.gameResult === 'WIN' ? '승' : item.gameResult === 'LOSE' ? '패' : '무';
  const textColor =
    item.ratingChange > 0
      ? 'text-[#5383E8]'
      : item.ratingChange < 0
      ? 'text-[#E83F57]'
      : 'text-black';
  const getOrLose = item.ratingChange >= 0 ? '획득' : '상실';

  return (
    <div>
      <div
        className={`flex ${itemHeight} min-w-[305px] mb-3 justify-between rounded-lg border-2 ${borderColor}`}
      >
        <div className='gap-x-[12px] flex items-center ms-2'>
          <div
            className={`${backgroundColor} w-[24px] h-[24px] text-white text-center rounded-full font-semibold`}
          >
            {itemText}
          </div>
          <img
            src={item.opponentProfileImage}
            alt='상대방 프로필 이미지'
            className='w-[24px] h-[24px] rounded-full inline-block'
          />
          <div className='text-sm ms-1 inline-block'>
            <div className='font-semibold'>{item.opponentNickname}</div>
            {pageType === 'RECENT-RECORD' ? (
              <div className={`text-xs ${textColor}`}>
                나의 {getOrLose} 점수 : {item.ratingChange}
              </div>
            ) : null}
          </div>
        </div>
        <div className='flex items-center me-2'>
          {item.gameResult !== 'DRAW' ? (
            <div>
              {pageType === 'RECENT-RECORD' ? (
                <IoImageOutline fontSize={'24px'} />
              ) : null}
            </div>
          ) : null}
          <div className='text-sm inline-block w-[69px] text-right'>
            {useTimeStamp(item.createdAt)}
          </div>
        </div>
      </div>
    </div>
  );
};

export default RecentRecordItem;
