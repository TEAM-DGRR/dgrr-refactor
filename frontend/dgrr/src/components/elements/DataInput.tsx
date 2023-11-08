'use client';

import { useEffect, useState } from 'react';

export type inputType = 'NICKNAME' | 'DESCRIPTION';
export type pageType = 'SIGNUP' | 'PROFILE_EDIT';

interface DataInputProps {
  inputType: inputType;
  pageType: pageType;
  value?: string;
  nicknameExists?: boolean;
  onChange?: any;
}

const DataInput = ({ inputType, pageType, value, nicknameExists, onChange }: DataInputProps) => {
  return (
    <div className="px-6 mb-7">
      {inputType === 'NICKNAME' ? (
        <div>
          <div className="mb-2 font-semibold">닉네임</div>
          <div className="text-xs text-[#767676] mb-[10px]">
            한글/영어/숫자 최소 2자~최대 12자 가능
          </div>
          {pageType === 'SIGNUP' ? (
            // 닉네임 - 회원가입
            <div>
              <input
                type="text"
                onChange={onChange}
                minLength={2}
                maxLength={12}
                className="bg-[#F4F4F6] w-full text-xs p-4 rounded-lg focus:outline-none focus:ring focus:ring-main-blue"
                placeholder="닉네임을 입력해주세요."
              />
            </div>
          ) : (
            // 닉네임 - 프로필 수정
            <div>
              <input
                type="text"
                value={value}
                onChange={onChange}
                minLength={2}
                maxLength={12}
                className="bg-[#F4F4F6] w-full text-xs p-4 rounded-lg focus:outline-none focus:ring focus:ring-main-blue"
              />
            </div>
          )}
          {/* 닉네임이 중복되면 경고 문구 뜸 */}
          {nicknameExists === true ? (
                <div className="text-xs h-4 pt-2 ms-1 text-red-500">이미 존재하는 닉네임입니다</div>
              ) : (
                <div className="text-xs h-4 pt-2"></div>
              )}
        </div>
      ) : (
        <div>
          <div className="font-semibold mb-2">상태 메시지</div>
          {pageType === 'SIGNUP' ? (
            // 상태 메시지 - 회원가입
            <div>
              <input
                type="text"
                onChange={onChange}
                maxLength={20}
                className="bg-[#F4F4F6] w-full text-xs p-4 rounded-lg focus:outline-none focus:ring focus:ring-main-blue"
                placeholder="상태 메시지를 입력해주세요."
              />
            </div>
          ) : (
            // 상태 메시지 - 프로필 수정
            <input
              type="text"
              value={value}
              onChange={onChange}
              maxLength={20}
              className="bg-[#F4F4F6] w-full text-xs p-4 rounded-lg focus:outline-none focus:ring focus:ring-main-blue"
            />
          )}
        </div>
      )}
    </div>
  );
};

export default DataInput;
