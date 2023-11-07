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
  } = DESTINATION_URI;
  const [firstRoundResult, setFirstRoundResult] = useState('');
  const [secondRoundResult, setSecondRoundResult] = useState('');
  const childRef = useRef<ChildMethods | null>(null);
  const dispatch = useDispatch();

  const subscribeGame = () => {
    // 1라운드 관련 구독
    client?.subscribe(FIRST_ROUND_GO_URI, (message) => {
      console.log('1라운드 메세지: ', message.body);
      if (message.body == 'START') {
        console.log('1라운드 시작');
      }
    });
    client?.subscribe(FIRST_ROUND_NO_LAUGH_URI, (message) => {
      console.log('1라운드 메세지: ', message.body);
      setFirstRoundResult(message.body);
    });
    client?.subscribe(FIRST_ROUND_LAUGH_URI, (message) => {
      console.log('1라운드 메세지: ', message.body);
      setFirstRoundResult(message.body);
    });

    // 2라운드 관련 구독
    client?.subscribe(SECOND_ROUND_GO_URI, (message) => {
      console.log('2라운드 메세지: ', message.body);
      if (message.body == 'START') {
        console.log('2라운드 시작');
      }
    });
    client?.subscribe(SECOND_ROUND_NO_LAUGH_URI, (message) => {
      console.log('2라운드 메세지: ', message);
      setSecondRoundResult(message.body);
    });
    client?.subscribe(SECOND_ROUND_LAUGH_URI, (message) => {
      console.log('2라운드 메세지: ', message);
      setSecondRoundResult(message.body);
    });

    // error
    client?.subscribe(ERROR_URI, (message) => {
      console.log('error: ', message);
    });

    // 게임 결과
    client?.subscribe(RESULT_URI, (message) => {
      dispatch(saveGameResult(message.body));
    });
  };

  // 1라운드 시작
  const firstRoundStart = () => {
    if (client) {
      publishMessage(client, FIRST_ROUND_START_URI, gameRoomID);
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
          setPublisher(publisher);
          currentVideoDeviceRef.current = currentVideoDevice;
          console.log('OpenVidu 연결 완료');
        })
        .catch((error) => {
          console.log('OpenVidu 연결 실패', error.code, error.message);
        });
    });
  };

  // 이미지 처리]
  const startWebcamCapture = useRef<NodeJS.Timer>();
  const webcamCapture = useRef<() => void>(() => {});
  useEffect(() => {
    connectOV();
  }, []);
  useEffect(() => {
    // WebSocket 연결
    const PYTHON_URL = process.env.NEXT_PUBLIC_PYTHON_URL;
    const websocket = new WebSocket(`${PYTHON_URL}`);
    setWs(websocket);
    websocket.onopen = () => console.log('WebSocket 연결됨');
    websocket.onmessage = (event) => console.log('서버로부터 메세지 받음:', event.data);
    websocket.onerror = (error) => console.log('WebSocket 에러:', error);
    websocket.onclose = () => console.log('WebSocket 연결 종료됨');
    // 웹캠 이미지 캡쳐 및 전송
    const captureAndSend = () => {
      if (canvasRef.current && videoRef.current) {
        const ctx = canvasRef.current.getContext('2d');
        if (ctx) {
          ctx.drawImage(videoRef.current, 0, 0, 640, 480); // 여기서 640x480은 캡처할 이미지의 크기입니다. 원하는대로 조절하세요.
          const dataURL = canvasRef.current.toDataURL('image/jpeg');
          if (websocket.readyState === WebSocket.OPEN) {
            const message = {
              image: dataURL,
              header: {
                round: 1,
                gameSessionId: '12345',
              },
            };
            websocket.send(JSON.stringify(message));
          }
        }
      }
    };
    const intervalId = setInterval(captureAndSend, 1000);

    const alertTimeout = setTimeout(() => {
      console.log(turn);
      const myState = turn == 'FIRST' ? '공격' : '방어';
      alert(myState);
      firstRoundStart();
    }, 3000);

    return () => {
      clearInterval(intervalId);
      clearTimeout(alertTimeout);
      subscribeGame();
      // if (websocket) {
      //   websocket.close();
      // }
    };
  }, [turn, gameRoomID, gameInfo]);

  useEffect(() => {
    if (firstRoundResult && client) {
      publishMessage(client, SECOND_ROUND_START_URI, gameRoomID);
    }
  }, [firstRoundResult]);
  return (
    <div>
      <UserVideoComponent ref={childRef} streamManager={publisher} />
      <UserVideoComponent streamManager={subscriber} />
      {/* <video ref={videoRef} width="640" height="480" autoPlay muted /> */}
      <canvas ref={canvasRef} width="640" height="480" style={{ display: 'none' }} />
    </div>
  );
};

export default PlayPage;
