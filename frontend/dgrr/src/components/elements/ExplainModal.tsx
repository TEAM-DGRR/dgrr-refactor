import { IoCloseOutline } from 'react-icons/io5';

export const ExplainModal = ({ onClose }: { onClose: () => void }) => {
  const modalInClick = (event: React.MouseEvent<HTMLDivElement>) => {
    event.stopPropagation();
  };

  return (
    <div
      className='z-10 bg-black/30 w-full h-full max-w-[500px] fixed top-0 flex justify-center items-center'
      onClick={onClose}
    >
      <div
        className='w-4/5 h-fit max-h-[600px] overflow-scroll bg-white rounded-lg border-2 border-black px-4 pb-4 overflow-x-hidden'
        onClick={modalInClick}
      >
        <div className='flex justify-between sticky top-0 bg-white pt-4'>
          <p className='w-8'></p>
          <p className='font-bold text-2xl'>게임 설명</p>
          <button className='cursor-xhover'>
            <IoCloseOutline onClick={onClose} fontSize={'32px'} />
          </button>
        </div>
        <div className='mt-3'>
          <p>안녕하세요! 저희는 1:1 실시간 웃음 참기 서비스 데구르르 입니다.</p>
          <br />
          <p>&quot;상대방을 웃겨라&quot;</p>
          <br />
          <ul>
            <li>
              1. 2라운드로 진행됩니다. 각 라운드별로 공격자와 수비자가 정해지며 라운드가 전환되면
              공격과 수비자가 바뀌게 됩니다.
            </li>
            <li>2. 공격턴에는 상대방을 웃게 만들면 됩니다.</li>
            <li>3. 수비턴에는 상대방의 웃음 공격을 참아야합니다. </li>
            <li>4. 수비자가 웃을 경우 라운드가 전환됩니다. </li>
            <li>
              5. 각 턴은 30초간 진행되며, 수비 측에서 30초간 웃음을 참는다면 라운드가 전환 됩니다.
            </li>
            <li>
              6. 두 라운드 결과를 취합하여 결과가 판정됩니다.
              <ul>
                <li>
                  A. 한쪽 플레이어만 웃은 경우 해당 플레이어를 웃게 만든 공격자가 승리하게 됩니다.
                </li>
                <li>B. 두 플레이어 모두 웃지 않은 경우, 무승부로 처리 됩니다 </li>
                <li>C. 두 플레이어 모두 웃은 경우, 더 빠르게 웃은 플레이어가 패배합니다.</li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
};
