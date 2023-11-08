'use client';

import { IoCloseOutline } from 'react-icons/io5';

interface ModalWithXProps {
  modalStatus: boolean;
  closeModal: () => void;
  item?: {
    gameHistoryId: number;
    gameRoomId: number;
    gameResult: string;
    gameType: string;
    gameTime: number;
    holdingTime: number;
    ratingChange: number;
    highlightImage: string;
    opponentNickname: string;
    opponentProfileImage: string;
    opponentDescription: string;
  };
  children?: React.ReactNode;
}

const ModalWithX = ({ modalStatus, closeModal, item, children }: ModalWithXProps) => {
  return (
    <div>
      {modalStatus === true ? (
        <div className="z-10 bg-black/30 w-screen h-full max-w-[500px] fixed top-0 flex justify-center items-center">
          <div className="w-80 h-fit bg-white rounded-lg border-2 border-black p-3">
            <div className="flex justify-end mb-1" onClick={closeModal}>
              <button className="hover:text-[#E83F57]">
                <IoCloseOutline fontSize={'24px'} />
              </button>
            </div>
            {item !== undefined ? (
              <img className="max-w-[290px]" src={item.highlightImage} />
            ) : (
              <div>{children}</div>
            )}
          </div>
        </div>
      ) : null}
    </div>
  );
};

export default ModalWithX;
