'use client';

import React, { useState, useRef, useEffect } from 'react';

interface CodeInputProps {
  length: number;
  onChange: (code: string) => void;
  onComplete?: (code: string) => void;
  disabled?: boolean;
}

const CodeInput: React.FC<CodeInputProps> = ({ length, onChange, onComplete, disabled }) => {
  const [code, setCode] = useState<string[]>(Array(length).fill(''));
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

  useEffect(() => {
    // 첫 번째 입력 필드에 포커스
    inputRefs.current[0]?.focus();
  }, []);

  const handleChange = (value: string, index: number) => {
    const newCode = [...code];
    newCode[index] = value;
    setCode(newCode);
    onChange(newCode.join(''));

    // 다음 입력 필드로 이동
    if (value && index < length - 1) {
      inputRefs.current[index + 1]?.focus();
    }

    // 모든 입력이 완료되면 검증
    if (newCode.every((digit) => digit !== '') && onComplete) {
      onComplete(newCode.join(''));
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, index: number) => {
    // Tab 키 처리
    if (e.key === 'Tab' && !e.shiftKey) {
      e.preventDefault();
      const nextIndex = index + 1;
      if (nextIndex < length) {
        inputRefs.current[nextIndex]?.focus();
      }
    }
    // Backspace 처리
    if (e.key === 'Backspace' && !code[index] && index > 0) {
      inputRefs.current[index - 1]?.focus();
    }
  };

  return (
    <div className="flex justify-center space-x-2">
      {code.map((digit, index) => (
        <input
          key={index}
          ref={(el) => {
            inputRefs.current[index] = el;
          }}
          type="text"
          maxLength={1}
          value={digit}
          onChange={(e) => handleChange(e.target.value, index)}
          onKeyDown={(e) => handleKeyDown(e, index)}
          className="h-12 w-12 rounded-md border border-gray-300 text-center text-xl focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          disabled={disabled}
        />
      ))}
    </div>
  );
};

export default CodeInput;
