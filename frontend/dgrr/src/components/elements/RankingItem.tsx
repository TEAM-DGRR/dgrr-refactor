import RankingMedal from './RankingMedal';

export type itemType = 'MY' | 'ALL';

interface RankingItemProps {
  itemType: itemType;
  item: {
    memberId: number;
    rating: number;
    rank: number;
    nickname: string;
    profileImage: string;
  };
}

const RankingItem = ({ itemType, item }: RankingItemProps) => {
  const profileImage =
    item.profileImage !== '' ? item.profileImage : '/images/default_profile_image.png';
  const medalColor =
    item.rating >= 1800
      ? '/images/Gold.png'
      : item.rating >= 1600
      ? '/images/Sliver.png'
      : '/images/Bronze.png';

  return (
    <div className='h-[80px] w-11/12 flex my-[10px]'>
      {itemType === 'MY' || item.rank > 3 ? (
        <div className='w-1/5 h-full flex justify-center items-center bg-main-blue rounded-l-lg'>
          <RankingMedal ranking={item.rank} />
        </div>
      ) : (
        <div className='w-1/5 h-full flex justify-center items-center bg-[#EAEEFF] rounded-l-lg'>
          <RankingMedal ranking={item.rank} />
        </div>
      )}
      <div className='w-4/5 flex items-center bg-white rounded-r-lg'>
        <img
          className='w-[60px] aspect-square rounded-full mx-3'
          src={profileImage}
          alt='프로필 사진'
        />
        <div className='inline-block me-3'>
          <div className='mb-1 font-semibold text-sm'>{item.nickname}</div>
          <div className='flex gap-x-2'>
            <div className='flex w-[24px] aspect-square'>
              <img src={medalColor} />
            </div>
            <div className='flex items-center text-sm'>{item.rating}</div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RankingItem;
