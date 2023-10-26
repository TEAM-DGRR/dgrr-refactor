"use client";
import character from "@/../public/images/logo_character.png";
import title from "@/../public/images/logo_title.png";
import Image from "next/image";
import { LinkButton } from "@/components/LinkButton";
import { FuncButton } from "@/components/FuncButton";
import Header from "@/components/elements/Header";

const MainPage = () => {
  // 나중에 설명 모달 on/off 로직 추가할 예정
  const handleModal = () => {};
  return (
    <div className="flex flex-col items-center w-screen bg-main-blue">
      <Header headerType="MAIN" />
      <Image alt="캐릭터" src={character} className="w-40 h-40" />
      <Image alt="타이틀" src={title} className="w-48" />
      <div className="flex flex-col w-full items-center space-y-6">
        <FuncButton value="게임 설명" clickEvent={handleModal} />
        <LinkButton moveLink="game/list" value="게임 시작" />
      </div>
    </div>
  );
};

export default MainPage;
