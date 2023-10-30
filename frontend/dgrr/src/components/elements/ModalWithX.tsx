'use client';

import { IoCloseOutline } from 'react-icons/io5';

interface ModalWithXProps {
  modalStatus: boolean;
  closeModal: () => void;
  item: {
    gameDetailId: number;
    gameRoomId: number;
    gameResult: string;
    gameType: string;
    gameTime: number;
    holdingTime: number;
    laughAmount: number;
    highlightImage: string;
    opponentNickname: string;
    opponentProfileImage: string;
    opponentDescription: string;
  };
}

const ModalWithX = ({ modalStatus, closeModal, item }: ModalWithXProps) => {
  return (
    <div>
      {modalStatus === true ? (
        <div className='z-10 bg-black/30 w-full h-full max-w-[360px] fixed top-0 flex justify-center items-center'>
          <div className='w-80 h-fit bg-white rounded-lg border-2 border-black p-3'>
            <div className='flex justify-end mb-1' onClick={closeModal}>
              <button className='hover:text-[#E83F57]'>
                <IoCloseOutline fontSize={'24px'} />
              </button>
            </div>
            <img className='max-w-[290px]' src={item.highlightImage} />
          </div>
        </div>
      ) : null}
    </div>
  );
};

export default ModalWithX;
