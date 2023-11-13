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
          console.log('미디어 접근 권한을 요청합니다.');
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
      console.log('초반퍼블리셔: ', publisher);
    });
  };

  useEffect(() => {
    // WebSocket 연결
    const PYTHON_URL = process.env.NEXT_PUBLIC_PYTHON_URL;
    const websocket = new WebSocket(`${PYTHON_URL}`);
    dispatch(saveWebsocket(websocket));
    websocket.onopen = () => console.log('WebSocket 연결됨');
    websocket.onmessage = (event) => console.log('서버로부터 메세지 받음:', event.data);
    websocket.onerror = (error) => console.log('WebSocket 에러:', error);
    websocket.onclose = () => console.log('WebSocket 연결 종료됨');

    connectOV();
  }, []);

  return (
    <div className='Container relative w-screen h-screen h-screen min-w-[500px] max-w-[500px] min-h-[565px] z-0 truncate'>
      <div className='Turn flex absolute items-center justify-center bg-match-versus mt-10 mx-3 m-2 rounded-lg'>
        <img
          className='MatchedPersonProfile w-[20%] rounded-full'
          alt='역할 이미지'
          src={roleImage}
        />
        <div className='text-[30px] text-[#000000] font-black'>
          <p>{roleMessage}</p>
        </div>
      </div>

      <div className='flex flex-col justify-between h-full'>
        {/* 상대 정보 */}

        <div className='flex flex-col flex-1 justify-end bg-match-white mx-6 my-0'>
          <div className='flex flex-1 items-center justify-center bg-match-versus py-6 mx-3 my-4 rounded-lg'></div>
          <div className='MatchedPerson1 flex flex-1 relative items-end justify-center bg-match-versus py-5 px-3 mx-3 my-2 rounded-lg'>
            <div className='MatchedPersonBackground1 min-h-[150px] min-x-[400px] z-30'>
              <div className='absolute text-[10px] text-[#9cd4ab] mt-4 ml-[85%] mb-[50px]'>
                닉네임
              </div>

              <div className='w-[240px] px-2 mt-7 ml-[150px] text-right text-[#fee691] text-[18px] rounded-lg border-b-2 '>
                <p>{gameInfo.enemyInfo.nickname}</p>
              </div>
              <div className='absolute text-[10px] text-[#9cd4ab] mt-3 ml-[80%] mb-[50px]'>
                상태메시지
              </div>
              <div className='ml-[170px] px-2 min-w-[150px] text-right max-w-[220px] text-[14px] text-[#f2f2f2] mt-6rounded-lg border-b-2 '>
                <p>{gameInfo.enemyInfo.description}</p>
              </div>
            </div>

            <img
              className='MatchedPersonProfile w-[160px] mb-[2%] mr-[60%] absolute rounded-full border-0 border-cyan-500 z-50'
              alt='프로필 이미지'
              src={gameInfo.enemyInfo.profileImage}
            />
          </div>
        </div>

        {/* ------------------------------------------------------------------------------------------------------------- */}

        {/* vsImage */}
        <div className='VersusImage absolute w-[140px] left-[37.5%] top-[38%] z-40'>
          <Image alt='vs이미지' src={vsImage} />
        </div>
        {/* ------------------------------------------------------------------------------------------------------------- */}

        {/* 내 정보 */}
        <div className='flex flex-col flex-1 justify-end bg-match-white mx-6 my-0'>
          <div className='MatchedPerson2 flex flex-1 relative items-start justify-center bg-match-versus py-5 px-2 mx-3 my-2 rounded-lg'>
            <div className='MatchedPersonBackground2 min-h-[150px] min-x-[400px] z-30'>
              <div className='absolute text-[10px] text-[#9cd4ab] mt-4 ml-5'>닉네임</div>
              <div className='w-[240px] pl-2 mt-7 ml-4 text-[#fee691] text-[18px] rounded-lg border-b-2 '>
                <p>{gameInfo.myInfo.nickname}</p>
              </div>
              <div className='absolute text-[10px] text-[#9cd4ab] mt-3 ml-5'>상태메시지</div>
              <div className='min-w-[150px] max-w-[220px] text-[14px] text-[#f2f2f2] pl-2 mt-6 ml-4 rounded-lg border-b-2 '>
                <p>{gameInfo.myInfo.description}</p>
              </div>
            </div>
            <img
              className='MatchedPersonProfile w-[160px] mt-[2%] ml-[52%] absolute rounded-full border-0 border-cyan-500 z-50'
              alt='프로필 이미지'
              src={gameInfo.myInfo.profileImage}
            />
          </div>

          <div className='flex flex-1 items-center justify-center bg-match-versus py-6 mx-3 my-4 rounded-lg'></div>
        </div>
      </div>
    </div>
  );
};

export default MatchPage;
