'use client';
import { useAppSelector } from '@/store/hooks';
import { ChildMethods, stompConfig, IImageResult } from '@/types/game';
import { useEffect, useRef, useState } from 'react';
import { UserVideoComponent } from './videoComponent';
import { useDispatch } from 'react-redux';
import { saveGameResult, saveRound, saveRoundResult } from '@/store/gameSlice';
import { publishMessage } from '@/components/Game/stomp';
import { GameStateModal } from '@/components/elements/GameStateModal';
import { useRouter } from 'next/navigation';
import Header from '@/components/elements/Header';
import ProbabilityGauge from './probabilityGauge';
import attackImg from '@/../public/images/match-attack.png';
import defenseImg from '@/../public/images/match-defense.png';
import Image from 'next/image';
import Toast from '@/components/elements/Toast';

const PlayPage = () => {
  const client = useAppSelector((state) => state.game.client);
  const gameInfo = useAppSelector((state) => state.game.gameInfo);
  const gameRoomID = useAppSelector((state) => state.game.gameInfo.gameRoomId);
  const turn = useAppSelector((state) => state.game.gameInfo.turn);
  const openviduToken = useAppSelector((state) => state.game.gameInfo.openviduToken);
  const publisher = useAppSelector((state) => state.game.publisher);
  const subscriber = useAppSelector((state) => state.game.subscriber);
  const ws = useAppSelector((state) => state.game.websocket);
  const round = useAppSelector((state) => state.game.round);

  const { DESTINATION_URI } = stompConfig;
  const {
    FIRST_ROUND_GO_URI,
    FIRST_ROUND_NO_LAUGH_URI,
    FIRST_ROUND_LAUGH_URI,
    FIRST_ROUND_START_URI,
    SECOND_ROUND_GO_URI,
    SECOND_ROUND_LAUGH_URI,
    SECOND_ROUND_NO_LAUGH_URI,
    SECOND_ROUND_START_URI,
    ERROR_URI,
    RESULT_URI,
    STATUS_URI,
    END_URI,
    ENEMY_LEFT_URI,
  } = DESTINATION_URI;
  const [firstRoundResult, setFirstRoundResult] = useState('');
  const childRef = useRef<ChildMethods | null>(null);
  const dispatch = useDispatch();
  const [modalOpen, setModalOpen] = useState(true);
  const [when, setWhen] = useState<'START' | 'ROUND' | 'END'>('START');
  const router = useRouter();
  const [smileProbability, setSmileProbability] = useState<number>(0);
  const [recognition, setRecognition] = useState<boolean>(false);

  let intervalId: any;
  // 1라운드 관련 구독
  const subscribeFirstGame = () => {
    client?.subscribe(FIRST_ROUND_GO_URI, (message) => {
      // console.log('1라운드 메세지: ', message.body);
      if (message.body == 'START') {
        // console.log('1라운드 시작');
        setWhen('ROUND');
        client?.subscribe(STATUS_URI, (message) => {
          const imageResult: IImageResult = JSON.parse(message.body);
          // console.log('표정인식 결과: ', imageResult);
          setRecognition(imageResult.success);
          setSmileProbability(imageResult.smileProbability);
        });
        if (turn === 'SECOND') {
          // 표정 분석 결과
          // console.log('캡쳐 시작해줘');
          intervalId = setInterval(() => captureAndSend(1), 100);
        }
      }
    });
    client?.subscribe(FIRST_ROUND_NO_LAUGH_URI, (message) => {
      // console.log('1라운드 결과: ', message.body);
      if (message) {
        dispatch(saveRoundResult('NO_LAUGH'));
        setModalOpen(true);
        clearInterval(intervalId);
        if (turn === 'SECOND') {
          disconnectWs();
        }
        // 1초 후 modalOpen를 false로 설정
        setTimeout(() => {
          setModalOpen(false);
          subscribeSecondGame();
          dispatch(saveRound('second'));
        }, 2000);
      }
    });
    client?.subscribe(FIRST_ROUND_LAUGH_URI, (message) => {
      // console.log('1라운드 결과: ', message.body);
      dispatch(saveRoundResult(message.body));
      setModalOpen(true);
      if (turn === 'SECOND') {
        disconnectWs();
      }
      // 1초 후 modalOpen를 false로 설정
      setTimeout(() => {
        setModalOpen(false);
        subscribeSecondGame();
        dispatch(saveRound('second'));
      }, 2000);
    });

    // error
    client?.subscribe(ERROR_URI, (message) => {
      console.log('error: ', message);
    });

    // 게임 결과
    client?.subscribe(RESULT_URI, (message) => {
      // console.log(message.body);
      if (message.body) {
        dispatch(saveGameResult(JSON.parse(message.body)));
        client.deactivate();
        disconnectWs();
        router.push('/game/result');
      }
    });

    // 상대 탈주 정보
    client?.subscribe(ENEMY_LEFT_URI, (message) => {
      // console.log('상대 나감: ', message.body);
      dispatch(saveGameResult(JSON.parse(message.body)));
      client.deactivate();
      disconnectWs();
      router.push('/game/result');
    });
    firstRoundStart();
  };

  // 2라운드 관련 구독
  const subscribeSecondGame = () => {
    client?.subscribe(SECOND_ROUND_GO_URI, (message) => {
      // console.log('2라운드 메세지: ', message.body);
      if (message.body == 'START') {
        // console.log('2라운드 시작');
        if (turn === 'FIRST') {
          // 표정 분석 결과
          // console.log('캡쳐 시작해줘');
          intervalId = setInterval(() => captureAndSend(2), 100);
        }
      }
    });
    client?.subscribe(SECOND_ROUND_NO_LAUGH_URI, (message) => {
      // console.log('2라운드 결과: ', message.body);
      setWhen('END');
      clearInterval(intervalId);
      setModalOpen(true);
      gameEnd();
      setTimeout(() => {
        setModalOpen(false);
      }, 1000);
    });
    client?.subscribe(SECOND_ROUND_LAUGH_URI, (message) => {
      // console.log('2라운드 결과: ', message.body);
      setWhen('END');
      clearInterval(intervalId);
      gameEnd();
      setTimeout(() => {
        setModalOpen(false);
      }, 1000);
    });

    secondRoundStart();
  };

  // 1라운드 시작
  const firstRoundStart = () => {
    if (client) {
      // console.log('1라운드 시작한다고 메세지 보낼거임');
      // console.log(gameRoomID);
      publishMessage(client, FIRST_ROUND_START_URI, gameRoomID);
      dispatch(saveRound('first'));
    }
  };

  // 2라운드 시작
  const secondRoundStart = () => {
    if (client) {
      // console.log('2라운드 시작한다고 메세지 보낼거임');
      publishMessage(client, SECOND_ROUND_START_URI, gameRoomID);
    }
  };

  // 게임 종료
  const gameEnd = () => {
    if (client) {
      // console.log('게임 종료 메세지 보낼거임');
      publishMessage(client, END_URI, gameRoomID);
    }
  };

  // 웹소켓 해제
  const disconnectWs = () => {
    if (ws) {
      ws.close();
      // console.log('연결 해제');
    }
  };
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const canvasRef = useRef<HTMLCanvasElement | null>(null);

  // 웹캠 이미지 캡쳐 및 전송
  const capturePublisherScreen = () => {
    // console.log(2);
    // console.log('퍼블리셔: ', publisher);
    // console.log('Ref: ', canvasRef.current);

    if (publisher && canvasRef.current) {
      const videoElement = publisher.videos[0].video;
      const canvas = canvasRef.current;
      canvas.width = videoElement.videoWidth;
      canvas.height = videoElement.videoHeight;
      const ctx = canvas.getContext('2d');

      // Canvas에 비디오 화면 그리기
      if (ctx) {
        ctx.drawImage(videoElement, 0, 0, canvas.width, canvas.height);
      }

      // Canvas를 이미지로 저장
      const dataURL = canvas.toDataURL('image/jpeg');

      return dataURL;
    }
    return null;
  };

  const sendCapturedImage = (imageData: any, round: number) => {
    // console.log(5);
    // console.log(ws);
    if (ws && ws.readyState === WebSocket.OPEN) {
      const message = {
        image: imageData,
        header: {
          round: round, // 라운드 정보 설정
          gameSessionId: gameRoomID, // 게임 세션 ID 설정
        },
      };
      // console.log('이미지 보낸다');
      ws.send(JSON.stringify(message));
    }
  };
  const captureAndSend = (round: number) => {
    const capturedImage = capturePublisherScreen();
    if (capturedImage) {
      sendCapturedImage(capturedImage, round);
    }
  };

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      const parseJwt = (token: any) => {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace('-', '+').replace('_', '/');
        return JSON.parse(window.atob(base64));
      };
      const id = parseJwt(token).id;
      localStorage.setItem('memberId', id);
    } else {
      Toast.fire('로그인이 필요합니다!', '', 'warning');
      // 토큰 없으면 로그인 화면으로 보내기
      router.push('/');
    }
    const alertTimeout = setTimeout(() => {
      // console.log(turn);
      setModalOpen(false);
      subscribeFirstGame();
    }, 2000);

    return () => {
      clearTimeout(alertTimeout);
    };
  }, []);

  return (
    <div className='w-screen h-screen max-w-[500px] min-h-[565px] bg-black'>
      <Header headerType='GAME' />
      {modalOpen && <GameStateModal when={when} gameState={turn} />}
      <div className='relative'>
        <Image
          src={
            round === 'first'
              ? turn === 'FIRST'
                ? defenseImg
                : attackImg
              : turn === 'FIRST'
              ? attackImg
              : defenseImg
          }
          alt='공격상태'
          className='w-10 h-10 rounded-full bg-white absolute left-6 top-3 z-1'
        />
        <UserVideoComponent streamManager={subscriber} />
      </div>
      <div className='flex justify-center'>
        <ProbabilityGauge probability={smileProbability} />
        <div
          className={`w-4 h-4 rounded-full ml-3 ${recognition ? 'bg-green-500' : 'bg-red-600'}`}
        ></div>
      </div>
      <div className='relative'>
        {!modalOpen && (
          <Image
            src={
              round === 'first'
                ? turn === 'FIRST'
                  ? attackImg
                  : defenseImg
                : turn === 'FIRST'
                ? defenseImg
                : attackImg
            }
            alt='공격상태'
            className='w-10 h-10 rounded-full bg-white absolute left-6 top-3 z-10'
          />
        )}
        <UserVideoComponent ref={childRef} streamManager={publisher} />
      </div>
      <canvas ref={canvasRef} width='640' height='480' style={{ display: 'none' }} />
    </div>
  );
};

export default PlayPage;
