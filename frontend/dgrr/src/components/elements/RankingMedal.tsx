import Image from 'next/image';

interface RankingMedalProps {
  ranking: number;
}

const RankingMedal = ({ ranking }: RankingMedalProps) => {
  const rankingTrophy =
    ranking === 1
      ? '/images/ranking_1.png'
      : ranking === 2
      ? '/images/ranking_2.png'
      : '/images/ranking_3.png';
  const rankingName = ranking === 1 ? '1등' : ranking === 2 ? '2등' : '3등';

  return (
    <div>
      {ranking <= 3 ? (
        <Image
          src={rankingTrophy}
          alt={rankingName}
          width={500}
          height={500}
          className='w-[60px] aspect-square'
        />
      ) : (
        <div className='text-3xl font-bold'>{ranking}</div>
      )}
    </div>
  );
};

export default RankingMedal;
