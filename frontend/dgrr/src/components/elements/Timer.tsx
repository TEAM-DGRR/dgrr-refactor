import { memo, useEffect, useState } from 'react';

export const Timer = memo(({ round }: { round: string }) => {
  // 30초
  const playTime = 30 * 1000;
  // 1초
  const INTERVAL = 1000;
  // 남은 시간
  const [leftTime, setLeftTime] = useState<number>(playTime);

  // ms 초로 변환
  const second = String(Math.floor((leftTime / 1000) % 60)).padStart(2, '0');

  useEffect(() => {
    const timer = setInterval(() => {
      setLeftTime((prevTime) => prevTime - INTERVAL);
    }, INTERVAL);

    if (leftTime <= 0) {
      // 0초가 되면 timer 종료
      clearInterval(timer);
      console.log('시간 끝');
    }

    return () => {
      clearInterval(timer);
    };
  }, [leftTime]);

  useEffect(() => {
    setLeftTime(30 * 1000);

    // const timeout = setTimeout(() => {
    //   setLeftTime(30 * 1000);
    // }, 1000); // 3초 뒤에 초기화

    return () => {
      // clearTimeout(timeout);
    };
  }, [round]);

  return <>{round && <p className='text-white font-bold text-2xl w-fit'>0:{second}</p>}</>;
});

Timer.displayName = 'Timer';
