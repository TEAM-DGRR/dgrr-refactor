import Link from 'next/link';

type ButtonProps = {
  value: string;
  small?: boolean;
  moveLink: string;
  clickEvent?: () => void;
};

export const LinkButton = ({ value, small = false, moveLink, clickEvent}: ButtonProps) => {
  const size = small ? 'w-20' : 'w-4/5';
  return (
    <Link
      href={moveLink}
      className={`text-center text-base font-bold uppercase leading-none text-zinc-800 max-w-xs ${size} cursor-hover`}
      onClick={clickEvent}
    >
      <div
        className={`shadow-[inset_0_-2px_4px] shadow-[#FFA73F] rounded-lg border-2 py-5 hover:brightness-110 bg-gradient-to-b from-start-yellow to-end-yellow border-zinc-800`}
      >
        {value}
      </div>
    </Link>
  );
};
