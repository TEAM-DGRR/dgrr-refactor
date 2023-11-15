'use client';
import vsImage from '@/../public/images/vs-image.png';
import { initGame, joinSession } from '@/components/Game/openVidu';
import {
  saveOV,
  saveOVSession,
  savePublisher,
  saveSubscriber,
  saveWebsocket,
} from '@/store/gameSlice';
import { useAppSelector } from '@/store/hooks';
import { Image } from 'next/dist/client/image-component';
import { useRouter } from 'next/navigation';
import { Device, Publisher, Subscriber } from 'openvidu-browser';
import { useEffect, useRef, useState } from 'react';
import { useDispatch } from 'react-redux';

const matchAttack = '/images/match-attack.png';
const matchDefense = '/images/match-defense.png';

import './match.scss';
import Toast from '@/components/elements/Toast';

const MatchPage = () => {
  const [publisher, setPublisher] = useState<Publisher>();
  const [subscriber, setSubscriber] = useState<Subscriber>();
  const currentVideoDeviceRef = useRef<Device>();
  const openviduToken = useAppSelector((state) => state.game.gameInfo.openviduToken);
  const gameInfo = useAppSelector((state) => state.game.gameInfo);
  const dispatch = useDispatch();
  const router = useRouter();

  const roleMessages = ['상대를 웃겨보세요!', '웃음을 참아보세요!'];
  const roleImages = [matchAttack, matchDefense];

  const [roleMessage, setRoleMessage] = useState('');
  const [roleImage, setRoleImage] = useState('');

  const [seconds, setSeconds] = useState(0);
  const audioRef = useRef<HTMLAudioElement | null>(null);

  useEffect(() => {
    // 카메라 및 마이크 권한 체크
    const checkMediaPermissions = async () => {
      try {
        const cameraPermissionStatus = await navigator.permissions.query({
          name: 'camera' as PermissionName,
        });
        const microphonePermissionStatus = await navigator.permissions.query({
          name: 'microphone' as PermissionName,
        });

        if (
          cameraPermissionStatus.state !== 'granted' ||
          microphonePermissionStatus.state !== 'granted'
        ) {
          // console.log('미디어 접근 권한을 요청합니다.');
          await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
          // 요청 후 스트림 사용하지 않으므로 바로 닫음
        }
      } catch (error) {
        console.error('미디어 권한 상태 확인 중 오류 발생:', error);
      }
    };

    checkMediaPermissions();
  }, []);

  useEffect(() => {
    audioRef.current = new Audio('/audio/game-match.mp3');
    audioRef.current.play();
    const interval = setInterval(() => {
      setSeconds((prev) => prev + 1);
    }, 1000);
    return () => {
      // 페이지를 벗어날 때 오디오 정지
      audioRef.current?.pause();
      clearInterval(interval);
    };
  }, []);

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
    if (gameInfo.turn === 'FIRST') {
      setRoleMessage(roleMessages[0]);
      setRoleImage(roleImages[0]);
    } else {
      setRoleMessage(roleMessages[1]);
      setRoleImage(roleImages[1]);
    }
  }, [gameInfo.turn]); // gameInfo.turn이 변경될 때마다 업데이트

  // 오픈비두 연결
  const connectOV = () => {
    initGame().then(({ OV, session }) => {
      // console.log("THE FIRST OV INItialte");
      dispatch(saveOV(OV));
      dispatch(saveOVSession(session));
      session.on('streamCreated', (event) => {
        const ySubscriber = session.subscribe(event.stream, undefined);
        setSubscriber(ySubscriber);
        dispatch(saveSubscriber(ySubscriber));
      });

      //연결
      joinSession(OV, session, openviduToken, gameInfo.myInfo.nickname)
        .then(({ publisher, currentVideoDevice }) => {
          // console.log('받은 퍼블리셔: ', publisher);
          setPublisher(publisher);
          dispatch(savePublisher(publisher));
          currentVideoDeviceRef.current = currentVideoDevice;
          // console.log('OpenVidu 연결 완료');
          setTimeout(() => {
            router.push('/game/play');
          }, 4000);
        })
        .catch((error) => {
          console.log('OpenVidu 연결 실패', error.code, error.message);
        });
      // console.log('초반퍼블리셔: ', publisher);
    });
  };

  useEffect(() => {
    // WebSocket 연결
    const PYTHON_URL = process.env.NEXT_PUBLIC_PYTHON_URL;
    const websocket = new WebSocket(`${PYTHON_URL}`);
    dispatch(saveWebsocket(websocket));
    // websocket.onopen = () => console.log('WebSocket 연결됨');
    // websocket.onmessage = (event) => console.log('서버로부터 메세지 받음:', event.data);
    websocket.onerror = (error) => console.log('WebSocket 에러:', error);
    // websocket.onclose = () => console.log('WebSocket 연결 종료됨');

    connectOV();
  }, []);

  return (
    <div className='Container relative w-screen h-screen max-w-[500px] min-h-[580px] z-0 truncate'>
      <div className='Turn flex absolute items-center justify-center mt-5 mx-3 rounded-lg'>
        <img className='MatchedPersonProfile w-[20%] ' alt='역할 이미지' src={roleImage} />
        <div className='text-[#000000] font-black text-2xl'>
          <p>{roleMessage}</p>
        </div>
      </div>

      <div className='flex flex-col justify-between h-full'>
        {/* 상대 정보 */}

        <div className='flex flex-col flex-1 justify-end mx-6 my-0'>
          <div className='flex flex-1 items-center justify-center py-1 mx-3 my-4 rounded-lg'></div>
          <div className='MatchedPerson1 flex flex-1 relative items-end justify-center py-5 px-3 mx-3 my-2 rounded-lg'>
            <div className='MatchedPersonBackground1 min-h-[150px] z-30 '>
              <div className='ml-[38%] px-1 mt-3 text-left max-w-[240px] rounded-lg border-b-2 '>
                <p className='text-[10px] text-[#9cd4ab] truncate'>닉네임</p>
                <p className='text-[#fee691] text-[120%] truncate'>{gameInfo.enemyInfo.nickname}</p>
              </div>
              <div className='ml-[38%] px-1 mt-3 text-left max-w-[220px] rounded-lg border-b-2 '>
                <p className='text-[10px] text-[#9cd4ab] truncate'>상태메시지</p>
                <p className='text-base text-[#f2f2f2] truncate'>
                  {gameInfo.enemyInfo.description}
                </p>
              </div>
            </div>

            <img
              className='MatchedPersonProfile w-[50%] aspect-square mt-[2%] -left-[10%] absolute rounded-full border-0 border-cyan-500 z-50'
              alt='프로필 이미지'
              src={gameInfo.enemyInfo.profileImage}
            />
          </div>
        </div>

        {/* ------------------------------------------------------------------------------------------------------------- */}

        {/* vsImage */}
        <div className='flex flex- relative justify-center -mt-[65px] -mb-[50px]'>
          <div className='VersusImage w-[120px] z-40'>
            <Image alt='vs이미지' src={vsImage} />
          </div>
        </div>
        {/* ------------------------------------------------------------------------------------------------------------- */}

        {/* 내 정보 */}
        <div className='flex flex-col flex-1 justify-end mx-6 my-0'>
          <div className='MatchedPerson2 flex flex-1 relative items-start justify-center py-5 px-2 mx-3 my-2 rounded-lg'>
            <div className='MatchedPersonBackground2 min-h-[150px]  z-30 '>
              <div className='ml-2 px-1 mt-3 text-start max-w-[240px] truncate rounded-lg border-b-2 '>
                <p className='text-[10px] text-[#9cd4ab] truncate'>닉네임</p>
                <p className='text-[#fee691] text-lg truncate'>{gameInfo.myInfo.nickname}</p>
              </div>
              <div className='ml-2 px-1 mt-3 text-start max-w-[220px] truncate rounded-lg border-b-2 '>
                <p className='text-[10px] text-[#9cd4ab] truncate'>상태메시지</p>
                <p className='text-base text-[#f2f2f2] truncate'>{gameInfo.myInfo.description}</p>
              </div>
            </div>

            <img
              className='MatchedPersonProfile w-[50%] aspect-square mt-[1%] ml-[60%] absolute rounded-full border-0 border-cyan-500 z-50'
              alt='프로필 이미지'
              src={gameInfo.myInfo.profileImage}
            />
          </div>

          <div className='flex flex-1 items-center justify-center bg-match-versus py-1 mx-3 my-4 rounded-lg'></div>
        </div>
      </div>
    </div>
  );
};

export default MatchPage;
