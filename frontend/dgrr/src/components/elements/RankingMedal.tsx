interface RankingMedalProps {
  ranking: number;
}

const RankingMedal = ({ ranking }: RankingMedalProps) => {
  return (
    <div>
      {ranking === 1 ? (
        <img className='w-[60px] aspect-square' src='/images/ranking_1.png' alt='1등' />
      ) : ranking === 2 ? (
        <img className='w-[60px] aspect-square' src='/images/ranking_2.png' alt='2등' />
      ) : ranking === 3 ? (
        <img className='w-[60px] aspect-square' src='/images/ranking_3.png' alt='3등' />
      ) : (
        <div className='text-3xl font-bold'>{ranking}</div>
      )}
    </div>
  );
};

export default RankingMedal;
