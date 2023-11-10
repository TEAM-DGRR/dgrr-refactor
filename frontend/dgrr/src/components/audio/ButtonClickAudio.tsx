import { useRef, useEffect } from 'react';

const ButtonClickAudio = () => {

  const audioRef = useRef<HTMLAudioElement | null>(null);

  useEffect(() => {
    audioRef.current = new Audio('/audio/button-click.mp3');
    audioRef.current.preload = 'auto';
  }, []);

  const playSound = () => {
    // 현재 오디오가 있는지 체크하고 재생 시도
    if (audioRef.current) {
      audioRef.current.play().catch(e => console.error('Error playing sound:', e));
    }
  };

  return playSound;
};

export default ButtonClickAudio;
