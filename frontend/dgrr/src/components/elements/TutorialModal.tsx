import { useState } from 'react';
import { IoCloseOutline, IoChevronBackOutline, IoChevronForward } from 'react-icons/io5';

export const TutorialModal = ({ onClose }: { onClose: () => void }) => {
  const [currentImage, setCurrentImage] = useState(1);
  const totalImages = 5; // 총 이미지 수를 정의

  const modalInClick = (event: React.MouseEvent<HTMLDivElement>) => {
    event.stopPropagation();
  };

  const handleNext = () => {
    setCurrentImage((prev) => (prev < totalImages ? prev + 1 : prev));
  };

  const handlePrev = () => {
    setCurrentImage((prev) => (prev > 1 ? prev - 1 : prev));
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
          <p className='font-bold text-2xl'>튜토리얼</p>
          <button className='cursor-hover'>
            <IoCloseOutline onClick={onClose} fontSize={'32px'} />
          </button>
        </div>
        <div className='mt-3'>
          <div className='flex justify-between items-center mt-4'>
            <div
              className={`${
                currentImage === 1 ? 'invisible' : ''
              } p-2 focus:outline-none cursor-hover hover:text-main-blue`}
              onClick={handlePrev}
            >
              <IoChevronBackOutline fontSize={'30px'} />
            </div>
            <p className='text-sm font-semibold'>
              <span>{`${currentImage}/${totalImages}`}</span>
            </p>
            <div
              className={`${
                currentImage === 5 ? 'invisible' : ''
              } p-2 focus:outline-none cursor-hover hover:text-main-blue`}
              onClick={handleNext}
            >
              <IoChevronForward fontSize={'30px'} />
            </div>
          </div>
          <img className='' alt='역할 이미지' src={`/images/tutorial_${currentImage}.png`} />
          <br />
        </div>
      </div>
    </div>
  );
};
