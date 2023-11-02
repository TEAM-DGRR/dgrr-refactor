// 며칠 전인지 계산하는 함수

import { useState, useEffect } from 'react';

const useTimeStamp = (timestamp: string | number) => {
  const recordMonth = new Date(timestamp).getMonth() + 1
  const [timeAgo, setTimeAgo] = useState('');

  const updateTimeStamp = () => {
    // 경과한 시간 계산 (1초 = 1000)
    const timeOut = Math.floor((new Date().getTime() - new Date(timestamp).getTime()) / 1000);

    if (timeOut < 60 * 5) {
      setTimeAgo(`방금 전`);
    } else if (timeOut < 60 * 60) {
      const minutes = Math.floor(timeOut / 60);
      setTimeAgo(`${minutes}분 전`);
    } else if (timeOut < 60 * 60 * 24) {
      const hours = Math.floor(timeOut / (60 * 60));
      setTimeAgo(`${hours}시간 전`);
    } else if (timeOut < 60 * 60 * 24 * 7) {
      const days = Math.floor(timeOut / (60 * 60 * 24));
      setTimeAgo(`${days}일 전`);
    } else if (timeOut < 60 * 60 * 24 * 31) {
      const days = Math.floor(timeOut / (60 * 60 * 24 * 7));
      setTimeAgo(`${days}주 전`);
    } else if (timeOut < 60 * 60 * 24 * 61) {
      if (recordMonth === 1 || 2 || 3 || 5 || 7 || 8 || 10 || 12) {
        const days = Math.floor(timeOut / (60 * 60 * 24 * 30));
        setTimeAgo(`${days}달 전`); 
      } else {
        const days = Math.floor(timeOut / (60 * 60 * 24 * 31));
        setTimeAgo(`${days}달 전`); 
      }
    } else {
      // 일 년 이상 지난 전적에 대해서는 오래전으로 표기
      // const date = new Date(timestamp).toISOString().slice(0, 10);
      setTimeAgo('오래전');
    }
  };

  useEffect(() => {
    updateTimeStamp();
  }, [timestamp]);
  
  return timeAgo;
};

export default useTimeStamp;