import Link from "next/link";

type ButtonProps = {
  value: string;
  small?: boolean;
  moveLink: string;
};

export const LinkButton = ({ value, small = false, moveLink }: ButtonProps) => {
  const size = small ? "w-20" : "w-4/5";
  return (
    <Link
      href={moveLink}
      className={`text-center text-base font-bold uppercase leading-none text-zinc-800`}
    >
      <div
        className={`rounded-lg border-2 max-w-xs py-5 hover:brightness-110 bg-gradient-to-b from-start-yellow to-end-yellow border-zinc-800 ${size}`}
      >
        {value}
      </div>
    </Link>
  );
};
