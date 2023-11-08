import { MouseEvent } from 'react';

type FuncButtonProps = {
  value: string;
  isBlue?: boolean;
  small?: boolean;
  clickEvent: (e: MouseEvent<HTMLButtonElement>) => void;
};

export const FuncButton = ({
  value,
  isBlue = false,
  small = false,
  clickEvent,
}: FuncButtonProps) => {
  const bgColor = isBlue
    ? 'bg-main-blue'
    : 'bg-gradient-to-b from-start-yellow to-end-yellow border-zinc-800 shadow-[inset_0_-2px_4px] shadow-[#FFA73F]';
  const fontColor = isBlue ? 'text-white' : 'text-zinc-800';
  const size = small ? 'w-20' : 'w-4/5';
  return (
    <button
      onClick={clickEvent}
      className={`${bgColor} rounded-lg border-2 max-w-xs py-5 hover:brightness-110 ${size}`}
    >
      <p className={`${fontColor} text-center text-base font-bold uppercase leading-none`}>
        {value}
      </p>
    </button>
  );
};
