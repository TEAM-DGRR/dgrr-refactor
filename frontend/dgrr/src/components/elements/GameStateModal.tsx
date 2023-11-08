import attackImg from '@/../public/images/match-attack.png';
import defenseImg from '@/../public/images/match-defense.png';
import waitImg from '@/../public/images/ico_bread_wink.png';
import Image from 'next/image';

interface GameStateInfo {
  when: 'START' | 'ROUND' | 'END';
  gameState?: 'FIRST' | 'SECOND';
  roundResult?: string;
}

export const GameStateModal = ({ when, gameState, roundResult }: GameStateInfo) => {
  return (
    <div className="z-10 bg-black/30 w-full h-full max-w-[500px] fixed top-0 flex justify-center items-center">
      <div className="w-4/5 h-1/2 max-h-[300px] bg-white rounded-lg border-2 border-black p-8 flex flex-col items-center justify-center">
        {when === 'END' ? (
          <div className="flex flex-col items-center">
            <Image src={waitImg} alt="대기 이미지" className="w-40 mb-6" />
            <p className="font-bold text-2xl">결과를 판정 중입니다!</p>
          </div>
        ) : (
          <div className="flex flex-col items-center">
            <Image
              src={
                when === 'START'
                  ? gameState === 'FIRST'
                    ? attackImg
                    : defenseImg
                  : gameState === 'FIRST'
                  ? defenseImg
                  : attackImg
              }
              alt="공방 이미지"
              className="w-40"
            />
            {when === 'ROUND' && (
              <p className="font-bold text-2xl">
                {gameState === 'SECOND'
                  ? roundResult === 'NO_LAUGH'
                    ? '웃음을 참았어요'
                    : '웃음을 참지 못했어요'
                  : roundResult === 'NO_LAUGH'
                  ? '상대가 웃음을 참았어요!'
                  : '상대가 웃었어요!'}
              </p>
            )}
            <p className="font-bold text-2xl">
              {when === 'START'
                ? gameState === 'FIRST'
                  ? '상대방을 웃기세요!'
                  : '웃음을 참아보세요!'
                : gameState === 'FIRST'
                ? '웃음을 참아보세요'
                : '상대방을 웃기세요!'}
            </p>
          </div>
        )}
      </div>
    </div>
  );
};
