'use client';
import { useAppSelector } from '@/store/hooks';
import { ChildMethods, stompConfig } from '@/types/game';
import { useEffect, useRef, useState } from 'react';
import { UserVideoComponent } from './videoComponent';
import { Device, OpenVidu, Publisher, Subscriber, Session } from 'openvidu-browser';
import { useDispatch } from 'react-redux';
import { saveGameResult } from '@/store/gameSlice';
import { publishMessage } from '@/components/Game/stomp';
import { initGame, joinSession } from '@/components/Game/openVidu';
import { GameStateModal } from '@/components/elements/GameStateModal';
import { useRouter } from 'next/navigation';
import Header from '@/components/elements/Header';

const PlayPage = () => {
  const client = useAppSelector((state) => state.game.client);
  const gameInfo = useAppSelector((state) => state.game.gameInfo);
  const gameRoomID = useAppSelector((state) => state.game.gameInfo.gameRoomId);
  const turn = useAppSelector((state) => state.game.gameInfo.turn);
  const openviduToken = useAppSelector((state) => state.game.gameInfo.openviduToken);

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
  } = DESTINATION_URI;
  const [firstRoundResult, setFirstRoundResult] = useState('');
  const [secondRoundResult, setSecondRoundResult] = useState('');
  const childRef = useRef<ChildMethods | null>(null);
  const dispatch = useDispatch();
  const [modalOpen, setModalOpen] = useState(true);
  const [when, setWhen] = useState<'START' | 'ROUND' | 'END'>('START');
  const [round, setRound] = useState(0);
  const router = useRouter();

  let intervalId: any;
  // 1라운드 관련 구독
  const subscribeFirstGame = () => {
    client?.subscribe(FIRST_ROUND_GO_URI, (message) => {
      console.log('1라운드 메세지: ', message.body);
      if (message.body == 'START') {
        console.log('1라운드 시작');
        setRound(1);
        setWhen('ROUND');
        if (turn === 'SECOND') {
          console.log('캡쳐 시작해줘');
          intervalId = setInterval(captureAndSend, 1000);
        }
      }
    });
    client?.subscribe(FIRST_ROUND_NO_LAUGH_URI, (message) => {
      console.log('1라운드 결과: ', message.body);
      if (message) {
        setFirstRoundResult(message.body);
        subscribeSecondGame();
        setModalOpen(true);
        clearInterval(intervalId);
        // 1초 후 modalOpen를 false로 설정
        setTimeout(() => {
          setModalOpen(false);
        }, 1000);
      }
    });
    client?.subscribe(FIRST_ROUND_LAUGH_URI, (message) => {
      console.log('1라운드 결과: ', message.body);
      setFirstRoundResult(message.body);
      subscribeSecondGame();
      setModalOpen(true);

      // 1초 후 modalOpen를 false로 설정
      setTimeout(() => {
        setModalOpen(false);
      }, 1000);
    });

    // error
    client?.subscribe(ERROR_URI, (message) => {
      console.log('error: ', message);
    });

    // 게임 결과
    client?.subscribe(RESULT_URI, (message) => {
      console.log(message.body);
      if (message.body) {
        dispatch(saveGameResult(message.body));
        router.push('/game/result');
      }
    });

    // 표정 분석 결과
    client?.subscribe(STATUS_URI, (message) => {
      console.log('표정인식 결과: ', message.body);
    });
    firstRoundStart();
  };

  // 2라운드 관련 구독
  const subscribeSecondGame = () => {
    client?.subscribe(SECOND_ROUND_GO_URI, (message) => {
      console.log('2라운드 메세지: ', message.body);
      if (message.body == 'START') {
        console.log('2라운드 시작');
        setRound(2);
        if (turn === 'FIRST') {
          intervalId = setInterval(captureAndSend, 1000);
        }
      }
    });
    client?.subscribe(SECOND_ROUND_NO_LAUGH_URI, (message) => {
      console.log('2라운드 결과: ', message.body);
      setSecondRoundResult(message.body);
      setWhen('END');
      clearInterval(intervalId);
      setModalOpen(true);
      gameEnd();
      setTimeout(() => {
        setModalOpen(false);
      }, 1000);
    });
    client?.subscribe(SECOND_ROUND_LAUGH_URI, (message) => {
      console.log('2라운드 결과: ', message.body);
      setSecondRoundResult(message.body);
      setWhen('END');
      clearInterval(intervalId);
      setModalOpen(true);
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
      console.log('1라운드 시작한다고 메세지 보낼거임');
      console.log(gameRoomID);
      publishMessage(client, FIRST_ROUND_START_URI, gameRoomID);
    }
  };

  // 2라운드 시작
  const secondRoundStart = () => {
    if (client) {
      console.log('2라운드 시작한다고 메세지 보낼거임');
      publishMessage(client, SECOND_ROUND_START_URI, gameRoomID);
    }
  };

  // 게임 종료
  const gameEnd = () => {
    if (client) {
      console.log('게임 종료 메세지 보낼거임');
      publishMessage(client, END_URI, gameRoomID);
    }
  };
  // 웹소켓 관련
  const [ws, setWs] = useState<WebSocket | null>(null);
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  // OpenVidu
  // eslint-disable-next-line
  const [OV, setOV] = useState<OpenVidu>();
  const [OVSession, setOVSession] = useState<Session>();
  const [publisher, setPublisher] = useState<Publisher>();
  const [subscriber, setSubscriber] = useState<Subscriber>();
  const currentVideoDeviceRef = useRef<Device>();

  // 오픈비두 연결
  const connectOV = () => {
    initGame().then(({ OV, session }) => {
      // console.log("THE FIRST OV INItialte");
      setOV(OV);
      setOVSession(session);
      session.on('streamCreated', (event) => {
        const ySubscriber = session.subscribe(event.stream, undefined);
        setSubscriber(ySubscriber);
      });

      //연결
      joinSession(OV, session, openviduToken, gameInfo.myInfo.nickname)
        .then(({ publisher, currentVideoDevice }) => {
          console.log('받은 퍼블리셔: ', publisher);
          setPublisher(publisher);
          currentVideoDeviceRef.current = currentVideoDevice;
          console.log('OpenVidu 연결 완료');
        })
        .catch((error) => {
          console.log('OpenVidu 연결 실패', error.code, error.message);
        });
      console.log('초반퍼블리셔: ', publisher);
    });
  };

  // 웹캠 이미지 캡쳐 및 전송
  const capturePublisherScreen = () => {
    console.log(2);
    console.log('퍼블리셔: ', publisher);
    console.log('Ref: ', canvasRef.current);

    if (publisher && canvasRef.current) {
      console.log(3);
      const videoElement = publisher.videos[0].video;
      const canvas = canvasRef.current;
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

  const sendCapturedImage = (imageData: any) => {
    console.log(5);
    if (ws && ws.readyState === WebSocket.OPEN) {
      const message = {
        image: imageData,
        header: {
          round: round, // 라운드 정보 설정
          gameSessionId: gameRoomID, // 게임 세션 ID 설정
        },
      };
      console.log('이미지 보낸다');
      ws.send(JSON.stringify(message));
    }
  };
  const captureAndSend = () => {
    console.log(1);
    const capturedImage = capturePublisherScreen();
    if (capturedImage) {
      console.log(4);
      sendCapturedImage(capturedImage);
    }
  };

  useEffect(() => {
    // WebSocket 연결
    const PYTHON_URL = process.env.NEXT_PUBLIC_PYTHON_URL;
    const websocket = new WebSocket(`${PYTHON_URL}`);
    setWs(websocket);
    websocket.onopen = () => console.log('WebSocket 연결됨');
    websocket.onmessage = (event) => console.log('서버로부터 메세지 받음:', event.data);
    websocket.onerror = (error) => console.log('WebSocket 에러:', error);
    websocket.onclose = () => console.log('WebSocket 연결 종료됨');
    connectOV();

    const alertTimeout = setTimeout(() => {
      console.log(turn);
      subscribeFirstGame();
    }, 2000);

    return () => {
      clearTimeout(alertTimeout);
      setModalOpen(false);
    };
  }, []);

  return (
    <div className="w-screen h-screen max-w-[500px] min-h-[565px] bg-black">
      <Header headerType="GAME" />
      {modalOpen && <GameStateModal when={when} gameState={turn} roundResult={firstRoundResult} />}
      <UserVideoComponent ref={childRef} streamManager={publisher} />
      <UserVideoComponent streamManager={subscriber} />
      <canvas ref={canvasRef} width="640" height="480" style={{ display: 'none' }} />
    </div>
  );
};

export default PlayPage;
